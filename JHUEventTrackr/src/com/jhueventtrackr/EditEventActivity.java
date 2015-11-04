package com.jhueventtrackr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.facebook.Session;

public class EditEventActivity extends Activity {

	private final int LOCATION = 0;
	private double lat, lng;
	private EditText title, date, startTime, endTime, location, description;
	private Button ok, changeLocation;
	private LinearLayout ll;
	private Activity a;
	private int sHour, sMinute, eHour, eMinute, year, month, day;
	private Session _session;
	private Bundle b;
	private Event old;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_event);
		b = getIntent().getExtras();
		lat = b.getDouble("lat", 0);
		lng = b.getDouble("lng", 0);
		sHour = b.getInt("shour", 0);
		eHour = b.getInt("ehour", 0);
		sMinute = b.getInt("smin", 0);
		eMinute = b.getInt("emin", 0);
		year = b.getInt("year", 0);
		month = b.getInt("month", 0);
		day = b.getInt("day", 0);
		a = this;
		initComponents();
		initListeners();
		initFocusManager();
		old = new Event(title.getText().toString(), description.getText()
				.toString(), lat, lng, location.getText().toString(), year, month, day, sHour, sMinute, eHour,
				eMinute, EventCollection.userId, EventCollection.userFName,
				EventCollection.userLName);
	}

	public void initComponents() {
		title = (EditText) findViewById(R.id.edit_event_title);
		title.setText(b.getString("title"));
		date = (EditText) findViewById(R.id.edit_event_date);
		date.setText(month + 1 + "/" + day + "/" + year);

		startTime = (EditText) findViewById(R.id.edit_event_start_time);
		String ap = sHour > 11 ? " PM" : " AM";
		int editHour = sHour > 12 ? sHour - 12 : sHour;
		editHour = editHour == 0 ? 12 : editHour;
		String min = sMinute < 10 && sMinute != 0 ? "0" + sMinute : sMinute
				+ "";
		min = sMinute == 0 ? sMinute + "0" : min;
		startTime.setText(editHour + ":" + min + ap);

		endTime = (EditText) findViewById(R.id.edit_event_end_time);
		String ap1 = eHour > 11 ? " PM" : " AM";
		int editHour1 = eHour > 12 ? eHour - 12 : eHour;
		editHour1 = editHour1 == 0 ? 12 : editHour1;
		String min1 = eMinute < 10 && eMinute != 0 ? "0" + eMinute : eMinute
				+ "";
		min1 = eMinute == 0 ? eMinute + "0" : min1;
		endTime.setText(editHour1 + ":" + min1 + ap1);

		location = (EditText) findViewById(R.id.edit_event_location);
		location.setText(b.getString("loc"));
		changeLocation = (Button) findViewById(R.id.edit_event_location_button);
		description = (EditText) findViewById(R.id.edit_event_description);
		description.setText(b.getString("desc"));
		ok = (Button) findViewById(R.id.edit_event_ok);
	}

	public void initListeners() {
		title.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					title.setHint("");
				} else {
					title.setHint("Title");
				}

			}

		});
		date.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					date.setHint("");
					final Calendar c = Calendar.getInstance();
					int y = c.get(Calendar.YEAR);
					int m = c.get(Calendar.MONTH);
					int d = c.get(Calendar.DAY_OF_MONTH);

					if (!date.getText().toString().equals("")) {
						y = year;
						m = month;
						d = day;
					}
					DatePickerDialog dpd = new DatePickerDialog(a,
							new OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datePicker,
										int ye, int mo, int da) {
									year = ye;
									month = mo;
									day = da;
									date.setText(mo + 1 + "/" + da + "/" + ye);
								}

							}, y, m, d);
					dpd.show();
					ll.requestFocus();
				} else {
					date.setHint("Date");
				}

			}

		});
		startTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					startTime.setHint("");
					final Calendar c = Calendar.getInstance();
					int h = c.get(Calendar.HOUR_OF_DAY);
					int m = c.get(Calendar.MINUTE);

					if (!startTime.getText().toString().equals("")) {
						h = sHour;
						m = sMinute;
					}

					TimePickerDialog tpd = new TimePickerDialog(a,
							new OnTimeSetListener() {
								@Override
								public void onTimeSet(TimePicker timePicker,
										int ho, int mi) {
									if (eHour * 100 + eMinute <= ho * 100 + mi
											&& !endTime.getText().toString()
													.equals("")) {
										AlertDialog.Builder builder = new AlertDialog.Builder(
												a);
										builder.setTitle("Error!");
										builder.setMessage("Your start time is after your end time!");
										builder.setPositiveButton(
												"Okay",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
													}
												});
										AlertDialog dialog = builder.create();
										dialog.show();
										return;
									}
									sHour = ho;
									sMinute = mi;
									String ap = sHour > 11 ? " PM" : " AM";
									int editHour = sHour > 12 ? sHour - 12
											: sHour;
									editHour = editHour == 0 ? 12 : editHour;
									String min = sMinute < 10 && sMinute != 0 ? "0"
											+ sMinute
											: sMinute + "";
									min = sMinute == 0 ? sMinute + "0" : min;
									startTime
											.setText(editHour + ":" + min + ap);
								}

							}, h, m, false);
					tpd.show();
					ll.requestFocus();
				} else {
					startTime.setHint("Start Time");
				}

			}

		});
		endTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					endTime.setHint("");
					final Calendar c = Calendar.getInstance();
					int h = c.get(Calendar.HOUR_OF_DAY);
					int m = c.get(Calendar.MINUTE);

					if (!endTime.getText().toString().equals("")) {
						h = eHour;
						m = eMinute;
					}

					TimePickerDialog tpd = new TimePickerDialog(a,
							new OnTimeSetListener() {
								@Override
								public void onTimeSet(TimePicker timePicker,
										int ho, int mi) {
									if (sHour * 100 + sMinute >= ho * 100 + mi
											&& !startTime.getText().toString()
													.equals("")) {
										AlertDialog.Builder builder = new AlertDialog.Builder(
												a);
										builder.setTitle("Error!");
										builder.setMessage("Your end time is before your start time!");
										builder.setPositiveButton(
												"Okay",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
													}
												});
										AlertDialog dialog = builder.create();
										dialog.show();
										return;
									}
									eHour = ho;
									eMinute = mi;
									String ap = eHour > 11 ? " PM" : " AM";
									int editHour = eHour > 12 ? eHour - 12
											: eHour;
									editHour = editHour == 0 ? 12 : editHour;
									String min = eMinute < 10 && eMinute != 0 ? "0"
											+ eMinute
											: eMinute + "";
									min = eMinute == 0 ? eMinute + "0" : min;
									endTime.setText(editHour + ":" + min + ap);
								}

							}, h, m, false);
					tpd.show();
					ll.requestFocus();
				} else {
					endTime.setHint("End Time");
				}

			}

		});
		changeLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(a, SelectLocationActivity.class);
				intent.putExtra("lat", lat);
				intent.putExtra("lng", lng);
				startActivityForResult(intent, LOCATION);
			}
			
		});
		location.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					location.setHint("");
				} else {
					location.setHint("Location");
				}

			}

		});
		description.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					description.setGravity(Gravity.LEFT);
					description.setHint("");
				} else {
					description.setGravity(Gravity.CENTER);
					description.setHint("Description");
				}

			}

		});
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkFields(true)) {
					try {
						Date startDate = new SimpleDateFormat(
								"mm dd yyyy hh mm").parse(month + " " + day
								+ " " + year + " " + sHour + " " + sMinute);
						Date endDate = new SimpleDateFormat("mm dd yyyy hh mm")
								.parse(month + " " + day + " " + year + " "
										+ eHour + " " + eMinute);
						Event e = new Event(title.getText().toString(),
								description.getText().toString(), lat, lng, location.getText().toString(),
								year, month, day, sHour, sMinute, eHour,
								eMinute, EventCollection.userId,
								EventCollection.userFName,
								EventCollection.userLName);
						RemoteDB.removeEvent(old);
						boolean temp = false;
						if (EventCollection.isWatching(old)) {
							temp = true;
						}
						EventCollection.deleteCreatedEvent(old);
						if (temp) {
							EventCollection.addWatchingEvent(e);
						}
						RemoteDB.postEvent(e);
						EventCollection.addCreatedEvent(e);
						// publishFBEvent(title.getText().toString(),
						// description
						// .getText().toString(), startDate, endDate);
						Intent intent = new Intent();
						setResult(1, intent);
						finish();
					} catch (Exception e) {
						AlertDialog.Builder builder = new AlertDialog.Builder(a);
						builder.setTitle("Error!");
						builder.setMessage("There was an error while processing your event.");
						builder.setPositiveButton("Okay",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
									}
								});
						AlertDialog dialog = builder.create();
						dialog.show();
					}
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(a);
					builder.setTitle("Error!");
					builder.setMessage("Please fill all fields before submitting your event.");
					builder.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}

		});
	}

	public void initFocusManager() {
		ll = (LinearLayout) findViewById(R.id.dummy_view2);
		hideKeyboardOnOffClick(findViewById(R.id.parent2), this);
	}

	public void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);
		ll.requestFocus();
	}

	public void hideKeyboardOnOffClick(View view, Activity a) {
		final Activity activity = a;

		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {

			view.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(activity);
					return false;
				}

			});
		}

		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {

			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

				View innerView = ((ViewGroup) view).getChildAt(i);

				hideKeyboardOnOffClick(innerView, a);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == LOCATION
				&& resultCode == SelectLocationActivity.getResultOkCode()) {
			lat = intent.getExtras().getDouble("lat");
			lng = intent.getExtras().getDouble("lng");
			ll.requestFocus();
		}
	}

	public boolean checkFields(boolean checkAll) {
		EditText[] all = { title, date, startTime, endTime, location,
				description };
		boolean isEmpty = true;
		int count = 0;
		for (EditText e : all) {
			if (!e.getText().toString().equals("")) {
				isEmpty = false;
				if (checkAll) {
					count++;
				} else {
					break;
				}
			}
		}
		return checkAll ? count == 6 : isEmpty;
	}

	@Override
	public void onBackPressed() {
		if (checkFields(false)) {
			super.onBackPressed();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(a);
			builder.setTitle("Warning!");
			builder.setMessage("Your event has not been saved yet. Are you sure you want to leave this page?");
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							EditEventActivity.super.onBackPressed();
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

}
