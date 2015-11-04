package com.jhueventtrackr;

import com.google.android.gms.maps.model.LatLng;

/** Combined with Event.java class
 * 
 * @author Andrew
 *
 */
public class EventLoc {
	private String _eventName;
	private LatLng _latlng;
	private String _description;

	public EventLoc (String eventName, LatLng latlng, String description) {
		_eventName = eventName;
		_latlng = latlng;
		_description = description;
	}
	
	public String eventName() {
		return _eventName;
	}
	
	public LatLng latLng() {
		return _latlng;
	}
	
	public String description() {
		return _description;
	}
}
