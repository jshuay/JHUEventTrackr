package com.jhueventtrackr;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class EventCollection {
	protected static ArrayList<Event> events;
	protected static ArrayList<Event> watching;
	protected static ArrayList<Event> created;

	private static LocalDatabaseManager ldm;
	private static WatchingDatabaseManager wdm;
	private static CreatedDatabaseManager cdm;

	protected static String userId = "";
	protected static String userFName = "";
	protected static String userLName = "";

	public EventCollection(Context context) {
		ldm = new LocalDatabaseManager(context, null, null, 34);
		wdm = new WatchingDatabaseManager(context, null, null, 18);
		cdm = new CreatedDatabaseManager(context, null, null, 18);

		events = new ArrayList<Event>();
		watching = new ArrayList<Event>();
		created = new ArrayList<Event>();
	}

	public static void updateCollection() {
		updateEvents();
		updateWatching();
		updateCreated();
	}

	public static void updateEvents() {
		Cursor cursor = ldm.findAllEvents();
		events.clear();
		if (cursor.moveToFirst()) {
			do {
				String title = cursor.getString(1);
				String desc = cursor.getString(2);
				double lat = Double.parseDouble(cursor.getString(3));
				double lng = Double.parseDouble(cursor.getString(4));
				String location = cursor.getString(5);
				int year = Integer.parseInt(cursor.getString(6));
				int month = Integer.parseInt(cursor.getString(7));
				int day = Integer.parseInt(cursor.getString(8));
				int shour = Integer.parseInt(cursor.getString(9));
				int smin = Integer.parseInt(cursor.getString(10));
				int ehour = Integer.parseInt(cursor.getString(11));
				int emin = Integer.parseInt(cursor.getString(12));
				String cId = cursor.getString(13);
				String fname = cursor.getString(14);
				String lname = cursor.getString(15);
				Event e = new Event(title, desc, lat, lng, location, year, month, day, shour, smin, ehour, emin, cId, fname, lname);
				events.add(e);
			} while (cursor.moveToNext());
		}

		cursor.close();
	}

	public static void updateWatching() {
		Cursor cursor = wdm.findAllEvents();
		watching.clear();
		if (cursor.moveToFirst()) {
			do {
				String title = cursor.getString(1);
				String desc = cursor.getString(2);
				double lat = Double.parseDouble(cursor.getString(3));
				double lng = Double.parseDouble(cursor.getString(4));
				String location = cursor.getString(5);
				int year = Integer.parseInt(cursor.getString(6));
				int month = Integer.parseInt(cursor.getString(7));
				int day = Integer.parseInt(cursor.getString(8));
				int shour = Integer.parseInt(cursor.getString(9));
				int smin = Integer.parseInt(cursor.getString(10));
				int ehour = Integer.parseInt(cursor.getString(11));
				int emin = Integer.parseInt(cursor.getString(12));
				String cId = cursor.getString(13);
				String fname = cursor.getString(14);
				String lname = cursor.getString(15);
				Event e = new Event(title, desc, lat, lng, location, year, month, day, shour, smin, ehour, emin, cId, fname, lname);
				watching.add(e);
			} while (cursor.moveToNext());
		}

		cursor.close();
	}

	public static void updateCreated() {
		Cursor cursor = cdm.findAllEvents();
		created.clear();
		if (cursor.moveToFirst()) {
			do {
				String title = cursor.getString(1);
				String desc = cursor.getString(2);
				double lat = Double.parseDouble(cursor.getString(3));
				double lng = Double.parseDouble(cursor.getString(4));
				String location = cursor.getString(5);
				int year = Integer.parseInt(cursor.getString(6));
				int month = Integer.parseInt(cursor.getString(7));
				int day = Integer.parseInt(cursor.getString(8));
				int shour = Integer.parseInt(cursor.getString(9));
				int smin = Integer.parseInt(cursor.getString(10));
				int ehour = Integer.parseInt(cursor.getString(11));
				int emin = Integer.parseInt(cursor.getString(12));
				String cId = cursor.getString(13);
				String fname = cursor.getString(14);
				String lname = cursor.getString(15);
				Event e = new Event(title, desc, lat, lng, location, year, month, day, shour, smin, ehour, emin, cId, fname, lname);
				created.add(e);
			} while (cursor.moveToNext());
		}

		cursor.close();
	}

	public static void updateWithServer() {
		RemoteDB.updateWithLocal();
	}

	public static void updateLocals() {
		updateCreated();
		updateWatching();
	}
	
	public static void updateLocalsAgainstServer() {
		Log.d("wednesday", "got here");
		for (Event e : watching) {
			if (!events.contains(e)) {
				wdm.deleteEvent(e);
			}
		}
		updateWatching();
		for (Event e : created) {
			if (!events.contains(e)) {
				cdm.deleteEvent(e);
			}
		}
		updateCreated();
	}

	public static void addEvent(Event e) {
		ldm.addEvent(e);
	}

	public static void addCreatedEvent(Event e) {
		addEvent(e);
		updateEvents();
		Log.d("gothere", "got passed addevent");
		cdm.addEvent(e);
		updateCreated();
		Log.d("gothere", "shoulding show up");
	}

	public static boolean addWatchingEvent(Event e) {
		if (watching.contains(e)) {
			return false;
		}
		wdm.addEvent(e);
		updateWatching();
		return true;
	}

	public static void deleteWatchingEvent(Event e) {
		wdm.deleteEvent(e);
		updateWatching();
	}
	
	public static boolean isWatching(Event e) {
		return watching.contains(e);
	}

	public static void deleteCreatedEvent(Event e) {
		Log.d("gothere", "delete createdevents is called");
		wdm.deleteEvent(e);
		Log.d("gothere", "watching size= " + watching.size());
		ldm.deleteEvent(e);
		cdm.deleteEvent(e);
		updateCollection();
	}

	public static void removeAll() {
		ldm.removeAll();
	}
	
	public static Event findEvent(LatLng ll) {
		for (Event e : events) {
			if (e.latlng.equals(ll)) {
				return e;
			}
		}
		return null;
	}
	
	public static boolean isCreator(Event e) {
		return created.contains(e);
	}

}
