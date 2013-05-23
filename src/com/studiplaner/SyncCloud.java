package com.studiplaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

public class SyncCloud {
	public final URI syncURL = new URI("http://192.168.2.139:81/studiplaner/index.php?r=sync/syncClient");
	public Activity thisActivity;

	public SyncCloud(Activity activity) throws URISyntaxException, ClientProtocolException, JSONException, IOException {
		RestClient rc = new RestClient(syncURL);
		String response = rc.getHttpUrlContent();
		parseServerResponse(response);
		thisActivity = activity;
	}

	/**
	 * @param json
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>> parseServerResponse(String json)
			throws JSONException, ClientProtocolException, IOException {
		// DataModel:
		// HashMap<String, ...> = table name
		// HashMap<..., ArrayList<<HashMap<String, ... > = method name
		// HashMap<..., ArrayList<<HashMap<..., ArrayList>>>> = rows for method
		HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>> tables = new HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>>();
		JSONArray tablesJSONArr = new JSONArray(json);
		for (int i = 0; i < tablesJSONArr.length(); i++) {
			// 1) get table name
			JSONObject table = tablesJSONArr.getJSONObject(i);
			String tableName = table.toString();
			ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>> methodsWithRows = new ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>();

			// 2) get data of table
			JSONArray syncMethods = tablesJSONArr.getJSONArray(i);
			// iterate through sync-methods
			for (int a = 0; a < syncMethods.length(); a++) {
				HashMap<String, ArrayList<HashMap<String, String>>> methodWithRows = new HashMap<String, ArrayList<HashMap<String, String>>>();
				JSONObject methodName = syncMethods.getJSONObject(i);
				JSONArray rowJSONArr = syncMethods.getJSONArray(i);
				ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
				// iterate through rows
				for (int l = 0; l < rowJSONArr.length(); l++) {
					HashMap<String, String> row = new HashMap<String, String>();
					JSONObject rowJSONObj = rowJSONArr.getJSONObject(l);
					String[] columnNames = JSONObject.getNames(rowJSONObj);
					for (String rowColumn : columnNames) {
						row.put(rowColumn, rowJSONObj.getString(rowColumn));
					}
					rows.add(row);
				}
				methodWithRows.put(methodName.toString(), rows);
				methodsWithRows.add(methodWithRows);
			}
			tables.put(tableName, methodsWithRows);
		}
		return tables;
	}

	public void syncDB(HashMap<String, ArrayList<HashMap<String, ArrayList<HashMap<String, String>>>>> data) {

	}
}

class RestClient {
	public URI httpUrl;

	public RestClient(URI url) {
		httpUrl = url;
	}

	public String getHttpUrlContent() throws ClientProtocolException, IOException {
		String urlContents = "";
		HttpClient hc = new DefaultHttpClient();
		HttpUriRequest hur = new HttpGet(httpUrl);
		HttpResponse hr = hc.execute(hur);
		if (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 200 OK
			HttpEntity he = hr.getEntity();
			if (he != null) {
				InputStream is = he.getContent();
				urlContents = isToString(is);
			}
		}
		return urlContents;
	}

	public String isToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
}