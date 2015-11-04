package com.jhueventtrackr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;

import android.os.AsyncTask;

public class AsyncListWithURL extends AsyncTask<Void, Void, JSONArray> {
	String url;
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	JSONArray json = null;

	public AsyncListWithURL(String url, List<NameValuePair> nameValuePairs) {
		this.url = url;
		this.nameValuePairs = nameValuePairs;
	}

	protected JSONArray doInBackground(Void... arg) {
		json = JSONfunctions.getJSONfromURL(url, nameValuePairs);
		return json;
	}

	protected void onPostExecute(JSONArray json) {
		if (json != null)
			RemoteDB.updateEventList(json);
	}
}
