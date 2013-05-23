package com.studiplaner;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.studiplaner.db.DataBaseHelper;

public class Helper {
	public SQLiteDatabase db;
	public DataBaseHelper dbHelper;

	public static String getNullAsPrefix(String check) {
		return (new String(check).length() == 1 ? "0" + check : check);
	}

	public void markFirstAppStart() {
		ContentValues cv = new ContentValues();
		cv.put("appStarted", "1");
		db.insert("init", null, cv);
	}

	public void saveURL(String url) {
		ContentValues cv = new ContentValues();
		cv.put("url", url);
		db.delete("preferences", "url != ?", new String[] { "" });
		db.insert("preferences", null, cv);
	}

	public String getURL() {
		String url = null;
		Cursor cursor = db.query("preferences", new String[] { "url" }, "url != ?", new String[] { "" }, null, null, null);
		if (cursor.moveToNext()) {
			url = cursor.getString(0);
			cursor.close();
			return url;
		}
		return url;
	}

	public static boolean checkMail(String email) {
		String input = "@sun.com";
		// Checks for email addresses starting with
		// inappropriate symbols like dots or @ signs.
		Pattern p = Pattern.compile("^\\.|^\\@");
		Matcher m = p.matcher(input);
		if (m.find())
			return false;
		// Checks for email addresses that start with
		// www. and prints a message if it does.
		p = Pattern.compile("^www\\.");
		m = p.matcher(input);
		if (m.find()) {
			return false;
		}
		p = Pattern.compile("[^A-Za-z0-9\\.\\@_\\-~#]+");
		m = p.matcher(input);
		StringBuffer sb = new StringBuffer();
		boolean result = m.find();
		boolean deletedIllegalChars = false;

		while (result) {
			deletedIllegalChars = true;
			m.appendReplacement(sb, "");
			result = m.find();
		}

		// Add the last segment of input to the new String
		m.appendTail(sb);
		input = sb.toString();

		if (deletedIllegalChars) {
			return false;
		}

		return true;
	}

	public static String shortenString(String str, int length) {
		if (str.length() > length) {
			String newStr = str.substring(0, length);
			return newStr.concat("...");
		}
		return str;
	}
	
	public static boolean dateIsBetweenTwoDates(String begin, String end) {
		String[] splitBeginStr = begin.split(":");
		int hrBegin = Integer.parseInt(splitBeginStr[0]);
		int minBegin = Integer.parseInt(splitBeginStr[1]);

		String[] splitEndStr = end.split(":");
		int hrEnd = Integer.parseInt(splitEndStr[0]);
		int minEnd = Integer.parseInt(splitEndStr[1]);

		Calendar beginCal = Calendar.getInstance();
		beginCal.set(Calendar.HOUR_OF_DAY, hrBegin);
		beginCal.set(Calendar.MINUTE, minBegin);

		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.HOUR_OF_DAY, hrEnd);
		endCal.set(Calendar.MINUTE, minEnd);

		Calendar currentCal = Calendar.getInstance();

		if (beginCal.before(currentCal) && currentCal.before(endCal)) {
			return true;
		}

		return false;
	}
	
	public static boolean isPast(String end) {
		String[] splitEndStr = end.split(":");
		int hrEnd = Integer.parseInt(splitEndStr[0]);
		int minEnd = Integer.parseInt(splitEndStr[1]);
		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.HOUR_OF_DAY, hrEnd);
		endCal.set(Calendar.MINUTE, minEnd);
		Calendar currentCal = Calendar.getInstance();
		if (endCal.before(currentCal)) {
			return true;
		}
		return false;
	}

	public static void printMessage(final Activity activity, String contentMessage, String title, final boolean finishActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title);
		builder.setMessage(contentMessage);
		builder.setCancelable(true);
		builder.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (finishActivity) {
					activity.finish();
				}
			}
		});
		builder.show();
	}

	public static void sendEmail(Activity oActivity, String strTo, String strSubject, String strText) {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("text/plain");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { strTo });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, strSubject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, strText);
		oActivity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
}
