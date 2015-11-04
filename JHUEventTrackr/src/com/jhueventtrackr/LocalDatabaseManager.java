package com.jhueventtrackr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDatabaseManager extends SQLiteOpenHelper {

	private static final String DB_NAME = "event.db";
	private static final String TABLE_EVENT = "event";

	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String LOC = "loc";
	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String DAY = "day";
	public static final String SHOUR = "shour";
	public static final String SMIN = "smin";
	public static final String EHOUR = "ehour";
	public static final String EMIN = "emin";
	public static final String C_ID = "c_id";
	public static final String FNAME = "fname";
	public static final String LNAME = "lname";
	public static final String ID = "id";

	public LocalDatabaseManager(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DB_NAME, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String DB_CREATE = "CREATE TABLE " + TABLE_EVENT + " (" + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT,"
				+ DESCRIPTION + " TEXT," + LAT + " TEXT," + LNG + " TEXT,"
				+ LOC + " TEXT,"
				+ YEAR + " TEXT," + MONTH + " TEXT," + DAY + " TEXT," + SHOUR
				+ " TEXT," + SMIN + " TEXT," + EHOUR + " TEXT," + EMIN + " TEXT,"
				+ C_ID + " TEXT," + FNAME + " TEXT," + LNAME + " TEXT);";
		db.execSQL(DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
		onCreate(db);

	}

	public void addEvent(Event e) {
		ContentValues cvalues = new ContentValues();
		cvalues.put(TITLE, e.getName());
		cvalues.put(DESCRIPTION, e.getDesc());
		cvalues.put(LAT, e.getLat() + "");
		cvalues.put(LNG, e.getLng() + "");
		cvalues.put(LOC, e.getLocation());
		cvalues.put(YEAR, e.getYear());
		cvalues.put(MONTH, e.getMonth());
		cvalues.put(DAY, e.getDay());
		cvalues.put(SHOUR, e.getSHour());
		cvalues.put(SMIN, e.getSMin());
		cvalues.put(EHOUR, e.getEHour());
		cvalues.put(EMIN, e.getEMin());
		cvalues.put(C_ID, e.getCreatorId());
		cvalues.put(FNAME, e.getFName());
		cvalues.put(LNAME, e.getLName());

		SQLiteDatabase db = this.getWritableDatabase();

		db.insert(TABLE_EVENT, null, cvalues);
		db.close();
	}

	public Cursor findAllEvents() {
		String query = "Select * FROM " + TABLE_EVENT;

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(query, null);

		cursor.moveToFirst();

		db.close();

		return cursor;
	}

	public void removeAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_EVENT, null, null);
		db.close();
	}

	public void deleteEvent(Event e) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(
				TABLE_EVENT,
				TITLE + "=? AND " + DESCRIPTION + "=? AND " + LAT + "=? AND "
						+ LNG + "=? AND " + C_ID + "=?",
				new String[] { e.getName(), e.getDesc(), e.getLat() + "",
						e.getLng() + "", e.getCreatorId() });
		db.close();
	}

}