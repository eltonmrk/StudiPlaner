package com.studiplaner.SubActivities.Semester;

import com.studiplaner.db.model.Semester;

import android.app.Activity;
import android.os.Bundle;

public class ActivitySemester extends Activity {
	Semester semester;
	Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisActivity = this;
		semester = new Semester();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}