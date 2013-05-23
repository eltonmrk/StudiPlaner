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

public class Subject extends CRUD {
	private final int cutStringAfterNChars = 30;
	private Map<Integer, ArrayList<Map<String, String>>> subjectsForWeek;

	public static final String tableName = "subject";

	public static String columnTitle = "title";
	public static String columnBegin = "begin";
	public static String columnEnd = "end";
	public static String columnDay = "day";
	public static String columnPlace = "place";
	public static String columnSubjectTypeId = "subject_type_id";
	public static String columnLecturerId = "lecturer_id";
	public static String columnSemesterId = "semester_id";

	public Subject() {
		super(tableName);
	}

	public int countSubjectsForDay(int subjectId, int day) {
		Cursor cursor = db.rawQuery("SELECT COUNT(*) " + "FROM " + Semester.tableName + " AS se " + "LEFT JOIN " + Subject.tableName
				+ " AS su ON (su.id = se.semesterId) " + "WHERE su.day = " + day + " AND se.id = " + subjectId, null);
		cursor.moveToNext();
		int count = Integer.parseInt(cursor.getString(0));
		cursor.close();
		return count;
	}
	
	public ArrayList<Map<String, String>> getSubjectsForListView(int semesterId) {
		ArrayList<Map<String, String>> subjects = new ArrayList<Map<String, String>>();
		Cursor cursor = db.rawQuery("SELECT v.* " + "FROM " + Semester.tableName + " AS s " + "LEFT JOIN " + Subject.tableName
				+ " AS v ON (s.id = v.semesterId) " + "WHERE v.semesterId = " + semesterId + " " + "ORDER BY v.day ", null);
		String dayToCompare = "";
		String[] colomnNames = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> subjectRow = new HashMap<String, String>();
			if (cursor.getString(0) != null) {
				// separate by day
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
	
	public ArrayList<Map<String, String>> getSubjects(int semesterId) {
		ArrayList<Map<String, String>> subjects = new ArrayList<Map<String, String>>();
		Cursor cursor = db.rawQuery("SELECT v.*, v.place AS vPlace, l.*, v.id AS veranstaltungId " + "FROM " + Subject.tableName
				+ " AS v " + "LEFT JOIN " + Lecturer.tableName + " AS l ON(v.lecturerId = l.id) " + "WHERE v.semesterId = " + semesterId + " "
				+ "ORDER BY v.begin", null);
		String[] columnNames = cursor.getColumnNames();
		String dayToCompare = "";
		while (cursor.moveToNext()) {
			Map<String, String> subject = new HashMap<String, String>();
			// separate by day
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
	
	public HashMap<String, String> get(int id) {
		HashMap<String, String> subject = new HashMap<String, String>();
		Cursor cursor = db.rawQuery("SELECT v.*, v." + Subject.columnPlace + " AS vPlace, l.* " + "FROM " + Subject.tableName + " AS v "
				+ "LEFT JOIN " + Lecturer.tableName + " AS l ON (v." + Subject.columnLecturerId + " = l.id) " + "WHERE v.id = " + String.valueOf(id), null);
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
