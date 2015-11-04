package com.jhueventtrackr;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PrivacyFragmentActivity extends Activity {
	
	private static ArrayList<String> title;
	private ListView lv;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_fragment);
		String[] t = {"Share Location", "Display User Info"};
		lv = (ListView) findViewById(R.id.privacy_settings_list);
		title = new ArrayList<String>(Arrays.asList(t));
		adapter = new ArrayAdapter<String>(this, R.layout.list_item_2, R.id.textView1,
				title);
		lv.setAdapter(adapter);
		
	}

}
