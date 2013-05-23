package db.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import db.DataBaseHelper;

public class Task extends CRUD {
	public SQLiteDatabase db;
	public static final String tableName = "task";
	
	public Task() {
		super(tableName);
		db = DataBaseHelper.db;
	}

	public void markAsRead(String id) {
		db.delete("notizen", "id = ?", new String[] { id });
	}

	public void deleteAssignment(int id) {
		db.delete("notizen", "id = ?", new String[] { String.valueOf(id) });
	}

	public boolean checkNoteExist(String id) {
		Cursor cursor = db.query("notizen", null, "id = ?", new String[] { id }, null, null, null);
		if (cursor.moveToNext()) {
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	public ArrayList<Map<String, String>> getAll() {
		ArrayList<Map<String, String>> notes = new ArrayList<Map<String, String>>();
		Cursor cursor = db.query("notizen", null, null, null, null, null, "date ASC");
		String[] columnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> row = new HashMap<String, String>();
			for (int i = 0; i < columnNames.length; i++) {
				row.put(columnNames[i], cursor.getString(i));
			}
			notes.add(row);
		}
		cursor.close();
		return notes;
	}
}
