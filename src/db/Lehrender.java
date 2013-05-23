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

import com.studiplaner.DataBaseHelper;
import com.studiplaner.Helper;
import com.studiplaner.R;
import com.studiplaner.R.string;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Lehrender {
	SQLiteDatabase db;
	Helper helper;
	Activity act;
	// erster Integer gibt Index im Spinner an, zweiter Integer Dozenten-Id in
	// der DB
	public Map<Integer, Integer> bindSpinnerToDb;

	public Lehrender(Activity activity) {
		act = activity;
		helper = new Helper(act.getApplicationContext());
	}

	public void openDBConnections() {
		helper.openDBConnections();
		DataBaseHelper dbHelper = new DataBaseHelper(act.getApplicationContext());
		db = dbHelper.getWritableDatabase();
	}

	public void closeDBConnections() {
		helper.closeDBConnections();
		db.close();
	}

	public void deleteTable() {
		db.delete("lehrender", null, null);
	}

	public boolean checkLecturerExist(String id) {
		Cursor cursor = db.query("lehrender", new String[] { "id" }, "id = ?", new String[] { id }, null, null, null);
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		return false;
	}

	public ArrayList<Map<String, String>> getAll() {
		ArrayList<Map<String, String>> lehrende = new ArrayList<Map<String, String>>();
		Cursor cursor = db.query("lehrender", null, null, null, null, null, "name");
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
		Cursor cursor = db.query("lehrender", null, null, null, null, null, "name");
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
		Cursor cursor = db.query("lehrender", new String[] { "name", "id" }, null, null, null, null, "name");
		String[] dozenten = new String[cursor.getCount() + 1];
		dozenten[0] = act.getString(R.string.not_selected);
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
		Cursor cursor = db.rawQuery("SELECT * FROM lehrender WHERE id = " + dozentId, null);
		while (cursor.moveToNext()) {
			String[] columnNames = cursor.getColumnNames();
			for (int i = 0; i < columnNames.length; i++) {
				dozent.put(columnNames[i], cursor.getString(i));
			}
		}
		cursor.close();
		return dozent;
	}
	
	public long saveDozent(String name, String room, String email) {
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("place", room);
		values.put("email", email);
		long insertedId = db.insert("lehrender", null, values);
		return insertedId;
	}
	
	public boolean deleteDozent(int lehrenderId) {
		ContentValues values = new ContentValues();
		values.put("id", "");
		db.delete("lehrender", "id = ?", new String[] { String.valueOf(lehrenderId) });
		db.update("veranstaltung", values, "lecturerId = ?", new String[] { String.valueOf(lehrenderId) });
		return true;
	}

	public boolean updateDozent(String name, String room, String email, int lehrenderID) {
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("place", room);
		values.put("email", email);
		db.update("lehrender", values, "id = ?", new String[] { String.valueOf(lehrenderID) });
		return true;
	}
}
