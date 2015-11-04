package com.jhueventtrackr;

import java.util.ArrayList;
import java.util.Arrays;

import com.facebook.Session;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SettingActivity extends Activity {

	private static ArrayList<String> options;
	private ListView lv;
	private CustomAdapter adapter;
	private Activity a = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		String[] list = { "User Profile", "Privacy", "Notification",
				"Enable GPS", "About" };
		options = new ArrayList<String>(Arrays.asList(list));
		lv = (ListView) findViewById(R.id.main_settings_list);
		adapter = new CustomAdapter(this, R.layout.list_item, R.id.about_title,
				options);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent;
				switch (position) {
				case (0):
					intent = new Intent(a, ProfileFragmentActivity.class);
					startActivity(intent);
					break;
				case (1):
					intent = new Intent(a, PrivacyFragmentActivity.class);
					startActivity(intent);
					break;
				case (2):
					intent = new Intent(a, NotificationFragmentActivity.class);
					startActivity(intent);
					break;
				case (3):
					intent = new Intent(a, NotificationFragmentActivity.class);
					startActivity(intent);
					break;
				case (4):
					intent = new Intent(a, AboutFragmentActivity.class);
					startActivity(intent);
					break;
				}
			}

		});
	}
	
	// MENU OPTIONS
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}
	public void logoutApp(MenuItem item) {
		Session session = Session.getActiveSession();
		if (session != null) {
			session.closeAndClearTokenInformation();
		}
		redirectToLogin();
	}
	public void enterEventList(MenuItem item) {
		Intent intent = new Intent(this, EventListActivity.class);
		startActivity(intent);
	}
	public void enterFilters(MenuItem item) {
		Intent intent = new Intent(this, FilterActivity.class);
		startActivity(intent);
	}
	public void enterMap(MenuItem item) {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
	private void redirectToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, MapActivity.LOGIN);
	}

}
