package com.studiplaner.db.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

public class Task extends CRUD {
	public static final String tableName = "task";
	
	public static String columnTitle = "title";
	public static String columnNote = "note";
	public static String columnDue = "due";
	public static String columnArchived = "archived";
	public static String columnTaskCategoryId = "task_category_id";
	public static String columnPosition = "position";

	public Task() {
		super(tableName);
	}

	public void archive(int id) {
		ContentValues v = new ContentValues();
		v.put(Task.columnArchived, "1");
		db.update(Task.tableName, v, "id = ?", new String[] {new Integer(id).toString()});
	}

	public void deleteAssignment(int id) {
		db.delete(Task.tableName, "id = ?", new String[] { String.valueOf(id) });
	}

	public ArrayList<Map<String, String>> getAll() {
		ArrayList<Map<String, String>> notes = new ArrayList<Map<String, String>>();
		Cursor cursor = db.query(Task.tableName, null, null, null, null, null, Task.columnCreated + " ASC");
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
