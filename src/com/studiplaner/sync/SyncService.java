package com.studiplaner.sync;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.studiplaner.sync.SyncHelper.ACTION;
import com.studiplaner.sync.SyncHelper.TYPE;

public class SyncService extends Service {

	@Override
	public void onCreate() {
		ArrayList<HashMap<String, String>> requestUrls = SyncHelper.buildUrlComponents();
		// loop through the url components and do the request
		for (HashMap<String, String> requestComponent : requestUrls) {
			try {
				// execute request
				TYPE syncType = TYPE.valueOf(requestComponent.get("type"));
				ACTION syncAction = ACTION.valueOf(requestComponent.get("action"));
				String syncTable = requestComponent.get("table");

				if (syncType == TYPE.C2S) {
					
					// send unsynced local data to server
					// get receipt
					ArrayList<HashMap<String, String>> clientData = SyncHelper.getDataForC2SAction(syncAction, syncTable);
					String jsonData = JSONHelper.generateJSONStr(clientData);
					URL requestURL = SyncHelper.buildURL(syncType, syncAction, syncTable, jsonData);
					String response = SyncHelper.getResponse(requestURL);
					JSONHelper.parseCTSResponse(syncAction, syncTable, response);
					
				} else if (syncType == TYPE.S2C) {
					
					// get unsynced data from server
					// save unsynced server-side data local
					// send receipt
					URL requestURLGet = SyncHelper.buildURL(syncType, syncAction, syncTable, null);
					String response = SyncHelper.getResponse(requestURLGet);
					ArrayList<HashMap<String, String>> receipt = SyncHelper.parseS2CResponse(syncAction, syncTable, response);
					String jsonData = JSONHelper.generateJSONStr(receipt);
					URL requestURLReceipt = SyncHelper.buildURL(syncType, syncAction, syncTable, jsonData);
					SyncHelper.getResponse(requestURLReceipt);
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
