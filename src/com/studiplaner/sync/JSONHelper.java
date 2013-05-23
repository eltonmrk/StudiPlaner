package com.studiplaner.sync;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.studiplaner.db.model.CRUD;
import com.studiplaner.db.model.Lecturer;
import com.studiplaner.db.model.Semester;
import com.studiplaner.db.model.Subject;
import com.studiplaner.db.model.SubjectType;
import com.studiplaner.db.model.Task;
import com.studiplaner.db.model.TaskCategory;
import com.studiplaner.db.model.TaskTags;
import com.studiplaner.sync.SyncHelper.ACTION;

public class JSONHelper {

	public static String generateJSONStr(ArrayList<HashMap<String, String>> list) {
		JSONArray arr = new JSONArray();
		for (HashMap<String, String> listEl : list) {
			JSONObject obj = new JSONObject(listEl);
			arr.put(obj);
		}
		return arr.toString();
	}

	/**
	 * Scenario: client sent data to server and gets the receipt (sync dates)
	 * Parse DB Response for CTS, Local dates are updated or rows will be deleted, depending on action
	 * 
	 * @param action
	 * @param table
	 * @param responseStr
	 * @return
	 * @throws JSONException
	 */
	public static void parseCTSResponse(ACTION action, String table, String responseStr) throws JSONException {
		JSONArray myJSONArray = new JSONArray(responseStr);
		ArrayList<HashMap<String, String>> responseData = convertJSONToList(myJSONArray);
		CRUD model = getModel(table);

		for (HashMap<String, String> responseRow : responseData) {
			try {
				String csRef = responseRow.get(CRUD.columnCsReference);
				responseRow = JSONHelper.prepareData(responseRow);

				// parse string to number or throw exception
				if (csRef != null)
					Integer.valueOf(csRef);

				switch (action) {
				case update:
				case create:
					// set synchronized to server-side date & update to null
					model.update(responseRow, csRef);
					break;
				case delete:
					// delete client-row
					model.delete(Integer.valueOf(csRef));
					break;
				}
			} catch (NumberFormatException e) {

			}
		}
	}

	/**
	 * Scenarion: Client got data from server
	 * #1 Communication (Response) delivers data to the client (depending on action)
	 * #2 Communication (Request) sending receipts to the server (not implemented in this function)
	 * 
	 * @param action
	 * @param table
	 * @param responseStr
	 * @return receipt for server
	 * @throws JSONException
	 */
	public static ArrayList<HashMap<String, String>> parseS2CResponse(ACTION action, String table, String responseStr) throws JSONException {
		JSONArray myJSONArray = new JSONArray(responseStr);
		ArrayList<HashMap<String, String>> responseData = convertJSONToList(myJSONArray);
		CRUD model = getModel(table);
		ArrayList<HashMap<String, String>> receipts = new ArrayList<HashMap<String, String>>();

		for (HashMap<String, String> remoteDBRow : responseData) {
			
			boolean updated = false;
			boolean created = false;
			boolean deleted = false;
			
			try {
				String csRef = remoteDBRow.get(CRUD.columnCsReference);
				String remoteRef = remoteDBRow.get(CRUD.columnId);
				remoteDBRow = prepareData(remoteDBRow);

				// parse string to number or throw exception
				// check for csref, maybe new created server-side data hasn't been synced already
				if (csRef != null)
					Integer.valueOf(csRef);
				if (remoteRef != null)
					Integer.valueOf(remoteRef);

				// current timestamp
				long timestamp = JSONHelper.getCurrentDateInMillis();
				remoteDBRow.put(CRUD.columnSynchronized, String.valueOf(timestamp));
				switch (action) {
				case update:
					updated = model.update(remoteDBRow, csRef);
					break;
				case create:
					long result = model.save(remoteDBRow);
					created = result == -1 ? false : true;
					break;
				case delete:
					deleted = model.delete(Integer.valueOf(csRef));
					break;
				}
				
				// add to receipt
				if (updated || created || deleted) {
					HashMap<String, String> response = new HashMap<String, String>();
					response.put(CRUD.columnCsReference, String.valueOf(timestamp));
					response.put(CRUD.columnId, remoteRef);
					receipts.add(response);
				}
			} catch (NumberFormatException e) {
			}
		}
		
		return receipts;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public static HashMap<String, String> prepareData(HashMap<String, String> map) {
		map.put(CRUD.columnUpdated, null);
		if (map.containsKey(CRUD.columnId)) {
			map.remove(CRUD.columnId);
		}
		return map;
	}

	public static long getCurrentDateInMillis() {
		long currentTime = (long) (Calendar.getInstance().getTimeInMillis() / 1000);
		return currentTime;
	}

	public static CRUD getModel(String tableName) {
		CRUD model = null;
		if (tableName.equalsIgnoreCase(Lecturer.tableName))
			model = new Lecturer();
		if (tableName.equalsIgnoreCase(Semester.tableName))
			model = new Semester();
		if (tableName.equalsIgnoreCase(Subject.tableName))
			model = new Subject();
		if (tableName.equalsIgnoreCase(SubjectType.tableName))
			model = new SubjectType();
		if (tableName.equalsIgnoreCase(Task.tableName))
			model = new Task();
		if (tableName.equalsIgnoreCase(TaskCategory.tableName))
			model = new TaskCategory();
		if (tableName.equalsIgnoreCase(TaskTags.tableName))
			model = new TaskTags();
		return model;
	}

	public static ArrayList<HashMap<String, String>> convertJSONToList(JSONArray data) throws JSONException {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < data.length(); i++) {
			JSONObject obj = data.getJSONObject(i);
			HashMap<String, String> map = new HashMap<String, String>();
			for (int b = 0; b < obj.length(); b++) {
				Iterator<String> keys = obj.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					map.put(key, obj.getString(key));
				}
			}
			list.add(map);
		}
		return list;
	}
}
