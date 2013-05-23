package com.studiplaner.SubActivities.Semester;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.studiplaner.R;
import com.studiplaner.db.model.CRUD;

public class SubActivitySemesterModEdit extends SubActivitySemesterMod {
	private int semesterId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = this.getIntent().getExtras();
		semesterId = b.getInt("semesterId");
	}

	@Override
	public void onStart() {
		super.onStart();
		editSemester(semesterId);
	}

	public void editSemester(final int semesterId) {
		setContentView(R.layout.semesters_add);
		Button editButton = (Button) findViewById(R.id.save);
		editButton.setText(getString(R.string.update));
		setSemesterGuiInformation(semesterId);
		editButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (checkFields()) {
					HashMap<String, String> semesterMap = getSemesterGuiInformation();
					semester.update(semesterMap, semesterMap.get(CRUD.columnId));
					finish();
				}
			}
		});
	}
}