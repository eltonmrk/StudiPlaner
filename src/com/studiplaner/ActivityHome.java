package com.studiplaner;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.markupartist.android.widget.ActionBar;
import com.studiplaner.SubActivities.Lecturer.ActivityLecturer;
import com.studiplaner.SubActivities.Notes.ActivityNotes;
import com.studiplaner.SubActivities.Scheduler.ActivityScheduler;
import com.studiplaner.SubActivities.Subject.ActivitySubjects;
import com.studiplaner.db.ActivityDB;
import com.studiplaner.db.model.Semester;
import com.studiplaner.db.model.Subject;
import com.studiplaner.db.model.Task;

public class ActivityHome extends ActivityDB {
	Subject veranstaltung;
	Task notizen;
	Helper helper;
	Semester semester;
	public static String[] days;
	Map<String, String> currentSemester;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		days = new String[] {	getString(R.string.su_long),
								getString(R.string.mo_long),
								getString(R.string.tu_long),
								getString(R.string.wed_long),
								getString(R.string.thu_long),
								getString(R.string.fr_long),
								getString(R.string.sa_long) };
		veranstaltung = new Subject();
		notizen = new Task();
		semester = new Semester();
		helper = new Helper();
	}

	@Override
	public void onStart() {
		super.onStart();
		setContentView(R.layout.dashboard);
		//initActionBar();
	}

	public void initActionBar() {
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getString(R.string.welcome));
	}

	public void note(View arg0) {
		Intent i = new Intent(this.getApplicationContext(), ActivityNotes.class);
		startActivity(i);
	}

	public void teacher(View arg0) {
		Intent i = new Intent(this.getApplicationContext(), ActivityLecturer.class);
		startActivity(i);
	}

	public void scheduler(View arg0) {
		Intent i = new Intent(this.getApplicationContext(), ActivityScheduler.class);
		if (currentSemester.size() > 0) {
			Bundle b = new Bundle();
			b.putInt("semesterId", Integer.parseInt(currentSemester.get("id")));
			i.putExtras(b);
			startActivity(i);
		} else {
			Helper.printMessage(this, getString(R.string.no_current_semester), getString(R.string.schedule), false);
		}
	}

	public void subjects(View arg0) {
		Intent i = new Intent(this.getApplicationContext(), ActivitySubjects.class);
		startActivity(i);
	}
}