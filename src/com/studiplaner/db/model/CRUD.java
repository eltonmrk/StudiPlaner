package com.studiplaner.db.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.studiplaner.db.DataBaseHelper;

public class CRUD {
	protected SQLiteDatabase db;
	private String tableName;

	public static String columnDeleted = "deleted";
	public static String columnUpdated = "updated";
	public static String columnSynchronized = "synchronized";
	public static String columnCreated = "created";
	public static String columnCsReference = "cs_reference";
	public static String columnId = "id";

	public static final String[] TABLES = new String[] { Lecturer.tableName,
														Semester.tableName,
														Subject.tableName,
														SubjectType.tableName,
														Task.tableName,
														TaskCategory.tableName,
														TaskTags.tableName };

	public CRUD(String table) {
		db = DataBaseHelper.db;
		tableName = table;
	}

	public CRUD() {
		db = DataBaseHelper.db;
	}

	public boolean delete(int id) {
		int result = db.delete(tableName, "id = ?", new String[] { String.valueOf(id) });
		if (result > 0)
			return true;
		else
			return false;
	}

	public boolean update(HashMap<String, String> data, String id) {
		ContentValues values = new ContentValues();
		for (String key : data.keySet()) {
			if (data.get(key) == null)
				values.putNull(key);
			else
				values.put(key, data.get(key));
		}
		int result = db.update(tableName, values, "id = ?", new String[] { id });

		if (result > 0)
			return true;
		else
			return false;
	}

	public int save(HashMap<String, String> data) {
		ContentValues values = new ContentValues();
		for (String key : data.keySet()) {
			values.put(key, data.get(key));
		}
		long id = db.insert(tableName, null, values);
		return (int) id;
	}

	public HashMap<String, String> get(int id) {
		HashMap<String, String> note = null;
		Cursor cursor = db.query(tableName, null, "id = ?", new String[] { String.valueOf(id) }, null, null, null);
		if (cursor.moveToNext()) {
			note = new HashMap<String, String>();
			String[] columnNames = cursor.getColumnNames();
			for (int i = 0; i < columnNames.length; i++) {
				if (cursor.getString(i) != null)
					note.put(columnNames[i], cursor.getString(i));
			}
		}
		cursor.close();
		return note;
	}

	public void deleteTable() {
		db.delete(tableName, null, null);
	}

	public ArrayList<HashMap<String, String>> executeQuery(String dbCriteria, String table) {
		ArrayList<HashMap<String, String>> updated = new ArrayList<HashMap<String, String>>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + dbCriteria, null);
		String[] columnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			HashMap<String, String> row = new HashMap<String, String>();
			for (int i = 0; i < columnNames.length; i++) {
				row.put(columnNames[i], cursor.getString(i));
			}
			updated.add(row);
		}
		return updated;
	}
}
