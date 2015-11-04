package com.jhueventtrackr;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ToggleButton;

	// Allows for two TextViews in one list, so that the expenses can
	// be left aligned, and the prices right aligned. This took soooooo
    // long to figure out......
	public class CustomAdapter extends ArrayAdapter<String> {

		public CustomAdapter(Context context, int resource,
				int textViewResourceId1, List<String> o) {
			super(context, resource, textViewResourceId1, o);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			if(position == 3) {
				((ToggleButton) v.findViewById(R.id.gps_switch)).setVisibility(View.VISIBLE);
				((ToggleButton) v.findViewById(R.id.gps_switch)).setEnabled(true);
			}
			
			return v;
		}

	}