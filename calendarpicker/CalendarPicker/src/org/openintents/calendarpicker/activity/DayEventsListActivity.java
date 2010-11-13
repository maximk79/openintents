package org.openintents.calendarpicker.activity;

import java.text.DateFormatSymbols;
import java.util.Date;

import org.openintents.calendarpicker.R;
import org.openintents.calendarpicker.contract.IntentConstants;
import org.openintents.calendarpicker.view.ScrollableMonthView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class DayEventsListActivity extends AbstractEventsListActivity {

	static final String TAG = "DayEventsListActivity";
	
    // ========================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        View main = findViewById(R.id.main);
        main.setBackgroundResource(R.drawable.panel_background);
    }
    
    // ========================================================================
	Cursor requery() {

        Uri intent_data = getIntent().getData();
    	Log.d(TAG, "Querying content provider for: " + intent_data);
    	
        Date d = new Date(getIntent().getLongExtra(IntentConstants.CalendarDatePicker.INTENT_EXTRA_EPOCH, 0));
        

        Log.e(TAG, "Received date: " + d.getDate());
        long day_begin = d.getTime();
        long day_end = day_begin + ScrollableMonthView.MILLISECONDS_PER_DAY;
        
		
		Cursor cursor = managedQuery(intent_data,
				new String[] {
					KEY_ROWID,
					KEY_EVENT_TIMESTAMP,
					KEY_EVENT_TITLE},
				KEY_EVENT_TIMESTAMP + ">=? AND " + KEY_EVENT_TIMESTAMP + "<?",
				new String[] {Long.toString(day_begin), Long.toString(day_end)},
				constructOrderByString());

		String header_text = cursor.getCount() + " event(s) on " + new DateFormatSymbols().getShortMonths()[d.getMonth()] + " " + d.getDate();
		((TextView) findViewById(R.id.list_header)).setText(header_text);
		
		return cursor;
	}

}
