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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Semester {
	private SQLiteDatabase db;
	public Map<Integer, Integer> bindSpinnerToDb;
	
	public static final String dbName = "semester";

	public Semester() {
		db = DataBaseHelper.db;
	}

	public void deleteTable() {
		db.delete(Semester.dbName, null, null);
	}

	public Map<String, String> getCurrentSemester() {
		Map<String, String> currentSemester = new HashMap<String, String>();
		Cursor cursor = db.rawQuery(
						"SELECT * " +
								"FROM " + Semester.dbName + " AS s " +
								"WHERE date(s.begin) <= date('now') AND date(s.end) >= date('now') " +
								"ORDER BY s.begin LIMIT 0,1", null);
		while (cursor.moveToNext()) {
			String[] names = cursor.getColumnNames();
			for (int i = 0; i < names.length; i++) {
				currentSemester.put(names[i], cursor.getString(i));
			}
		}
		cursor.close();
		return currentSemester;
	}

	public ArrayList<Map<String, String>> getSemestersForView() {
		ArrayList<Map<String, String>> semesters = new ArrayList<Map<String, String>>();
		Cursor cursor = db.query(Semester.dbName, new String[] { "strftime('%Y', begin) AS year", "*" }, null, null, null, null, "begin");
		String yearToCompare = "";
		String[] colomnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> subjectRow = new HashMap<String, String>();
			if (cursor.getString(0) != null) {
				// put the seperator day
				if (!cursor.getString(0).equals(yearToCompare)) {
					Map<String, String> day = new HashMap<String, String>();
					day.put("jahr", cursor.getString(0));
					semesters.add(day);
				}
				for (int i = 0; i < colomnNames.length; i++) {
					String columnContent = cursor.getString(i);
					if (columnContent != null)
						if (columnContent.length() > 30)
							columnContent = columnContent.substring(0, 30) + "...";

					subjectRow.put(colomnNames[i], columnContent);
				}
			}
			yearToCompare = cursor.getString(0);
			semesters.add(subjectRow);
		}
		cursor.close();
		return semesters;
	}

	public ArrayList<Map<String, String>> getAll() {
		ArrayList<Map<String, String>> semesters = new ArrayList<Map<String, String>>();
		Cursor cursor = db.query(Semester.dbName, null, null, null, null, null, "begin");
		String[] columns = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> semester = new HashMap<String, String>();
			for (int i = 0; i < columns.length; i++) {
				semester.put(columns[i], cursor.getString(i));
			}
			semesters.add(semester);
		}
		cursor.close();
		return semesters;
	}

	public Integer countSemesterSubjects(Integer id) {
		if (id != null) {
			Cursor cursor = db.query(Subject.dbName, new String[] { "id" }, "semesterId = ?", new String[] { String.valueOf(id) }, null, null, null);
			int countSemester = cursor.getCount();
			cursor.close();
			return countSemester;
		}
		return 0;
	}
	
	/************************************************* CRUD Operations *************************************************/
	public void deleteSemester(int semesterId) {
		db.delete(Semester.dbName, "id = ?", new String[] { String.valueOf(semesterId) });
		db.delete(Subject.dbName, "semesterId = ?", new String[] { String.valueOf(semesterId) });
	}

	public Map<String, String> getSemester(int semesterId) {
		Map<String, String> semester = new HashMap<String, String>();
		Cursor cursor = db.query(Semester.dbName, null, "id = ?", new String[] { String.valueOf(semesterId) }, null, null, null);
		String[] columns = cursor.getColumnNames();
		if (cursor.moveToNext()) {
			for (int i = 0; i < columns.length; i++) {
				semester.put(columns[i], cursor.getString(i));
			}
		}
		cursor.close();
		return semester;
	}
	
	public long saveSemester(String name, String dateBegin, String dateEnd) {
		ContentValues values = new ContentValues();
		values.put("title", name);
		values.put("begin", dateBegin);
		values.put("end", dateEnd);
		long insertedID = db.insert(Semester.dbName, null, values);
		return insertedID;
	}

	public boolean updateSemester(String name, String dateBegin, String dateEnd, int semesterId) {
		ContentValues values = new ContentValues();
		values.put("title", name);
		values.put("begin", dateBegin);
		values.put("end", dateEnd);
		db.update(Semester.dbName, values, "id = ?", new String[] { String.valueOf(semesterId) });
		return true;
	}
}
