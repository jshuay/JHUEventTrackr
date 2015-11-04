package com.jhueventtrackr;

import java.util.ArrayList;
import java.util.List;

import com.facebook.Session;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class EventListActivity extends Activity implements TabListener {

	List<Fragment> fragList = new ArrayList<Fragment>();

	private int cur;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		setContentView(R.layout.activity_eventlist);

		for (int i = 0; i < 3; i++) {
			Tab tab = bar.newTab();
			switch (i) {
			case 0:
				tab.setText("All"); // Deleted "Events" to save room. -AW
				break;
			case 1:
				tab.setText("Tracking"); // Our app is eventTrackr...
				break;
			case 2:
				tab.setText("Created");
				break;
			}
			tab.setTabListener(this);
			bar.addTab(tab);
			cur = bar.getSelectedTab().getPosition();
		}
	}

	public void initFragList() {
		Log.d("wednesday", "does this get called before tab selected??");
		for (int i = 0; i < 3; i++) {
			EventListTabFrag tf = new EventListTabFrag();
			Bundle data = new Bundle();
			data.putInt("idx", i);
			tf.setArguments(data);
			fragList.add(tf);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("wednesday", "on resume in event list activity");
	}

	public void refreshEvents() {
		cur = getActionBar().getSelectedTab().getPosition();
		new AsyncTask<Void, Void, Void>() {
			protected void onPreExecute() {
				EventCollection.updateWithServer();
			}

			@Override
			protected Void doInBackground(Void... params) {
				return null;
			}

			protected void onPostExecute(Void v) {
				EventCollection.updateLocals();
				EventCollection.updateLocalsAgainstServer();
				ArrayList<Event> temp = new ArrayList<Event>();
				switch (cur) {
				case (0):
					temp = EventCollection.events;
					break;
				case (1):
					temp = EventCollection.watching;
					break;
				case (2):
					temp = EventCollection.created;
					break;
				}
				((EventListTabFrag) fragList.get(cur)).updateLists(temp);
				MapActivity.showToastMessage("Event List Refreshed!", EventListActivity.this);
			}
		}.execute();
	}

	public void createEvent(MenuItem item) {
		Intent intent = new Intent(this, CreateEventActivity.class);
		intent.putExtra("lat", 0.0);
		intent.putExtra("lng", 0.0);
		startActivityForResult(intent, 0);
	}

	public void editEvent(Event event) {
		Intent intent = new Intent(this, EditEventActivity.class);
		intent.putExtra("title", event.getName());
		intent.putExtra("year", event.getYear());
		intent.putExtra("month", event.getMonth());
		intent.putExtra("day", event.getDay());
		intent.putExtra("shour", event.getSHour());
		intent.putExtra("smin", event.getSMin());
		intent.putExtra("ehour", event.getEHour());
		intent.putExtra("emin", event.getEMin());
		intent.putExtra("lat", event.getLat());
		intent.putExtra("lng", event.getLng());
		intent.putExtra("loc", event.getLocation());
		intent.putExtra("desc", event.getDesc());
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == 0 && resultCode == 1) {
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, "Event Created!", duration);
			toast.show();
		}
		if (requestCode == 1 && resultCode == 1) {
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, "Event Updated!", duration);
			cur = getActionBar().getSelectedTab().getPosition();
			((EventListTabFrag) fragList.get(cur))
					.updateLists(EventCollection.created);
			toast.show();
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Log.d("wednesday", "on tab select at start???");
		Fragment f = null;
		EventListTabFrag tf = null;
		if (fragList.size() > tab.getPosition()) {
			f = fragList.get(tab.getPosition());
		}

		if (f == null) {
			initFragList();
			f = fragList.get(tab.getPosition());
		}
		tf = (EventListTabFrag) f;

		ft.replace(android.R.id.content, tf);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragList.get(tab.getPosition()));
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
//		Fragment f = null;
//		EventListTabFrag tf = null;
//
//		Log.d("water", "fraglist size: " + fragList.size() + ", tab position: "
//				+ tab.getPosition());
//		if (fragList.size() > tab.getPosition())
//			f = fragList.get(tab.getPosition());
//
//		if (f == null) {
//			tf = new EventListTabFrag();
//			Bundle data = new Bundle();
//			data.putInt("idx", tab.getPosition());
//			tf.setArguments(data);
//			fragList.add(tf);
//		} else
//			tf = (EventListTabFrag) f;
//
//		ft.replace(android.R.id.content, tf);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.refresh_button_list) {
			Log.d("wednesday", "hit refresh button");
			refreshEvents();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// MENU OPTIONS
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.eventlist, menu);
		return true;
	}
	public void logoutApp(MenuItem item) {
		Session session = Session.getActiveSession();
		if (session != null) {
			session.closeAndClearTokenInformation();
		}
		redirectToLogin();
	}
	public void enterSettings(MenuItem item) {
		Intent intent = new Intent(this, SettingActivity.class);
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


