package com.jhueventtrackr;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class RemoteDB {
	private static final String REMOVE_EVENT_URL = "http://54.86.13.60/removeEvent.php";
	private static final String POST_EVENTS_URL = "http://54.86.13.60/postEvent.php";
	private static final String GET_EVENTS_URL = "http://54.86.13.60/getEvents.php";
	private static final String GET_EVENTS_USERID_URL = "http://54.86.13.60/getEvents_userid.php";
	private static final String GET_EVENTS_TIME_URL = "http://54.86.13.60/getEvents_time.php";
	private static final String GET_EVENTS_NAME_DESC_URL = "http://54.86.13.60/getEvents_name_desc.php";
	private static MapActivity map = null;

	public static void postEvent(Event event) {
		new AsyncTask<Event, Void, Void>() {
			@Override
			protected Void doInBackground(Event... event) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(15);
				nameValuePairs.add(new BasicNameValuePair("name",
						sqlStrCheck(event[0].name)));
				nameValuePairs.add(new BasicNameValuePair("desc",
						sqlStrCheck(event[0].desc)));
				nameValuePairs.add(new BasicNameValuePair("lat", event[0].lat
						+ ""));
				nameValuePairs.add(new BasicNameValuePair("lng", event[0].lng
						+ ""));
				nameValuePairs.add(new BasicNameValuePair("location", event[0].location));
				nameValuePairs.add(new BasicNameValuePair("year", event[0]
						.getYear() + ""));
				nameValuePairs.add(new BasicNameValuePair("month", event[0]
						.getMonth() + ""));
				nameValuePairs.add(new BasicNameValuePair("day", event[0]
						.getDay() + ""));
				nameValuePairs.add(new BasicNameValuePair("shour", event[0]
						.getSHour() + ""));
				nameValuePairs.add(new BasicNameValuePair("smin", event[0]
						.getSMin() + ""));
				nameValuePairs.add(new BasicNameValuePair("ehour", event[0]
						.getEHour() + ""));
				nameValuePairs.add(new BasicNameValuePair("emin", event[0]
						.getEMin() + ""));
				nameValuePairs.add(new BasicNameValuePair("creator",
						event[0].creatorId));
				nameValuePairs.add(new BasicNameValuePair("fname", event[0]
						.getFName()));
				nameValuePairs.add(new BasicNameValuePair("lname", event[0]
						.getLName()));
				postData(POST_EVENTS_URL, nameValuePairs);
				return null;
			}
		}.execute(event);
	}

	public static void getEvents(MapActivity mapAct) {
		map = mapAct;
		new AsyncTask<Void, Void, JSONArray>() {
			protected JSONArray doInBackground(Void... params) {
				JSONArray json = JSONfunctions.getJSONfromURL(GET_EVENTS_URL);
				Log.v("getEvents", json.toString());
				return json;
			}

			protected void onPostExecute(JSONArray json) {
				updateEventList(json);
			}
		}.execute();
	}

	public static void updateWithLocal() {
		Log.d("water", "got to updateWithLocal");
		new AsyncTask<Void, Void, JSONArray>() {
			protected JSONArray doInBackground(Void... params) {
				JSONArray json = null;
				try {
					json = JSONfunctions.getJSONfromURL(GET_EVENTS_URL);
				} catch (Exception e) {

				}
				return json;
			}

			protected void onPostExecute(JSONArray json) {
				EventCollection.removeAll();
				if (json != null) {
					updateEventList(json);
				}
				EventCollection.updateEvents();
			}
		}.execute();
	}

	/**
	 * Retrieve all events occurring between the current time and an interval
	 * 
	 * @param mapAct
	 * @param interval
	 *            The interval time specified in hours
	 */
	public static void getEvents(MapActivity mapAct, int interval) {
		map = mapAct;
		Timestamp startTime = new Timestamp(System.currentTimeMillis());
		Timestamp endTime = new Timestamp(System.currentTimeMillis() + interval
				* 60 * 60 * 1000);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("startTime", startTime
				.toString()));
		nameValuePairs
				.add(new BasicNameValuePair("endTime", endTime.toString()));
		new AsyncListWithURL(GET_EVENTS_TIME_URL, nameValuePairs).execute();
	}

	/**
	 * Retrieve all events between the startTime and endTime
	 * 
	 * @param mapAct
	 * @param startTime
	 *            format 'YYYY-MM-DD HH:MM:SS'
	 * @param endTime
	 *            format 'YYYY-MM-DD HH:MM:SS'
	 */
	public static void getEvents(MapActivity mapAct, String startTime,
			String endTime) {
		map = mapAct;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("startTime", startTime));
		nameValuePairs.add(new BasicNameValuePair("endTime", endTime));
		new AsyncListWithURL(GET_EVENTS_TIME_URL, nameValuePairs).execute();
	}

	/**
	 * Retrieve all events that match either the *name* or *description*, wild
	 * cards either end
	 * 
	 * @param mapAct
	 * @param name
	 *            The word(s) to be searched through the `name` column
	 * @param desc
	 *            The word(s) to be searched through the `desc` column
	 */
	public static void getEventsByNameDesc(MapActivity mapAct, String name,
			String desc) {
		map = mapAct;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("desc", desc));
		new AsyncListWithURL(GET_EVENTS_NAME_DESC_URL, nameValuePairs)
				.execute();
	}

	/**
	 * Retrieve all events that match a creator/user id
	 * 
	 * @param mapAct
	 * @param userID
	 *            The id of the user to retrieve the events
	 */
	public static void getEventsByUserID(MapActivity mapAct, int userID) {
		map = mapAct;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("userID", userID + ""));
		// asyncList(GET_EVENTS_USERID_URL, nameValuePairs);
		new AsyncListWithURL(GET_EVENTS_USERID_URL, nameValuePairs).execute();
	}

	/**
	 * Retrieve all events that match a creator/user id
	 * 
	 * @param mapAct
	 * @param userID
	 *            The id of the user to retrieve the events
	 */
	public static void removeEvent(Event e) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
		nameValuePairs.add(new BasicNameValuePair("eventTitle", e.getName()));
		nameValuePairs.add(new BasicNameValuePair("eventDesc", e.getDesc()));
		nameValuePairs.add(new BasicNameValuePair("eventLat", e.getLat() + ""));
		nameValuePairs.add(new BasicNameValuePair("eventLng", e.getLng() + ""));
		nameValuePairs
				.add(new BasicNameValuePair("creatorID", e.getCreatorId()));
		// asyncList(GET_EVENTS_USERID_URL, nameValuePairs);
		new AsyncListWithURL(REMOVE_EVENT_URL, nameValuePairs).execute();
	}

	/**
	 * Starts an AsyncTask to send an array of NameValuePairs to the url and
	 * updates the EventList with the JSON result from the url
	 * 
	 * @param url
	 * @param nameValuePairs
	 */
	public static void asyncList(String url, List<NameValuePair> nameValuePairs) {
		new AsyncTask<List<NameValuePair>, Void, JSONArray>() {
			protected JSONArray doInBackground(List<NameValuePair>... arg) {
				JSONArray json = JSONfunctions.getJSONfromURL(
						GET_EVENTS_USERID_URL, arg[0]);
				return json;
			}

			protected void onPostExecute(JSONArray json) {
				updateEventList(json);
			}
		}.execute(nameValuePairs);
	}

	public static void updateEventList(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject obj = jsonArray.getJSONObject(i);
				String name = obj.getString("NAME");
				String desc = obj.getString("DESC");
				String lat = obj.getString("LAT");
				String lng = obj.getString("LNG");
				String location = obj.getString("LOC");
				String year = obj.getString("YEAR");
				String month = obj.getString("MONTH");
				String day = obj.getString("DAY");
				String shour = obj.getString("SHOUR");
				String smin = obj.getString("SMINUTE");
				String ehour = obj.getString("EHOUR");
				String emin = obj.getString("EMINUTE");
				String creator_id = obj.getString("CREATOR_ID");
				String fname = obj.getString("FNAME");
				String lname = obj.getString("LNAME");

				// Add event to the eventList for the map activity
				Event e = new Event(name, desc, Double.parseDouble(lat),
						Double.parseDouble(lng), location, Integer.parseInt(year),
						Integer.parseInt(month), Integer.parseInt(day),
						Integer.parseInt(shour), Integer.parseInt(smin),
						Integer.parseInt(ehour), Integer.parseInt(emin),
						creator_id, fname, lname);
				EventCollection.addEvent(e);
				// MapActivity.eventList.add(new Event(Integer.parseInt(id),
				// name, desc, Double.parseDouble(lat),
				// Double.parseDouble(lng),start, end,
				// Integer.parseInt(creator_id)));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Refresh map activity when done updated eventList
		// map.refreshEventsToMap();
	}

	public static void postData(String url, List<NameValuePair> nameValuePairs) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	/**
	 * Replace all ' with '' in Strings sent to the http server.
	 * 
	 * @param str
	 *            The string to convert
	 */
	public static String sqlStrCheck(String str) {
		return str.replaceAll("\'", "\'\'");
	}
}
