package com.jhueventtrackr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONfunctions {
	//private static String seed = "[{\"ID\":\"161\",\"NAME\":\"RemoteDB Seed\",\"DESC\":\"Really fun event\",\"LAT\":\"39.332267\",\"LNG\":\"-76.620467\",\"START\":\"2014-04-18 08:58:24\",\"END\":\"2014-04-18 00:00:00\",\"CREATOR_ID\":\"2356\"}]";

	public static JSONArray getJSONfromURL(String url) {
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		JSONArray jsonArray = null;

		// Download JSON data from URL
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		}

		// Convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.v("JSON FUNCTIONS", result);
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}

		try {
			jsonArray = new JSONArray(result);
		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
		// return jArray;
		return jsonArray;
	}

	public static JSONArray getJSONfromURL(String url,
			List<NameValuePair> nameValuePairs) {
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;
		JSONArray jsonArray = null;

		// Download JSON data from URL
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			// Encodes the nameValuePairs into the HTTPPost - AW
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		}

		// Convert response to string
		try {
			Log.e("Jsonfunction(3)", "adding namevaluepair");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 14);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.v("JSON FUNCTIONS", result);
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		if (result.length() <= 6)
			jsonArray = null;

		// return jArray;
		return jsonArray;
	}
}
