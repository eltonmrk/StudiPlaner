package com.studiplaner.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import com.studiplaner.db.model.CRUD;

public class SyncHelper extends JSONHelper {
	// credential URL values
	public static final String USER_TEST_LOGIN = "tester";
	public static final String USER_TEST_PASS = "tester";

	// URL components
	public static final String URL_PREFIX = "http://10.7.242.14:81/"; // at uni
	public static final String URL_POSTFIX = "studiplaner/index-test.php/api/";
	public static final String LOGIN_ATTR = "login"; // login attr
	public static final String PASS_ATTR = "pass"; // pass attr (md5)
	public static final String TABLE_ATTR = "table"; // table attr
	public static final String DATA_ATTR = "data"; // data attr
	public static final String ACTION_ATTR = "action"; // action attr (only for S2C-Sync)
	public static final String GET_ATTR = "get"; // get attr (only for S2C)

	public static enum ACTION {
		create, update, delete
	};
	
	// here is the direction ment date is flowing
	// C2S send data to the server
	// S2C request data from server
	public static enum TYPE {
		C2S, S2C
	}

	/*
	 * Build URL using the parameters
	 * 
	 * @return URL
	 */
	public static URL buildURL(TYPE type, ACTION action, String table, String jsonData) throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException {
		String requestData = jsonData != null ? URLEncoder.encode(jsonData) : null;
		String urlStr = null;
		String loginURL = LOGIN_ATTR + "=" + USER_TEST_LOGIN + "&" + PASS_ATTR + "=" + SyncHelper.getmd5FromString(USER_TEST_PASS);
		switch (type) {
		case S2C:
			urlStr = URL_PREFIX + URL_POSTFIX + GET_ATTR + "?" + ACTION_ATTR + "=" + action + "&" + TABLE_ATTR + "=" + table + "&" + loginURL;
			break;
		case C2S:
			urlStr = URL_PREFIX + URL_POSTFIX + action + "?" + TABLE_ATTR + "=" + table + "&" + DATA_ATTR + "=" + requestData + "&" + loginURL;
			break;
		}
		URL url = new URL(urlStr);
		return url;
	}

	/**
	 * Only for CTS Get data value for CTS Request
	 * 
	 * @param syncAction
	 * @param table
	 * @return JSON String
	 */
	public static ArrayList<HashMap<String, String>> getDataForC2SAction(ACTION action, String table) {
		CRUD model = new CRUD();
		String dynamicQuery = null;

		switch (action) {
		case create:
			dynamicQuery = "synchronized IS NULL";
			break;
		case delete:
			dynamicQuery = "deleted IS NOT NULL";
			break;
		case update:
			dynamicQuery = "updated IS NULL";
			break;
		}
		
		ArrayList<HashMap<String, String>> clientData = model.executeQuery(dynamicQuery, table);
		return clientData;
	}

	/**
	 * Create URL Requests for the combination type (C2S/S2C), action (CRUD),
	 * table
	 * 
	 * @return ArrayList with URL components in HashMap
	 */
	public static ArrayList<HashMap<String, String>> buildUrlComponents() {
		ArrayList<HashMap<String, String>> urlComponents = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < ACTION.values().length; i++) {
			for (int b = 0; b < CRUD.TABLES.length; b++) {
				HashMap<String, String> urlComponent = new HashMap<String, String>();
				String action = String.valueOf((ACTION.values())[i]);
				String table = CRUD.TABLES[b];
				urlComponent.put("action", action);
				urlComponent.put("table", table);
				urlComponents.add(urlComponent);
			}
		}
		return urlComponents;
	}
	
	/************************* HTTP Helper ***************************/
	public static String getResponse(URL url) throws IOException {
		StringBuilder response = new StringBuilder();
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader bf = new BufferedReader(new InputStreamReader(httpConn.getInputStream()), 8192);
			String strLine = null;
			while ((strLine = bf.readLine()) != null) {
				response.append(strLine);
			}
			bf.close();
		}
		return response.toString();
	}
	
	public static String getmd5FromString(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] strBytes = str.getBytes();
		byte[] digest = md5.digest(strBytes);
		BigInteger bigInt = new BigInteger(1, digest);
		String hashText = bigInt.toString(16);
		return hashText;
	}
}
