/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * 09/Apr/08 Dale Thatcher <openintents at dalethatcher dot com>
 *           Added wii-mote data collection.
 */

package org.openintents.tools.simulator.view.sensor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.view.sensor.sensors.AccelerometerView;
import org.openintents.tools.simulator.view.sensor.sensors.BarcodeReaderView;
import org.openintents.tools.simulator.view.sensor.sensors.GravityView;
import org.openintents.tools.simulator.view.sensor.sensors.GyroscopeView;
import org.openintents.tools.simulator.view.sensor.sensors.LightView;
import org.openintents.tools.simulator.view.sensor.sensors.LinearAccelerationView;
import org.openintents.tools.simulator.view.sensor.sensors.MagneticFieldView;
import org.openintents.tools.simulator.view.sensor.sensors.OrientationView;
import org.openintents.tools.simulator.view.sensor.sensors.PressureView;
import org.openintents.tools.simulator.view.sensor.sensors.ProximityView;
import org.openintents.tools.simulator.view.sensor.sensors.RotationVectorView;
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;
import org.openintents.tools.simulator.view.sensor.sensors.TemperatureView;

/**
 * 
 * SensorSimulatorView keeps the GUI of the SensorSimulator.
 * 
 * SensorSimulator simulates various sensors. An Android application can connect
 * through TCP/IP with the settings shown to the SensorSimulator to simulate
 * accelerometer, compass, orientation sensor, and thermometer.
 * 
 * @author Peli
 * @author Josip Balic
 * @author ilarele
 */
public class SensorSimulatorView extends JPanel {
	private static final long serialVersionUID = -587503580193069930L;

	// port for sensor simulation
	private JTextField mSensorPortText;
	private JButton mSensorPortButton;

	// Field for socket related output:
	// private JScrollPane areaScrollPane;
	private JTextPane mMessageTextArea;

	// Field for sensor simulator data output:
	private JTextArea mTextAreaSensorData;

	// Settings
	private JTextField mUpdateText;
	private JTextField mRefreshCountText;
	private JLabel mRefreshSensorsLabel;

	private SensorSimulatorModel mModel;
	private ArrayList<SensorView> mSensors;

	private JTabbedPane mSensorsTabbedPane;

	private AllSensorsView mAllSensorsTab;

	private DeviceView mMobile;

	public SensorSimulatorView(SensorSimulatorModel model) {
		this.mModel = model;
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		// sensors
		mSensors = new ArrayList<SensorView>();
		mSensors.add(new AccelerometerView(model.getAccelerometer()));
		mSensors.add(new MagneticFieldView(model.getMagneticField()));
		mSensors.add(new OrientationView(model.getOrientation(), model));
		mSensors.add(new TemperatureView(model.getTemperature()));
		mSensors.add(new BarcodeReaderView(model.getBarcodeReader()));
		mSensors.add(new LightView(model.getLight()));
		mSensors.add(new ProximityView(model.getProximity()));
		mSensors.add(new PressureView(model.getPressure()));
		mSensors.add(new LinearAccelerationView(model.getLinearAcceleration()));
		mSensors.add(new GravityView(model.getGravity()));
		mSensors.add(new RotationVectorView(model.getRotationVector()));
		mSensors.add(new GyroscopeView(model.getGyroscope()));

		mMobile = new DeviceView(model);

		// up/down & split
		JPanel upPanel = fillUpPanel();
		JPanel downPanel = fillDownPanel();

		JSplitPane splitPaneVertical = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, upPanel, downPanel);
		splitPaneVertical.setResizeWeight(Global.SENSOR_SPLIT_UP);

		add(splitPaneVertical);
		layout.putConstraint(SpringLayout.WEST, splitPaneVertical, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, splitPaneVertical, 10,
				SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, splitPaneVertical, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, splitPaneVertical, 10,
				SpringLayout.SOUTH, this);
	}

	/**
	 * Fills the panel from the bottom split.
	 * 
	 * @return
	 */
	private JPanel fillDownPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel downPanel = new JPanel(layout);

		// Sensor output update/measure frequency
		JPanel updateSimulationPanel = fillUpdateSimulationPanel();
		downPanel.add(updateSimulationPanel);
		Dimension minSize = updateSimulationPanel.getPreferredSize();
		downPanel.setPreferredSize(new Dimension(minSize.width,
				minSize.height + 195));
		// info output
		JScrollPane areaScrollPane = fillInfoOutput();
		downPanel.add(areaScrollPane);

		// sensors log/values
		JScrollPane scrollPaneSensorData = fillTextArea();
		downPanel.add(scrollPaneSensorData);

		// updateSimulationPanel - left
		layout.putConstraint(SpringLayout.WEST, downPanel, 10,
				SpringLayout.WEST, updateSimulationPanel);
		layout.putConstraint(SpringLayout.NORTH, updateSimulationPanel, 10,
				SpringLayout.NORTH, downPanel);

		// areaScrollPane - center
		layout.putConstraint(SpringLayout.WEST, areaScrollPane, 10,
				SpringLayout.EAST, updateSimulationPanel);
		layout.putConstraint(SpringLayout.WEST, scrollPaneSensorData, 10,
				SpringLayout.EAST, areaScrollPane);
		layout.putConstraint(SpringLayout.NORTH, areaScrollPane, 10,
				SpringLayout.NORTH, downPanel);
		layout.putConstraint(SpringLayout.SOUTH, downPanel, 10,
				SpringLayout.SOUTH, areaScrollPane);

		// scrollPaneSensorData - right
		layout.putConstraint(SpringLayout.EAST, downPanel, 10,
				SpringLayout.EAST, scrollPaneSensorData);
		layout.putConstraint(SpringLayout.NORTH, scrollPaneSensorData, 10,
				SpringLayout.NORTH, downPanel);
		layout.putConstraint(SpringLayout.SOUTH, scrollPaneSensorData, -10,
				SpringLayout.SOUTH, downPanel);

		return downPanel;
	}

	private JScrollPane fillTextArea() {
		mTextAreaSensorData = new JTextArea(3, 10);
		JScrollPane scrollPaneSensorData = new JScrollPane(mTextAreaSensorData);
		scrollPaneSensorData
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneSensorData.setPreferredSize(new Dimension(350, 10));
		return scrollPaneSensorData;
	}

	/**
	 * Fills info panel with possible IPs.
	 * 
	 * @return
	 */
	private JScrollPane fillInfoOutput() {
		mMessageTextArea = new JTextPane();
		mMessageTextArea.setContentType("text/html");
		mMessageTextArea.setEditable(false);

		StringBuffer infoText = new StringBuffer();
		infoText.append("Write emulator command port and<br\\>"
				+ "click on set to create connection.<br\\>");
		infoText.append("Possible IP addresses:<br\\>");
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();
			infoText.append("<font color='red'>10.0.0.2</font><br\\>");
			for (NetworkInterface netint : Collections.list(nets)) {
				Enumeration<InetAddress> inetAddresses = netint
						.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					String address = inetAddress.toString();
					if (address.compareTo("/127.0.0.1") != 0
							&& !address.contains(":")) {
						infoText.append("<font color='red'>"
								+ address.substring(1) + "</font><br\\>");
					}
				}
			}
		} catch (SocketException e) {
			infoText.append("Socket exception. Could not obtain IP addresses.");
		}

		mMessageTextArea.setText(infoText.toString());

		JScrollPane areaScrollPane = new JScrollPane(mMessageTextArea);
		areaScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension(350, 10));
		return areaScrollPane;
	}

	private JPanel fillUpdateSimulationPanel() {
		JPanel leftDownPanel = new JPanel(new GridBagLayout());
		GridBagConstraints layout = new GridBagConstraints();
		layout.fill = GridBagConstraints.HORIZONTAL;
		layout.anchor = GridBagConstraints.NORTHWEST;

		// Add IP address properties:
		Font fontNotify = new Font("SansSerif", Font.BOLD, 12);
		JLabel socketLabel = new JLabel("Socket ", JLabel.LEFT);
		socketLabel.setFont(fontNotify);
		socketLabel.setForeground(Global.COLOR_NOTIFY);
		layout.gridx = 0;
		layout.gridy = 0;
		layout.gridwidth = 2;
		leftDownPanel.add(socketLabel);

		layout.gridx = 1;
		layout.gridy = 0;
		layout.gridwidth = 2;
		mSensorPortText = new JTextField(5);
		mSensorPortText.setText("" + mModel.getSimulationPort());
		leftDownPanel.add(mSensorPortText);

		mSensorPortButton = new JButton("Set");
		mSensorPortButton.setFont(fontNotify);
		mSensorPortButton.setForeground(Global.COLOR_NOTIFY);
		layout.gridx = 2;
		layout.gridy = 0;
		layout.gridwidth = 1;
		leftDownPanel.add(mSensorPortButton);

		// Update sensors
		JPanel updateSimulationPanel = new JPanel(new GridBagLayout());

		updateSimulationPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Simulation update"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		layout.gridwidth = 1;
		layout.gridy = 0;
		layout.gridx = 0;
		JLabel label = new JLabel("Update sensors: ", JLabel.LEFT);
		updateSimulationPanel.add(label, layout);

		mUpdateText = new JTextField(5);
		mUpdateText.setText("" + mModel.getUpdateSensors());
		layout.gridx++;
		updateSimulationPanel.add(mUpdateText, layout);

		label = new JLabel(" ms", JLabel.LEFT);
		layout.gridx++;
		updateSimulationPanel.add(label, layout);

		// Refresh after
		layout.gridy++;
		label = new JLabel("Refresh after: ", JLabel.LEFT);
		layout.gridx = 0;
		updateSimulationPanel.add(label, layout);

		mRefreshCountText = new JTextField(5);
		mRefreshCountText.setText("10");
		layout.gridx++;
		updateSimulationPanel.add(mRefreshCountText, layout);

		label = new JLabel(" times", JLabel.LEFT);
		layout.gridx++;
		updateSimulationPanel.add(label, layout);

		// Sensor update
		layout.gridy++;
		label = new JLabel("Sensor update: ", JLabel.LEFT);
		layout.gridx = 0;
		updateSimulationPanel.add(label, layout);

		mRefreshSensorsLabel = new JLabel("0", JLabel.LEFT);
		layout.gridx++;
		updateSimulationPanel.add(mRefreshSensorsLabel, layout);

		layout.gridx = 0;
		layout.gridy = 1;
		layout.gridwidth = 3;
		leftDownPanel.add(updateSimulationPanel, layout);
		return leftDownPanel;
	}

	private JPanel fillUpPanel() {
		JPanel upPanel = new JPanel(new BorderLayout());

		JScrollPane leftScrollPane = fillLeftPanel();
		mSensorsTabbedPane = fillRightPanel();

		JSplitPane splitUpPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftScrollPane, mSensorsTabbedPane);
		splitUpPane.setResizeWeight(Global.SENSOR_SPLIT_LEFT);

		upPanel.add(splitUpPane);
		return upPanel;
	}

	private JTabbedPane fillRightPanel() {
		JTabbedPane rightPanel = new JTabbedPane();
		mAllSensorsTab = new AllSensorsView(mSensors);
		rightPanel.addTab("Sensors", mAllSensorsTab);
		for (SensorView sensor : mSensors) {
			SensorModel sensorModel = sensor.getModel();
			if (sensorModel.isEnabled())
				rightPanel.addTab(sensorModel.getName(), sensor);
		}
		return rightPanel;
	}

	private JScrollPane fillLeftPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel leftPanel = new JPanel(layout);
		JScrollPane leftScrollPane = new JScrollPane(leftPanel);

		leftPanel.add(mMobile);

		JPanel quickSettings = fillQuickSettingsPanel();
		leftPanel.add(quickSettings);

		// container
		layout.putConstraint(SpringLayout.NORTH, mMobile, 10,
				SpringLayout.NORTH, leftPanel);
		layout.putConstraint(SpringLayout.SOUTH, leftPanel, 10,
				SpringLayout.SOUTH, quickSettings);

		// device
		layout.putConstraint(SpringLayout.WEST, mMobile, 0, SpringLayout.WEST,
				leftPanel);
		layout.putConstraint(SpringLayout.EAST, leftPanel, 10,
				SpringLayout.EAST, mMobile);

		// quick settings
		layout.putConstraint(SpringLayout.WEST, quickSettings, 0,
				SpringLayout.WEST, leftPanel);
		layout.putConstraint(SpringLayout.NORTH, quickSettings, 10,
				SpringLayout.SOUTH, mMobile);
		layout.putConstraint(SpringLayout.EAST, leftPanel, 10,
				SpringLayout.EAST, quickSettings);

		return leftScrollPane;
	}

	private JPanel fillQuickSettingsPanel() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		result.setBorder(new TitledBorder("Quick Settings"));
		for (SensorView sensorView : mSensors) {
			sensorView.setQuickSettingsPanel(result);
			if (sensorView.getModel().isEnabled())
				result.add(sensorView.getQuickSettingsPanel());
		}
		return result;
	}

	/**
	 * Sets the socket port for listening
	 */
	public void setPort() {
		addMessage("Closing port " + mModel.getSimulationPort());
		mModel.restartSensorServer();
	}

	/**
	 * Adds new message to message box. If scroll position is at end, it will
	 * scroll to new message.
	 * 
	 * @param msg
	 *            Message.
	 */
	public void addMessage(String msg) {
		String oldText = mMessageTextArea.getText();
		mMessageTextArea.setText(oldText + msg + "\n");
	}

	/**
	 * Get socket port number.
	 * 
	 * @return String containing port number.
	 */
	public int getPort() {
		String s = mSensorPortText.getText();
		int port = 0;
		try {
			port = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			addMessage("Invalid port number: " + s);
		}
		return port;
	}

	/**
	 * This method is called by SensorServerThread when a new client connects.
	 */
	public void newClient() {
		addMessage("First incoming connection:");
		addMessage("ALL SENSORS DISABLED!");
	}

	/**
	 * Safely retries the double value of a text field. If the value is not a
	 * valid number, 0 is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            Textfield from which the value should be read.
	 * @param defaultValue
	 *            default value if input field is invalid.
	 * @return double value.
	 */
	public double getSafeDouble(JTextField textfield, double defaultValue) {
		double value = defaultValue;

		try {
			value = Double.parseDouble(textfield.getText());
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			value = defaultValue;
			textfield.setBackground(Color.RED);
		}
		return value;
	}

	public double getSafeDouble(JTextField textfield) {
		return getSafeDouble(textfield, 0);
	}

	/**
	 * Safely retries the float value of a text field. If the value is not a
	 * valid number, 0 is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            TextField from which the value should be read.
	 * @param defaultValue
	 *            default value if input field is invalid.
	 * @return float value.
	 */
	public float getSafeFloat(JTextField textfield, float defaultValue) {
		float value = defaultValue;
		try {
			value = Float.parseFloat(textfield.getText());
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			value = defaultValue;
			textfield.setBackground(Color.RED);
		}
		return value;
	}

	public float getSafeFloat(JTextField textfield) {
		return getSafeFloat(textfield, 0);
	}

	/**
	 * Safely retries the a list of double values of a text field. If the list
	 * contains errors, null is returned, and the field is marked red.
	 * 
	 * @param textfield
	 *            TextField from which the value should be read.
	 * @return list double[] with values or null.
	 */
	public double[] getSafeDoubleList(JTextField textfield) {
		double[] valuelist = null;

		try {
			String t = textfield.getText();
			// Now we have to split this into pieces
			String[] tlist = t.split(",");
			int len = tlist.length;
			if (len > 0) {
				valuelist = new double[len];
				for (int i = 0; i < len; i++) {
					valuelist[i] = Double.parseDouble(tlist[i]);
				}
			} else {
				valuelist = null;
			}
			textfield.setBackground(Color.WHITE);
		} catch (NumberFormatException e) {
			// wrong user input in box - take default values.
			valuelist = null;
			textfield.setBackground(Color.RED);
		}
		return valuelist;
	}

	public double getUpdateSensors() {
		return getSafeDouble(mUpdateText);
	}

	public double getRefreshAfter() {
		return getSafeDouble(mRefreshCountText);
	}

	public BarcodeReaderView getBarcodeReader() {
		return (BarcodeReaderView) mSensors.get(SensorModel.POZ_BARCODE_READER);
	}

	public AccelerometerView getAccelerometer() {
		return (AccelerometerView) mSensors.get(SensorModel.POZ_ACCELEROMETER);
	}

	public LightView getLight() {
		return (LightView) mSensors.get(SensorModel.POZ_LIGHT);
	}

	public OrientationView getOrientation() {
		return (OrientationView) mSensors.get(SensorModel.POZ_ORIENTATION);
	}

	public ProximityView getProximity() {
		return (ProximityView) mSensors.get(SensorModel.POZ_PROXIMITY);
	}

	public TemperatureView getTemperature() {
		return (TemperatureView) mSensors.get(SensorModel.POZ_TEMPERATURE);
	}

	public MagneticFieldView getMagneticField() {
		return (MagneticFieldView) mSensors.get(SensorModel.POZ_MAGNETIC_FIELD);
	}

	public JButton getSensorPortButton() {
		return mSensorPortButton;
	}

	public long getRefreshCount() {
		return (long) getSafeDouble(mRefreshCountText);
	}

	public void setRefreshSensorsLabel(double ms) {
		mRefreshSensorsLabel.setText(Global.TWO_DECIMAL_FORMAT.format(ms)
				+ " ms");
	}

	public void setOutput(String data) {
		if (!data.equals(mTextAreaSensorData.getText()))
			mTextAreaSensorData.setText(data);
	}

	public JTextPane getMessagePanel() {
		return mMessageTextArea;
	}

	public JTabbedPane getSensorsTabbedPanel() {
		return mSensorsTabbedPane;
	}

	public PressureView getPressure() {
		return (PressureView) mSensors.get(SensorModel.POZ_PRESSURE);
	}

	public LinearAccelerationView getLinearAceleration() {
		return (LinearAccelerationView) mSensors
				.get(SensorModel.POZ_LINEAR_ACCELERATION);
	}

	public GravityView getGravity() {
		return (GravityView) mSensors.get(SensorModel.POZ_GRAVITY);
	}

	public RotationVectorView getRotationVector() {
		return (RotationVectorView) mSensors.get(SensorModel.POZ_ROTATION);
	}

	public GyroscopeView getGyroscope() {
		return (GyroscopeView) mSensors.get(SensorModel.POZ_GYROSCOPE);
	}

	public DeviceView getDeviceView() {
		return mMobile;
	}

	public AllSensorsView getAllSensorsView() {
		return mAllSensorsTab;
	}
}
