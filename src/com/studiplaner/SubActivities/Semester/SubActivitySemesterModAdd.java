package com.studiplaner.SubActivities.Semester;

import java.util.HashMap;
import java.util.Map;

import com.studiplaner.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SubActivitySemesterModAdd extends SubActivitySemesterMod {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		addSemester(null);
	}

	public void addSemester(View arg0) {
		setContentView(R.layout.semesters_add);
		Button button = (Button) findViewById(R.id.save);
		button.setText(getString(R.string.save));
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (checkFields()) {
					HashMap<String, String> semesterMap = getSemesterGuiInformation();
					int semesterId = (int) semester.save(semesterMap);
					Intent resultIntent = new Intent();
					Bundle b = new Bundle();
					b.putInt("semesterId", semesterId);
					resultIntent.putExtras(b);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}
			}
		});
	}
}