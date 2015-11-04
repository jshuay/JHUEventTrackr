package com.jhueventtrackr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.facebook.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FilterActivity extends ListActivity {

	private static final String PREF_FILE = "filter_file";
	private static final String FILTER_LIST_NAME = "filter_list";
	private String[] _filter_items;
	private ArrayAdapter<String> _adapter;
	private ArrayList<Button> filterButtons;
	private SharedPreferences _prefs;
	private SharedPreferences.Editor _pref_editor;
	private Activity a;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);

		_prefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		_pref_editor = _prefs.edit();
		a = this;

		updateFilterList();

	}

	public void addFilter(MenuItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();

		builder.setTitle(R.string.title_new_filter);

		builder.setView(inflater.inflate(R.layout.dialog_filter_add, null))
				// Add action buttons
				.setPositiveButton(R.string.button_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Dialog d = (Dialog) dialog;
								String s = ((EditText) d
										.findViewById(R.id.text_new_filter_name))
										.getText().toString();
								if (s.equals("")) {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											a);
									builder.setTitle("Error!");
									builder.setMessage("Must enter a name for filter.");
									builder.setPositiveButton(
											"Okay",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
												}
											});
									AlertDialog di = builder.create();
									di.show();
								} else {
									addFilterNameToList(s);
								}
								dialog.cancel();
							}
						})
				.setNegativeButton(R.string.button_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void addFilterNameToList(String text) {
		Set<String> curr_set = new TreeSet<String>(_prefs.getStringSet(
				FILTER_LIST_NAME, new TreeSet<String>()));
		if (curr_set.contains(text + "0") || curr_set.contains(text + "1")) {
			MapActivity.showToastMessage("Filter already exists", this);
		} else {
			curr_set.add(text + "0");
			MapActivity.showToastMessage("Filter added", this);
		}
		System.out.println(curr_set.toString());
		_pref_editor.putStringSet(FILTER_LIST_NAME, curr_set);
		_pref_editor.commit();
		updateFilterList();
	}

	private void updateFilterList() {
		Set<String> pref_list_set = new TreeSet<String>(_prefs.getStringSet(
				FILTER_LIST_NAME, new TreeSet<String>()));
		String[] pref_list_array = pref_list_set
				.toArray(new String[pref_list_set.size()]);
		System.out.println(pref_list_array.length);
		_filter_items = pref_list_array;
		_adapter = new MyArrayAdapter(this, R.layout.row_filter,
				R.id.filter_label, _filter_items);
		setListAdapter(_adapter);
		Log.d("filter", _adapter.getCount() + "");
		Log.d("filter", "Filter item: " + _filter_items.length);
	}

	private class MyArrayAdapter extends ArrayAdapter<String> {

		public MyArrayAdapter(Context context, int resource,
				int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);

		}

		public View getView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.row_filter, null);
				}

				TextView textView = (TextView) convertView
						.findViewById(R.id.filter_label);
				textView.setText(_filter_items[position].substring(0,
						_filter_items[position].length() - 1));

				CheckBox checkBox = (CheckBox) convertView
						.findViewById(R.id.filter_checkbox);
				checkBox.setFocusable(false);
				checkBox.setChecked(_filter_items[position].substring(
						_filter_items[position].length() - 1,
						_filter_items[position].length()).equals("1"));

				checkBox.setOnCheckedChangeListener(new checkListener(position));

				ImageButton discardBtn = (ImageButton) convertView
						.findViewById(R.id.discard_button);
				discardBtn.setFocusable(false);
				discardBtn
						.setOnClickListener(new imgBtnClickListener(position));

			return convertView;
		}

		private void enableFilter(int position) {
			setFilter(position, 1);
		}

		private void disableFilter(int position) {
			setFilter(position, 0);
		}

		private void setFilter(int position, int toState) {
			Set<String> curr_set = new TreeSet<String>(_prefs.getStringSet(
					FILTER_LIST_NAME, new TreeSet<String>()));
			String filter_name = _filter_items[position].substring(0,
					_filter_items[position].length() - 1);
			System.out.println(filter_name);
			curr_set.remove(_filter_items[position]);
			curr_set.add(filter_name + toState);
			Log.d("filter", "filter_name: " + filter_name);
			_pref_editor.putStringSet(FILTER_LIST_NAME, curr_set);
			_pref_editor.commit();
			updateFilterList();
		}

		private class checkListener implements
				CompoundButton.OnCheckedChangeListener {
			private int _position;

			public checkListener(int position) {
				_position = position;
			}

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					enableFilter(_position);
				} else {
					disableFilter(_position);
				}
			}
		}

		private class imgBtnClickListener implements View.OnClickListener {
			private int _position;

			public imgBtnClickListener(int position) {
				_position = position;
			}

			@Override
			public void onClick(View v) {
				removeFilterPrompt(_position);
			}
		}
	}

	private void removeFilterPrompt(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Delete Filter?");

		builder.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						removeFilter(position);
						dialog.cancel();
					}
				});
		builder.setNegativeButton(R.string.button_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		builder.show();
	}

	private void removeFilter(int position) {
		Set<String> curr_set = new TreeSet<String>(_prefs.getStringSet(
				FILTER_LIST_NAME, new TreeSet<String>()));
		curr_set.remove(_filter_items[position]);
		_pref_editor.putStringSet(FILTER_LIST_NAME, curr_set);
		_pref_editor.commit();
		System.out.println("removed");
		updateFilterList();
		MapActivity.showToastMessage("Filter removed", this);
	}

	public static List<String> getFilterStrings(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREF_FILE,
				Context.MODE_PRIVATE);
		Set<String> pref_list_set = new TreeSet<String>(prefs.getStringSet(
				FILTER_LIST_NAME, new TreeSet<String>()));
		List<String> filterStrings = new ArrayList<String>();

		for (String str : pref_list_set) {
			if (str.substring(str.length() - 1, str.length()).equals("1")) {
				filterStrings.add(str.substring(0, str.length() - 1));
			}
		}
		return filterStrings;
	}
	
	/** Returns a list of events filtered by the filter list in shared preferences
	 * @return filtered event list
	 */
	public static List<Event> getFilteredEventList(Context context){
		List<String> filter_strings = getFilterStrings(context);
		List<Event> filtered_list;

		if (filter_strings.isEmpty()) {
			// no filter since nothing's checked
			filtered_list = EventCollection.events;
		} else {
			filtered_list = new ArrayList<Event>();
			for (Event event : EventCollection.events) {
				for (String str : filter_strings) {
					String strL = str.toLowerCase();
					if (event.name.toLowerCase().contains(strL)
							|| event.desc.toLowerCase().contains(strL)) {
						filtered_list.add(event);
					}
				}
			}
		}
		return filtered_list;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		CheckBox c = (CheckBox) v.findViewById(R.id.filter_checkbox);
		TextView t = (TextView) v.findViewById(R.id.filter_label);
		c.setChecked(!c.isChecked());
		String message = t.getText().toString();
		message = c.isChecked() ? message + " Selected!" : message
				+ " Unselected!";
		MapActivity.showToastMessage(message, this);
	}
	
	// MENU OPTIONS

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.filter, menu);
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
	public void enterEventList(MenuItem item) {
		Intent intent = new Intent(this, EventListActivity.class);
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