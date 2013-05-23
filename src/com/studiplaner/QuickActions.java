package com.studiplaner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.studiplaner.SubActivities.Lecturer.ActivityLecturer;
import com.studiplaner.SubActivities.Notes.ActivityNotes;
import com.studiplaner.SubActivities.Subject.ActivitySubjects;
import com.studiplaner.db.model.Lecturer;
import com.studiplaner.db.model.Semester;
import com.studiplaner.db.model.Subject;
import com.studiplaner.db.model.Task;


public class QuickActions {
	Subject veranstaltung;
	Lecturer lehrender;

	public static void initQuickActions(final ArrayList<Map<String, Object>> elements, final Activity activity, ImageView quickActionBt,
			final ArrayList<Map<String, String>> spinnerElements, final Intent spinnerSelectedIntent) {
		/*
		quickActionBt.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (elements.size() > 0) {
					final QuickAction qa = new QuickAction(arg0);
					for (int i = 0; i < elements.size(); i++) {
						final Map<String, Object> el = elements.get(i);
						final ActionItem ai = new ActionItem();
						ai.setTitle(String.valueOf(el.get("title")));
						ai.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								qa.dismiss();
								// footer
								if (el.get("title").equals(activity.getString(R.string.select))) {
									final CharSequence[] items = new CharSequence[spinnerElements.size()];
									for (int i = 0; i < spinnerElements.size(); i++) {
										Map<String, String> spinnerElement = spinnerElements.get(i);
										items[i] = spinnerElement.get("title");
									}
									AlertDialog.Builder builder = new AlertDialog.Builder(activity);
									builder.setTitle(activity.getString(R.string.select));
									builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int item) {
											Map<String, String> selectedSemester = spinnerElements.get(item);
											ActivitySubjects as = (ActivitySubjects) activity;
											as.semesterId = Integer.parseInt(selectedSemester.get("id"));
											as.showOtherSemester = true;
											as.initSemester();
											dialog.cancel();
										}
									});
									AlertDialog alert = builder.create();
									alert.show();
								} else if (el.get("title").equals(activity.getString(R.string.delete))) {
									if (el.get("veranstaltungId") != null) {
										Subject veranstaltung = new Subject();
										veranstaltung.delete(Integer.parseInt((String) el.get("veranstaltungId")));
										ActivitySubjects as = (ActivitySubjects) activity;
										as.initSemester();
									}
									if (el.get("lecturerId") != null) {
										Lecturer lehrender = new Lecturer();
										lehrender.delete(Integer.parseInt((String) el.get("lecturerId")));
										ActivityLecturer as = (ActivityLecturer) activity;
										as.buildDozentList();
									}
									if (el.get("assignmentId") != null) {
										Task notizen = new Task();
										notizen.deleteAssignment(Integer.parseInt((String) el.get("assignmentId")));
										ActivityNotes as = (ActivityNotes) activity;
										as.buildNotesList();
									}

									if (el.get("semesterId") != null) {
										Semester semester = new Semester();
										semester.delete(Integer.parseInt((String) el.get("semesterId")));
										ActivitySubjects as = (ActivitySubjects) activity;
										as.semesterId = null;
										as.selectedSemester = new HashMap<String, String>();
										as.showOtherSemester = false;
										as.initSemester();
									}
								} else if (el.get("title").equals(activity.getString(R.string.add))) {
									if (el.get("semester") != null) {
										Intent i = (Intent) el.get("intent");
										activity.startActivityForResult(i, 0);
									}
								} else {
									activity.startActivity((Intent) el.get("intent"));
								}
							}
						});

						qa.addActionItem(ai);
					}
					qa.setAnimStyle(QuickAction.ANIM_AUTO);
					qa.show();
				}
			}
		});*/
	}
}
