/************
 *	Copyright (c) 2010 entwicklerherz.de
 *	
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *	
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */
package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.studiplaner.R;

public class Lecturer {
	private SQLiteDatabase db;
	private Activity thisActivity;
	public final static String dbName = "lecturer";
	
	// erste Integer gibt Index im Spinner an, zweite Integer Dozenten-Id in der DB
	public Map<Integer, Integer> bindSpinnerToDb;

	public Lecturer(Activity activity) {
		thisActivity = activity;
		db = DataBaseHelper.db;
	}

	public void deleteTable() {
		db.delete(Lecturer.dbName, null, null);
	}

	public boolean checkLecturerExist(String id) {
		Cursor cursor = db.query(Lecturer.dbName, new String[] { "id" }, "id = ?", new String[] { id }, null, null, null);
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	public ArrayList<Map<String, String>> getAll() {
		ArrayList<Map<String, String>> lehrende = new ArrayList<Map<String, String>>();
		Cursor cursor = db.query(Lecturer.dbName, null, null, null, null, null, "name");
		String[] columnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> lehrender = new HashMap<String, String>();
			for (int i = 0; i < columnNames.length; i++) {
				lehrender.put(columnNames[i], cursor.getString(i));
			}
			lehrende.add(lehrender);
		}
		cursor.close();
		return lehrende;
	}

	public ArrayList<Map<String, String>> getDozenten() {
		ArrayList<Map<String, String>> dozenten = new ArrayList<Map<String, String>>();
		Cursor cursor = db.query(Lecturer.dbName, null, null, null, null, null, "name");
		String[] columns = cursor.getColumnNames();
		char charToCmpare = 'A';
		String[] colomnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> dozentenRow = new HashMap<String, String>();
			if (cursor.getString(1) != null) {
				// put the seperator day
				if (!(cursor.getString(1).charAt(0) == charToCmpare)) {
					Map<String, String> letter = new HashMap<String, String>();
					letter.put("buchstabe", String.valueOf(cursor.getString(1).charAt(0)).toUpperCase());
					dozenten.add(letter);
				}
				for (int i = 0; i < colomnNames.length; i++) {
					String columnContent = cursor.getString(i);
					if (columnContent != null)
						if (!columnContent.equals("")) {
							if (columnContent.length() > 30)
								columnContent = columnContent.substring(0, 30) + "...";
							dozentenRow.put(colomnNames[i], columnContent);
						}
				}
			}
			charToCmpare = cursor.getString(1).charAt(0);
			dozenten.add(dozentenRow);
		}
		cursor.close();
		return dozenten;
	}

	public String[] getInstructorsForSpinner() {
		bindSpinnerToDb = new HashMap<Integer, Integer>();
		Cursor cursor = db.query(Lecturer.dbName, new String[] { "name", "id" }, null, null, null, null, "name");
		String[] dozenten = new String[cursor.getCount() + 1];
		dozenten[0] = thisActivity.getString(R.string.not_selected);
		bindSpinnerToDb.put(0, null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			bindSpinnerToDb.put(i + 1, cursor.getInt(1));
			dozenten[i + 1] = cursor.getString(0);
		}
		cursor.close();
		return dozenten;
	}
	
	public Integer getIndexForInstructorId(Integer id) {
		for (int i = 1; i < bindSpinnerToDb.size(); i++) {
			if (bindSpinnerToDb.get(i) == id)
				return i;
		}
		return 0;
	}
	
	/************************************************* CRUD Operations *************************************************/
	public Map<String, String> getDozent(int dozentId) {
		Map<String, String> dozent = new HashMap<String, String>();
		Cursor cursor = db.query(false, Lecturer.dbName, null, "id = ?", new String[]{new Integer(dozentId).toString(), null, null, null, null}, null, null, null, null);
		while (cursor.moveToNext()) {
			String[] columnNames = cursor.getColumnNames();
			for (int i = 0; i < columnNames.length; i++) {
				dozent.put(columnNames[i], cursor.getString(i));
			}
		}
		cursor.close();
		return dozent;
	}
	
	public long saveDozent(HashMap<String, String> data) {
		ContentValues values = new ContentValues();
		for (String key:data.keySet()) {
			values.put(key, data.get(key));
		}
		long insertedId = db.insert(Lecturer.dbName, null, values);
		return insertedId;
	}
	
	public boolean deleteDozent(int lehrenderId) {
		ContentValues values = new ContentValues();
		values.put("id", "");
		db.delete(Lecturer.dbName, "id = ?", new String[] { String.valueOf(lehrenderId) });
		db.update(Subject.dbName, values, "lecturer_id = ?", new String[] { String.valueOf(lehrenderId) });
		return true;
	}

	public boolean updateDozent(HashMap<String, String> data) {
		ContentValues values = new ContentValues();
		for (String key:data.keySet()) {
			values.put(key, data.get(key));
		}
		db.update(Lecturer.dbName, values, "id = ?", new String[] { String.valueOf(data.get("id"))});
		return true;
	}
}
