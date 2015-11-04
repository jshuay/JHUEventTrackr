package com.jhueventtrackr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
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

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;

public class CreateEventActivity extends Activity {

	private final int LOCATION = 0;
	private double lat, lng;
	private EditText title, date, startTime, endTime, location, description;
	private Button ok, selectLocation;
	private LinearLayout ll;
	private Activity a;
	private int sHour, sMinute, eHour, eMinute, year, month, day;
	private Session _session;
	private Bundle b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
		b = getIntent().getExtras();
		lat = b.getDouble("lat", 0.0);
		lng = b.getDouble("lng", 0.0);
		sHour = 0;
		eHour = 0;
		sMinute = 0;
		eMinute = 0;
		year = 0;
		month = 0;
		day = 0;
		a = this;
		_session = Session.getActiveSession();
		initComponents();
		initListeners();
		initFocusManager();
	}

	public void initComponents() {
		title = (EditText) findViewById(R.id.create_event_title);
		date = (EditText) findViewById(R.id.create_event_date);
		startTime = (EditText) findViewById(R.id.create_event_start_time);
		endTime = (EditText) findViewById(R.id.create_event_end_time);
		location = (EditText) findViewById(R.id.create_event_location);
		selectLocation = (Button) findViewById(R.id.create_event_location_button);
		if (lat != 0.0 && lng != 0.0) {
			selectLocation.setText("Change Location");
		}
		description = (EditText) findViewById(R.id.create_event_description);
		ok = (Button) findViewById(R.id.create_event_ok);
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
		selectLocation.setOnClickListener(new OnClickListener() {
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
						Event e = new Event(title.getText().toString(),
								description.getText().toString(), lat, lng,
								location.getText().toString(), year, month,
								day, sHour, sMinute, eHour, eMinute,
								EventCollection.userId,
								EventCollection.userFName,
								EventCollection.userLName);
						RemoteDB.postEvent(e);
						EventCollection.addCreatedEvent(e);

						Calendar c1 = GregorianCalendar.getInstance();
						c1.set(year, month, day, sHour, sMinute);
						Calendar c2 = GregorianCalendar.getInstance();
						c2.set(year, month, day, eHour, eMinute);

						publishFBEvent(title.getText().toString(), description
								.getText().toString(), location.getText()
								.toString(), c1.getTime(), c2.getTime());

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
		ll = (LinearLayout) findViewById(R.id.dummy_view);
		hideKeyboardOnOffClick(findViewById(R.id.parent), this);
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
			selectLocation.setText("Change Location");
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
		if (checkFields(false) && lat == 0 && lng == 0) {
			super.onBackPressed();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(a);
			builder.setTitle("Warning!");
			builder.setMessage("Your event has not been created yet. Are you sure you want to leave this page?");
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CreateEventActivity.super.onBackPressed();
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

	private void publishFBEvent(final String name, final String description,
			final String location, final Date startTime, final Date endTime) {

		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		builder.setTitle("Post Event");
		builder.setMessage("Would you like to create the event on Facebook?");
		builder.setPositiveButton("Okay",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						realPostFB(name, description, location, startTime,
								endTime);
					}
				});
		builder.setNegativeButton("Nope",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent();
						setResult(1, intent);
						finish();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private void realPostFB(String name, String description, String location,
			Date startTime, Date endTime) {
		if (_session != null) {
			final ProgressDialog progressDialog = ProgressDialog.show(this,
					"Loading", "Creating Event...", true);

			Bundle postParams = new Bundle();
			postParams.putString("name", name);
			postParams.putString("location", location);
			postParams.putString("description", description);
			postParams.putString("start_time", toISO(startTime));
			postParams.putString("end_time", toISO(endTime));

			Request.Callback callback = new Request.Callback() {

				@Override
				public void onCompleted(Response response) {
					System.out.println("response:" + response.toString());
					if (response.getError() != null)
						System.out.println("error:"
								+ response.getError().toString());
					/*
					 * if (response.getError() != null) { AlertDialog.Builder
					 * builder = new AlertDialog.Builder( a);
					 * builder.setTitle("Facebook Error");
					 * builder.setMessage(response.getError().toString());
					 * builder.setPositiveButton( "Okay", new
					 * DialogInterface.OnClickListener() { public void onClick(
					 * DialogInterface dialog, int id) {
					 * progressDialog.dismiss(); Intent intent = new Intent();
					 * setResult(1, intent); finish(); } });
					 * builder.setNegativeButton( "Cancel", new
					 * DialogInterface.OnClickListener() { public void onClick(
					 * DialogInterface dialog, int id) {
					 * progressDialog.dismiss(); Intent intent = new Intent();
					 * setResult(1, intent); finish(); } }); AlertDialog dialog
					 * = builder.create(); dialog.show(); } else {
					 */
					progressDialog.dismiss();
					Intent intent = new Intent();
					setResult(1, intent);
					finish();

					// }
				}
			};

			Request request = new Request(_session, "me/events", postParams,
					HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(a);
			builder.setTitle("Error!");
			builder.setMessage("Your session is not active!");
			builder.setPositiveButton("Okay",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	private String toISO(Date date) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		return df.format(date);
	}

}
