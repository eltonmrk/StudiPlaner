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

package db.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import db.DataBaseHelper;

public class Subject extends CRUD{
	private SQLiteDatabase db;
	private final int cutStringAfterNChars = 30;
	private Map<Integer, ArrayList<Map<String, String>>> subjectsForWeek;

	public static final String tableName = "subject";

	public Subject() {
		super(tableName);
		db = DataBaseHelper.db;
	}

	public void deleteTable() {
		db.delete(tableName, null, null);
	}

	public int countSubjectsForDay(int id, int day) {
		Cursor cursor = db.rawQuery("SELECT COUNT(*) " + "FROM " + Semester.tableName + " AS s " + "LEFT JOIN " + Subject.tableName
				+ " AS v ON (s.id = v.semesterId) " + "WHERE v.day = " + day + " AND s.id = " + id, null);
		cursor.moveToNext();
		int count = Integer.parseInt(cursor.getString(0));
		cursor.close();
		return count;
	}

	public boolean isNow(String begin, String end, int day) {
		int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		if (today != day)
			return false;

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

	public boolean isPast(String end) {
		int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

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

	public ArrayList<Map<String, String>> getSubjectsForView(int id) {
		ArrayList<Map<String, String>> subjects = new ArrayList<Map<String, String>>();
		Cursor cursor = db.rawQuery("SELECT v.* " + "FROM " + Semester.tableName + " AS s " + "LEFT JOIN " + Subject.tableName
				+ " AS v ON (s.id = v.semesterId) " + "WHERE v.semesterId = " + id + " " + "ORDER BY v.day ", null);
		String dayToCompare = "";
		String[] colomnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> subjectRow = new HashMap<String, String>();
			if (cursor.getString(0) != null) {
				// put the seperator day
				if (!cursor.getString(6).equals(dayToCompare)) {
					Map<String, String> day = new HashMap<String, String>();
					day.put("day", cursor.getString(6));
					subjects.add(day);
				}
				for (int i = 0; i < colomnNames.length; i++) {
					String columnContent = cursor.getString(i);
					if (columnContent != null) {
						if (!columnContent.equals("")) {
							if (columnContent.length() > cutStringAfterNChars) {
								columnContent = columnContent.substring(0, cutStringAfterNChars) + "...";
							}
							subjectRow.put(colomnNames[i], columnContent);
						}
					}
				}
			}
			dayToCompare = cursor.getString(6);
			subjects.add(subjectRow);
		}
		cursor.close();
		return subjects;
	}

	public ArrayList<Map<String, String>> getSubjects(Integer id) {
		ArrayList<Map<String, String>> subjects = new ArrayList<Map<String, String>>();
		if (id != null) {
			Cursor cursor = db.rawQuery("SELECT v.*, v.place AS vPlace, l.*, v.id AS veranstaltungId " + "FROM " + Subject.tableName
					+ " AS v " + "LEFT JOIN " + Lecturer.tableName + " AS l ON(v.lecturerId = l.id) " + "WHERE v.semesterId = " + id + " "
					+ "ORDER BY v.begin", null);
			String[] columnNames = cursor.getColumnNames();
			String dayToCompare = "";
			while (cursor.moveToNext()) {
				Map<String, String> subject = new HashMap<String, String>();
				// put the seperator day
				if (!cursor.getString(6).equals(dayToCompare)) {
					Map<String, String> day = new HashMap<String, String>();
					day.put("day", cursor.getString(6));
					subjects.add(day);
				}
				for (int i = 0; i < columnNames.length; i++) {
					if (cursor.getString(i) != null) {
						if (!cursor.getString(i).equals("")) {
							String columnContent = cursor.getString(i);
							if (columnContent.length() > cutStringAfterNChars)
								columnContent = columnContent.substring(0, cutStringAfterNChars) + "...";
							subject.put(columnNames[i], columnContent);
						}
					}
				}
				dayToCompare = cursor.getString(6);
				subjects.add(subject);
			}
			cursor.close();
		}
		return subjects;
	}

	public void getSubjectsForWeek() {
		subjectsForWeek = new HashMap<Integer, ArrayList<Map<String, String>>>();
		for (int i = 1; i <= 6; i++) {
			Cursor cursor = db.rawQuery("SELECT v.begin, v.place AS vPlace, v.end, v.title, v.type, v.place, l.name, v.id " + "FROM "
					+ Semester.tableName + " AS s " + "LEFT JOIN " + Subject.tableName + " AS v ON (v.semesterId = s.id) " + "LEFT JOIN "
					+ Lecturer.tableName + " AS l ON (l.id = v.lecturerId) " + "WHERE date(s.begin) <= date('now') "
					+ "AND date(s.end) >= date('now') " + "AND v.day = " + i + " " + "ORDER BY v.begin", null);
			ArrayList<Map<String, String>> subjectsForDay = new ArrayList<Map<String, String>>();
			String[] columnNames = cursor.getColumnNames();
			while (cursor.moveToNext()) {
				Map<String, String> subject = new HashMap<String, String>();
				for (int a = 0; a < columnNames.length; a++) {
					subject.put(columnNames[a], cursor.getString(a));
				}
				subjectsForDay.add(subject);
			}
			cursor.close();
			subjectsForWeek.put(i, subjectsForDay);
		}
	}

	public ArrayList<Map<String, String>> getSubjectsForDayOfWeek(int dayOfWeek) {
		ArrayList<Map<String, String>> subjects = new ArrayList<Map<String, String>>();
		Cursor cursor = db.rawQuery("SELECT v.*, v.id AS veranstaltungId, v.place AS vPlace, l.name " + "FROM " + Semester.tableName
				+ " AS s " + "LEFT JOIN " + Subject.tableName + " AS v ON (v.semesterId = s.id) " + "LEFT JOIN " + Lecturer.tableName
				+ " AS l ON (l.id = v.lecturerId) " + "WHERE date(s.begin) <= date('now') " + "AND date(s.end) >= date('now') "
				+ "AND v.day = " + dayOfWeek + " " + "ORDER BY v.begin", null);
		String[] columnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> subject = new HashMap<String, String>();
			for (int i = 0; i < columnNames.length; i++) {
				if (cursor.getString(i) != null) {
					String columnContent = cursor.getString(i);
					if (!columnContent.equals("")) {
						if (columnContent.length() > cutStringAfterNChars)
							columnContent = columnContent.substring(0, cutStringAfterNChars) + "...";
						subject.put(columnNames[i], columnContent);
					}
				}
			}
			subjects.add(subject);
		}
		cursor.close();
		return subjects;
	}

	/************************************************* CRUD Operations *************************************************/
	public Map<String, String> get(int id) {
		Map<String, String> subject = new HashMap<String, String>();
		Cursor cursor = db.rawQuery("SELECT v.*, v.place AS vPlace, l.* " + "FROM " + Subject.tableName + " AS v " + "LEFT JOIN "
				+ Lecturer.tableName + " AS l ON (v.lecturerId = l.id) " + "WHERE v.id = " + String.valueOf(id), null);
		if (cursor.moveToNext()) {
			String[] columnNames = cursor.getColumnNames();
			for (int i = 0; i < columnNames.length; i++) {
				if (cursor.getString(i) != null) {
					if (!cursor.getString(i).equals("")) {
						subject.put(columnNames[i], cursor.getString(i));
					}
				}
			}
		}
		cursor.close();
		return subject;
	}
}
