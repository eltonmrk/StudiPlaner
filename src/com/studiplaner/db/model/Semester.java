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
package com.studiplaner.db.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;

public class Semester extends CRUD {
	public Map<Integer, Integer> bindSpinnerToDb;

	public static final String tableName = "semester";

	public static String columnBegin = "begin";
	public static String columnEnd = "end";
	public static String columnTitle = "title";
	public static String columnUserId = "user_id";

	public Semester() {
		super(tableName);
	}

	public Map<String, String> getCurrentSemester() {
		Map<String, String> currentSemester = new HashMap<String, String>();
		Cursor cursor = db.rawQuery("SELECT * " + "FROM " + Semester.tableName + " AS s "
				+ "WHERE date(s.begin) <= date('now') AND date(s.end) >= date('now') " + "ORDER BY s.begin LIMIT 0,1", null);
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
		Cursor cursor = db
				.query(Semester.tableName, new String[] { "strftime('%Y', begin) AS year", "*" }, null, null, null, null, "begin");
		String yearToCompare = "";
		String[] colomnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> subjectRow = new HashMap<String, String>();
			if (cursor.getString(0) != null) {
				// separate by day
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
		Cursor cursor = db.query(Semester.tableName, null, null, null, null, null, "begin");
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

	public Integer countSemesterSubjects(int id) {
		Cursor cursor = db.query(Subject.tableName, new String[] { "id" }, Subject.columnSemesterId + " = ?",
				new String[] { String.valueOf(id) }, null, null, null);
		int countSemester = cursor.getCount();
		cursor.close();
		return countSemester;
	}

	@Override
	public boolean delete(int id) {
		int deleted = db.delete(Semester.tableName, "id = ?", new String[] { String.valueOf(id) });
		db.delete(Subject.tableName, Subject.columnSemesterId + "= ?", new String[] { String.valueOf(id) });
		if (deleted > 0)
			return true;
		else
			return false;
	}
}
