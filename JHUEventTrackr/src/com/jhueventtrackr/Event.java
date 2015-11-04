package com.jhueventtrackr;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

//import com.google.android.gms.maps.model.LatLng;

public class Event {
	protected int id;
	protected String name;
	protected String desc;
	protected String latlngStr;
	protected double lat;
	protected double lng;
	protected String location;
	protected LatLng latlng;
	protected Timestamp start;
	protected Timestamp end;
	protected String creatorId;
	private int year;
	private int day;
	private int month;
	private int sHour;
	private int sMin;
	private int eHour;
	private int eMin;
	private String fName;
	private String lName;

	// public Event(String name, String desc, String latlng, String start,
	// String end, String creatorId) {
	// this.name = name;
	// this.desc = desc;
	// this.latlngStr = latlng;
	// this.start = Timestamp.valueOf(start);
	// this.end = Timestamp.valueOf(end);
	// this.creatorId = creatorId;
	// }
	//
	// public Event(String name, String desc, Double lat, Double lng,
	// String start, String end, String creatorId) {
	// this.name = name;
	// this.desc = desc;
	// this.lat = lat;
	// this.lng = lng;
	// this.latlng = new LatLng(lat, lng);
	// this.start = Timestamp.valueOf(start);
	// this.end = Timestamp.valueOf(end);
	// this.creatorId = creatorId;
	// }
	//
	// public Event(int id, String name, String desc, Double lat, Double lng,
	// String start, String end, String creatorId) {
	// this.id = id;
	// this.name = name;
	// this.desc = desc;
	// this.lat = lat;
	// this.lng = lng;
	// this.latlng = new LatLng(lat, lng);
	// this.start = Timestamp.valueOf(start);
	// this.end = Timestamp.valueOf(end);
	// this.creatorId = creatorId;
	// }
	//
	// public Event(String name, String desc, Double lat, Double lng, Long
	// start,
	// Long end, String creatorId) {
	// this.name = name;
	// this.desc = desc;
	// this.lat = lat;
	// this.lng = lng;
	// this.latlng = new LatLng(lat, lng);
	// this.start = new Timestamp(start);
	// this.end = new Timestamp(end);
	// this.creatorId = creatorId;
	// }

	public Event(String name, String desc, Double lat, Double lng,
			String location, int year, int month, int day, int sHour, int sMin,
			int eHour, int eMin, String creatorId, String fname, String lname) {
		this.name = name;
		this.desc = desc;
		this.lat = lat;
		this.lng = lng;
		this.location = location;
		this.latlng = new LatLng(lat, lng);
		this.day = day;
		this.month = month;
		this.year = year;
		this.sHour = sHour;
		this.sMin = sMin;
		this.eHour = eHour;
		this.eMin = eMin;
		try {
			Date formater = new SimpleDateFormat("MM dd yyyy hh mm")
					.parse(month + 1 + " " + day + " " + year + " " + sHour
							+ " " + sMin);
			this.start = new Timestamp(formater.getTime());
			formater = new SimpleDateFormat("MM dd yyyy hh mm").parse(month + 1
					+ " " + day + " " + year + " " + eHour + " " + eMin);
			this.end = new Timestamp(formater.getTime());
		} catch (Exception e) {

		}
		this.latlng = new LatLng(lat, lng);
		this.creatorId = creatorId;
		this.fName = fname;
		this.lName = lname;
	}

	public static LatLng getLatLng() {
		return null;
	}

	/**
	 * Return event start time in a readable format: 'Mon 4/28 at 12:34 pm'
	 * 
	 * @return
	 */
	public String readableStartTime() {
		Date date = new Date(start.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("E M/d 'from' hh:mm a");
		return sdf.format(date);
	}

	public String readableEndTime() {
		Date date = new Date(end.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("' to' hh:mm a");
		return sdf.format(date);
	}

	public String getName() {
		return name;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int getSHour() {
		return sHour;
	}

	public int getSMin() {
		return sMin;
	}

	public int getEHour() {
		return eHour;
	}

	public int getEMin() {
		return eMin;
	}

	public String getDesc() {
		return desc;
	}

	public String getDescWithName() {
		return desc + "\n\nEvent Creator:\n" + fName + " " + lName;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public String getLocation() {
		return location;
	}

	public Timestamp getStart() {
		return start;
	}

	public Timestamp getEnd() {
		return end;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public String getFName() {
		return fName;
	}

	public String getLName() {
		return lName;
	}

	public String toString() {
		return "Name: " + name + ", description: " + desc + ", lat: " + lat
				+ ", lng: " + lng + ", location: " + location + ", start: "
				+ start.toString() + ", end: " + end.toString() + ", id: "
				+ creatorId + ", name: " + fName + " " + lName;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Event) {
			Event e = (Event) o;
			return this.toString().equals(e.toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		Double d1 = new Double(lat);
		Double d2 = new Double(lng);
		return d1.intValue() + d2.intValue() + year + month + day + sHour
				+ sMin + eHour + eMin;
	}

}
