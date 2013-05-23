package db.model;

import java.util.HashMap;
import java.util.Map;

import db.DataBaseHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CRUD {
	SQLiteDatabase db;
	String tableName;

	public CRUD(String table) {
		db = DataBaseHelper.db;
		tableName = table;
	}

	public void delete(int id) {
		db.delete(tableName, "id = ?", new String[] { String.valueOf(id) });
	}

	public boolean update(HashMap<String, String> data) {
		ContentValues values = new ContentValues();
		for (String key : data.keySet()) {
			values.put(key, data.get(key));
		}
		db.update(tableName, values, "id = ?", new String[] { String.valueOf(data.get("id")) });
		return true;
	}

	public int save(HashMap<String, String> data) {
		ContentValues values = new ContentValues();
		for (String key : data.keySet()) {
			values.put(key, data.get(key));
		}
		long id = db.insert(tableName, null, values);
		return (int) id;
	}

	public Map<String, String> get(int id) {
		Map<String, String> note = new HashMap<String, String>();
		Cursor cursor = db.query(tableName, null, "id = ?", new String[] { String.valueOf(id) }, null, null, null);
		if (cursor.moveToNext()) {
			String[] columnNames = cursor.getColumnNames();
			for (int i = 0; i < columnNames.length; i++) {
				note.put(columnNames[i], cursor.getString(i));
			}
		}
		cursor.close();
		return note;
	}
}
