package com.jhueventtrackr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectLocationActivity extends Activity {

	private static final int RESULT_OK = 0;
	private Activity a = this;
	private double lat, lng, tempLat, tempLng;
	private Marker marker;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_location);
		Bundle extras = getIntent().getExtras();
		lat = extras.getDouble("lat");
		lng = extras.getDouble("lng");
		tempLat = 0.0;
		tempLng = 0.0;
		initMap();
		marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
	}
	
	public void initMap() {
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2))
				.getMap();
		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(
				39.3296454, -76.6199633)).tilt(60).zoom(17).bearing(0).build();
		map.setBuildingsEnabled(true);
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.getUiSettings().setAllGesturesEnabled(true);
		map.getUiSettings().setTiltGesturesEnabled(false);
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng coord) {
				tempLat = coord.latitude;
				tempLng = coord.longitude;
				if (marker != null) {
					marker.remove();
				}
				marker = map.addMarker(new MarkerOptions().position(coord));
				CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), map.getCameraPosition().zoom);
				map.moveCamera(cu);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(a);
				builder.setTitle("Confirm Location");
				builder.setMessage("Are you sure you want to create an event at this location?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   lat = tempLat;
				        	   lng = tempLng;
				               finish();
				           }
				       });
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               // User cancelled the dialog
				           }
				       });
				AlertDialog dialog = builder.create();
				Window window = dialog.getWindow();
				WindowManager.LayoutParams wlp = window.getAttributes();

				wlp.gravity = Gravity.TOP;
				wlp.y = 170;
				wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
				window.setAttributes(wlp);
				dialog.show();
			}
			
		});
	}

	public static int getResultOkCode() {
		return RESULT_OK;
	}

	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra("lat", lat);
		intent.putExtra("lng", lng);
		setResult(RESULT_OK, intent);
		super.finish();
	}
}
