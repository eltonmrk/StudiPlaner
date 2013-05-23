package com.studiplaner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Notizen {
	public SQLiteDatabase db;
	Context cont;

	public Notizen(Context context) {
		cont = context;
	}
	
	public void closeDBConnections(){
		db.close();
	}
	
	public void openDBConnections(){
		DataBaseHelper dbHelper = new DataBaseHelper(cont);
		db = dbHelper.getWritableDatabase();
	}

	public void markAsRead(String noteId){
		db.delete("notizen", "id = ?", new String[]{noteId});
	}
	
	public Map<String,String> getNote(int id){
		Map<String, String> note = new HashMap<String, String>();
		Cursor cursor = db.query("notizen", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
		if (cursor.moveToNext()){
			String[] columnNames = cursor.getColumnNames();
			for (int i = 0; i < columnNames.length; i++){
				note.put(columnNames[i], cursor.getString(i));
			}
		}
		return note;
	}
	
	public void deleteAssignment(int id){
		db.delete("notizen", "id = ?", new String[] { String.valueOf(id) });
	}
	
	public void deleteTable(){
		db.delete("notizen", null, null);
	}
	
	public boolean updateNotice(int id, String title, String descr, String tags, int archiv){
		Calendar rightNow = Calendar.getInstance();
		ContentValues cv = new ContentValues();
		cv.put("title", title);
		cv.put("content", descr);
		cv.put("tags", tags);
		cv.put("archived", String.valueOf(archiv));
		cv.put("date", String.valueOf(rightNow.get(Calendar.YEAR)) +"-"+ String.valueOf(rightNow.get(Calendar.MONTH) + 1) +"-"+ String.valueOf(rightNow.get(Calendar.DAY_OF_MONTH)));
		db.update("notizen", cv, "id = ?", new String[]{String.valueOf(id)});
		return true;
	}
	
	public void saveNotice(String title, String descr, String tags, int archiv){
		Calendar rightNow = Calendar.getInstance();
		ContentValues cv = new ContentValues();
		cv.put("title", title);
		cv.put("content", descr);
		cv.put("tags", tags);
		cv.put("date", String.valueOf(rightNow.get(Calendar.YEAR)) +"-"+ String.valueOf(rightNow.get(Calendar.MONTH) + 1) +"-"+ String.valueOf(rightNow.get(Calendar.DAY_OF_MONTH)));
		cv.put("archived", String.valueOf(archiv));
		db.insert("notizen", null, cv);
	}
	
	public boolean checkNoteExist(String id){
		Cursor cursor = db.query("notizen", null, "id = ?", new String[]{id}, null, null, null);
		if(cursor.moveToNext()){
			cursor.close();
			return true;
		}
		return false;
	}
	
	public ArrayList<Map<String, String>> getAll(){
		ArrayList<Map<String, String>> notes = new ArrayList<Map<String, String>>();
		Cursor cursor = db.query("notizen", null, null, null, null, null, "date ASC");
		String[] columnNames = cursor.getColumnNames();
		while(cursor.moveToNext())
		{
			Map<String, String> row = new HashMap<String, String>();
			for(int i = 0; i < columnNames.length; i++){
				row.put(columnNames[i], cursor.getString(i));
			}
			notes.add(row);
		}
		cursor.close();
		return notes;
	}
}
