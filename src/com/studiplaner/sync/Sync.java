package com.studiplaner.sync;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

import com.studiplaner.sync.SyncHelper.ACTION;
import com.studiplaner.sync.SyncHelper.TYPE;

public class Sync extends AsyncTask<String, Integer, String> {

	@Override
	protected void onPreExecute() {
		// show UI
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// show current status
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(String result) {
		// hide loading UI
		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... par) {
		ArrayList<HashMap<String, String>> requestUrls = SyncHelper.buildUrlComponents();
		String jsonRequest = null;
		URL requestURL = null;
		for (HashMap<String, String> requestComponent : requestUrls) {
			try {
				// execute request
				TYPE syncType = TYPE.valueOf(requestComponent.get("type"));
				ACTION syncAction = ACTION.valueOf(requestComponent.get("action"));
				String syncTable = requestComponent.get("table");
				ArrayList<HashMap<String, String>> clientData = SyncHelper.getDBRowsForAction(syncAction, syncTable);
				jsonRequest = JSONHelper.generateJSONStr(clientData);

				requestURL = SyncHelper.buildURL(syncType, syncAction, syncTable, jsonRequest);
				String response = SyncHelper.getResponse(requestURL);
				JSONArray data = new JSONArray(response);

				// handle response
				switch (syncType) {
				case SyncClientToServer:
					JSONHelper.parseJSONResponse(syncAction, syncType, syncTable, data);
					break;
				case SyncServerToClient:
					ArrayList<HashMap<String, String>> requestData = JSONHelper.parseJSONResponse(syncAction, syncType, syncTable, data);
					jsonRequest = JSONHelper.generateJSONStr(requestData);
					switch (syncAction) {
					case update:
					case create: {
						requestURL = SyncHelper.buildURL(TYPE.SyncClientToServer, ACTION.update, syncTable, jsonRequest);
						SyncHelper.getResponse(requestURL);
					}
						break;
					case delete: {
						requestURL = SyncHelper.buildURL(TYPE.SyncClientToServer, ACTION.delete, syncTable, jsonRequest);
						SyncHelper.getResponse(requestURL);
					}
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
