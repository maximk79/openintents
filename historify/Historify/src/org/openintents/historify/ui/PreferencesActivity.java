/* 
 * Copyright (C) 2011 OpenIntents.org
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

package org.openintents.historify.ui;

import org.openintents.historify.R;
import org.openintents.historify.ui.fragments.PreferencesFragment;
import org.openintents.historify.ui.views.ActionBar;
import org.openintents.historify.ui.views.ActionBar.MoreMenuFunction;
import org.openintents.historify.uri.Actions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.TextView;

public class PreferencesActivity extends FragmentActivity {
	
	private ActionBar actionBar;
	private PreferencesFragment preferencesFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_preferences);
		
		preferencesFragment = (PreferencesFragment) getSupportFragmentManager().findFragmentById(R.id.preferences_fragment);
		
		setupActionBar();
	}

	private void setupActionBar() {

		actionBar = new ActionBar((ViewGroup) findViewById(R.id.actionbar), R.string.preferences_title);
		actionBar.setInactiveFunction(MoreMenuFunction.preferences);
		actionBar.setup();
	}
}