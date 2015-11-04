package com.jhueventtrackr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventListArrayAdapter extends ArrayAdapter<String>{
	private final Context context;
	private final String[] names;
	private final String[] start;
	private final String[] desc;
	
	public EventListArrayAdapter(Context context, String[] names, String[] start, String[] desc){
		super(context, R.layout.eventlistrowlayout, names);
		this.context = context;
		this.names = names;
		this.start = start;
		this.desc = desc;
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.eventlistrowlayout, parent, false);
	    TextView textView1 = (TextView) rowView.findViewById(R.id.event_name);
	    TextView textView2 = (TextView) rowView.findViewById(R.id.event_start);
	    TextView textView3 = (TextView) rowView.findViewById(R.id.event_desc);
	    textView1.setText(names[position]);
	    textView2.setText(start[position]);
	    textView3.setText(desc[position]);

	    return rowView;
	  }
}
