package com.jhueventtrackr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
	protected static final int LOGIN = 0;
	private final int SUCCESS = 1;
	protected static boolean wifiConnection = true;
	private GoogleMap _map = null;
	private GraphUser _user = null; // use to get first name, last name,
									// birthday, Facebook username, etc.
	private double _longitude;
	private double _latitude;
	private Marker _currentLocMarker = null;
	private Boolean _foundLoc = false;

	private MapActivity a = this;

	private LocationManager _locManager;

	protected static EventCollection ec;
	// protected static ArrayList<Event> eventList = new ArrayList<Event>();
	// protected static ArrayList<Event> userEventList = new ArrayList<Event>();

	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			_longitude = location.getLongitude();
			_latitude = location.getLatitude();
			_foundLoc = true;

			refreshCurrentMarker();
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		// Example of how to add an event to the online DB
		// Timestamp startTime = new Timestamp(System.currentTimeMillis() +
		// 30*60*1000);
		// Timestamp endTime = new Timestamp(System.currentTimeMillis() +
		// 48*60*60*1000);
		// Event party = new Event("Andrew's pineapple party",
		// "Be there or be square", 39.336495, -76.622804, startTime.toString(),
		// endTime.toString(), 2356);
		// RemoteDB.postEvent(party);

		// RemoteDB.getEvents(this); // Get all events in the DB table
		// RemoteDB.getEvents(this, startTime.toString(), endTime.toString());
		// // Get all events between the startTime and endTime

		// RemoteDB.getEvents(this, 48 ); // Get all events between current time
		// and interval(hours)
		// RemoteDB.getEventsByNameDesc(this, "pIneapple", "Square"); // Get all
		// events that contain 2nd and 3rd arguments in the name/desc

		// RemoteDB.getEventsByUserID(this, 8);

		// Get events between now and the next interval specified in hours
		// int interval = 48;
		// RemoteDB.getEvents(this, interval);
		// RemoteDB.getEvents(this, 48 ); // Get all events between current time
		// and interval(hours)
		// RemoteDB.getEventsByNameDesc(this, "pIneapple", "Beach"); // Get all
		// events that contain 2nd and 3rd arguments in the name/desc
		// RemoteDB.removeEventByEventID(this, eventList.get(0).id);
		Session session = Session.getActiveSession();
		if (session != null && session.isClosed()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, LOGIN);
		}
		if (session == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, LOGIN);
			overridePendingTransition(0, 0);
		}
		Log.d("water", "before ec creation");
		ec = new EventCollection(this);
		Context context = getApplicationContext();
		wifiConnection = checkWifiConnection(context);
		if (!wifiConnection) {
			Log.v("MapActivity Wifi Status: ", wifiConnection + "");
			noWifiConnectionDialog(MapActivity.this);
		} else {
			refreshWithNewEvents(false);
		}
	}

	// private void populateFilterButtons(LinearLayout ll, ArrayList<String>
	// collection, String header) {
	// DisplayMetrics size = this.getResources().getDisplayMetrics();
	// int maxWidth = size.widthPixels - 10;
	//
	// if (collection.size() > 0) {
	// LinearLayout llAlso = new LinearLayout(this);
	// llAlso.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
	// LayoutParams.WRAP_CONTENT));
	// llAlso.setOrientation(LinearLayout.HORIZONTAL);
	//
	// TextView txtSample = new TextView(this);
	// txtSample.setText(header);
	//
	// llAlso.addView(txtSample);
	// txtSample.measure(0, 0);
	//
	// int widthSoFar = txtSample.getMeasuredWidth();
	// for (String samItem : collection) {
	// Button txtSamItem = new Button(this);
	// txtSamItem.setText(samItem);
	// txtSamItem.setPadding(10, 0, 0, 0);
	// txtSamItem.setTag(samItem);
	// txtSamItem.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	//
	// }
	// });
	//
	// txtSamItem.measure(0, 0);
	// widthSoFar += txtSamItem.getMeasuredWidth();
	//
	// if (widthSoFar >= maxWidth) {
	// ll.addView(llAlso);
	//
	// llAlso = new LinearLayout(this);
	// llAlso.setLayoutParams(new LayoutParams(
	// LayoutParams.MATCH_PARENT,
	// LayoutParams.WRAP_CONTENT));
	// llAlso.setOrientation(LinearLayout.HORIZONTAL);
	//
	// llAlso.addView(txtSamItem);
	// widthSoFar = txtSamItem.getMeasuredWidth();
	// } else {
	// llAlso.addView(txtSamItem);
	// }
	// }
	//
	// ll.addView(llAlso);
	// }
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh_button_map) {
			if (wifiConnection) {
				Context context = getApplicationContext();
				wifiConnection = checkWifiConnection(context);
				if (!wifiConnection) {
					noWifiConnectionDialog(MapActivity.this);
				} else {
					refreshWithNewEvents(true);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (wifiConnection) {
			Context context = getApplicationContext();
			wifiConnection = checkWifiConnection(context);
			if (!wifiConnection) {
				noWifiConnectionDialog(MapActivity.this);
			}
		}
		Log.v("MapActivity.onResume() Wifi Status: ", wifiConnection + "");
		if (EventCollection.events != null) {
			getMapObj();
			getUserObj();
		}
	}

	public void refreshWithNewEvents(final boolean showToast) {
		new AsyncTask<Void, Void, Void>() {
			protected void onPreExecute() {
				Log.d("water", "before updateserver");
				EventCollection.updateWithServer();
			}

			@Override
			protected Void doInBackground(Void... params) {
				return null;
			}

			protected void onPostExecute(Void v) {
				EventCollection.updateLocals();
				EventCollection.updateLocalsAgainstServer();
				if (EventCollection.events != null) {
					getMapObj();
					getUserObj();
					if (showToast) {
						MapActivity.showToastMessage("Map Events Refreshed!",
								MapActivity.this);
					}
				}
			}

		}.execute();
	}

	private void getMapObj() {
		// get map if the reference is currently null
		if (_map == null) {
			_map = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			_map.setOnMapLongClickListener(new OnMapLongClickListener() {

				@Override
				public void onMapLongClick(LatLng arg0) {
					Intent intent = new Intent(a, CreateEventActivity.class);
					intent.putExtra("lat", arg0.latitude);
					intent.putExtra("lng", arg0.longitude);
					startActivityForResult(intent, 1);
				}

			});
			_map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					LatLng ll = marker.getPosition();
					final Event e = EventCollection.findEvent(ll);
					AlertDialog.Builder builder = new AlertDialog.Builder(a);
					if (e == null) {
						builder.setTitle("Error!");
						builder.setMessage("There was an error while finding your event.");
						builder.setPositiveButton("Okay",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
									}
								});
						AlertDialog dialog = builder.create();
						dialog.show();
					} else {
						builder.setTitle(e.name + "\n");
						builder.setMessage("When: " + e.readableStartTime()
								+ e.readableEndTime() + "\nWhere: "
								+ e.getLocation() + "\n\nDescription:\n"
								+ e.getDescWithName());
						builder.setNegativeButton("Back",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});
						if (EventCollection.isCreator(e)) {
							builder.setNeutralButton("Edit Your\nEvent",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent(a,
													EditEventActivity.class);
											intent.putExtra("title",
													e.getName());
											intent.putExtra("year", e.getYear());
											intent.putExtra("month",
													e.getMonth());
											intent.putExtra("day", e.getDay());
											intent.putExtra("shour",
													e.getSHour());
											intent.putExtra("smin", e.getSMin());
											intent.putExtra("ehour",
													e.getEHour());
											intent.putExtra("emin", e.getEMin());
											intent.putExtra("lat", e.getLat());
											intent.putExtra("lng", e.getLng());
											intent.putExtra("loc",
													e.getLocation());
											intent.putExtra("desc", e.getDesc());
											startActivityForResult(intent, 1);
										}
									});
							builder.setPositiveButton("Delete Your\nEvent",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											AlertDialog.Builder builder = new AlertDialog.Builder(
													a);
											builder.setTitle("Confirm");
											builder.setMessage("Do you really want to delete your event:\n"
													+ e.getName());
											builder.setPositiveButton(
													"Yes",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {
															RemoteDB.removeEvent(e);
															EventCollection
																	.deleteCreatedEvent(e);
															refreshWithNewEvents(false);
															MapActivity
																	.showToastMessage(
																			"Event Deleted!",
																			MapActivity.this);
														}
													});
											builder.setNegativeButton(
													"Cancel",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {
														}
													});
											AlertDialog dialog2 = builder
													.create();
											dialog2.show();
										}
									});
						} else {
							builder.setPositiveButton("Add to\nTRACKING",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (!EventCollection
													.addWatchingEvent(e)) {
												AlertDialog.Builder builder = new AlertDialog.Builder(
														a);
												builder.setTitle("Error!");
												builder.setMessage("You are already tracking this event!");
												builder.setPositiveButton(
														"Okay",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int id) {
															}
														});
												AlertDialog dialog2 = builder
														.create();
												dialog2.show();
											} else {
												MapActivity.showToastMessage(
														"Now Tracking Event!",
														MapActivity.this);
											}
										}
									});
						}
						AlertDialog dialog = builder.create();
						dialog.show();
					}
				}

			});
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(39.3296454, -76.6199633)).tilt(60)
					.zoom(17).bearing(0).build();
			_map.setBuildingsEnabled(true);
			_map.moveCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			_map.getUiSettings().setAllGesturesEnabled(true);
			_map.getUiSettings().setTiltGesturesEnabled(false);

		}
		refreshEventsToMap();
	}

	private void getUserObj() {
		// get the user object if necessary
		if (_user == null) {
			System.out.println("getting user");
			Session session = Session.getActiveSession();
			Request.newMeRequest(session, new Request.GraphUserCallback() {
				// callback after Graph API response with user object
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						_user = user;
						setUpMap();
					}
				}
			}).executeAsync();
		}
	}

	private void setUpMap() {
		if (_map != null) {
			_locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			_locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					2000, 10, locationListener);
			_locManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2000, 10,
					locationListener);
		}
	}

	private void refreshCurrentMarker() {
		if (_foundLoc) {
			if (_currentLocMarker == null) {
				LatLng currLatLng = new LatLng(_latitude, _longitude);
				_currentLocMarker = _map
						.addMarker(new MarkerOptions()
								.position(currLatLng)
								.title(EventCollection.userFName)
								.snippet("You are here")
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				_currentLocMarker.showInfoWindow();
				_map.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng,
						16));
			} else {
				_currentLocMarker
						.setPosition(new LatLng(_latitude, _longitude));
			}
		}
	}

	private void clearMap() {
		_map.clear();
		_currentLocMarker = null;
	}

	protected void refreshEventsToMap() {
		System.out.println("refreshing events to map");
		clearMap();
		refreshCurrentMarker();

		List<Event> filtered_list = FilterActivity
				.getFilteredEventList(getApplicationContext());

		for (Event event : filtered_list) {
			String t = event.name.length() > 30 ? event.name.substring(0, 30)
					+ "..." : event.name;
			String d = event.desc.length() > 30 ? event.desc.substring(0, 30)
					+ "..." : event.desc;

			float f = 0;
			long cur = (new Date()).getTime();

			// System.out.println("name " + event.getName());
			// System.out.println("curr " + cur);
			long start = event.getStart().getTime();
			// System.out.println("start " + start);
			long end = event.getEnd().getTime();
			// System.out.println("end " + end);
			long diff = Math.abs(cur - start);
			// System.out.println("diff" + diff);

			if (cur >= start && cur <= end) {
				f = BitmapDescriptorFactory.HUE_GREEN;
			} else if (diff < TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS)) {
				f = BitmapDescriptorFactory.HUE_YELLOW;
			} else {
				f = BitmapDescriptorFactory.HUE_RED;
			}

			if (cur <= end) {
				_map.addMarker(new MarkerOptions().position(event.latlng)
						.title(t).snippet(d)
						.icon(BitmapDescriptorFactory.defaultMarker(f)));
			} else {
				_map.addMarker(new MarkerOptions()
						.position(event.latlng)
						.title("[Ended] " + t)
						.snippet(d)
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
						.alpha(0.25f));
			}
		}

	}

	public long getDateCurrentTimeZone(long timestamp) {
		try {
			Calendar calendar = Calendar.getInstance();
			TimeZone tz = TimeZone.getDefault();
			calendar.setTimeInMillis(timestamp * 1000);
			calendar.add(Calendar.MILLISECOND,
					tz.getOffset(calendar.getTimeInMillis()));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date currenTimeZone = (Date) calendar.getTime();
			return currenTimeZone.getTime();
		} catch (Exception e) {
		}
		return 0;
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

	public void enterEventList(MenuItem item) {
		Intent intent = new Intent(this, EventListActivity.class);
		startActivity(intent);
	}

	private void redirectToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, LOGIN);
	}

	@Override
	protected void onActivityResult(int request, int result, Intent intent) {
		if (request == LOGIN && result != SUCCESS) {
			Session.getActiveSession().closeAndClearTokenInformation();
			finish();
		}
		if (request == 1 && result == 1) {
			refreshWithNewEvents(false);
			showToastMessage("Event Updated!", this);
		}
	}

	public static void showToastMessage(String text, Context context) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * Checks if there is wifi available.
	 * 
	 * @param context
	 *            Application context i.e. getApplicationContext()
	 * @return true if wifi available, false otherwise
	 */
	protected static boolean checkWifiConnection(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo myWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (myWifi.isConnected())
			return true;
		else
			return false;
	}

	protected static void noWifiConnectionDialog(Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle("Warning!");
		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Wifi/data connection not available.  Edits will not be displayed or saved on the map.")
				.setCancelable(false) // no back key to cancel
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
