package com.studiplaner.SubActivities.Subject;

import java.util.HashMap;
import java.util.Map;

import com.studiplaner.R;
import com.studiplaner.db.model.CRUD;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SubActivitySubjectModEdit extends SubActivitySubjectMod {
	public int subjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = this.getIntent().getExtras();
		subjectId = b.getInt("veranstaltungId");
	}

	@Override
	public void onStart() {
		super.onStart();
		editVeranstaltung(subjectId);
	}

	public void editVeranstaltung(final int veranstaltungId) {
		setContentView(R.layout.subjects_add);
		setDaySpinner();
		setDozentSpinner();
		setSubjectTypeSpinner();

		setVeranstGuiInformation(veranstaltungId);
		Button editButton = (Button) findViewById(R.id.save);
		editButton.setText(getString(R.string.update));
		editButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (checkFields()) {
					Map<String, String> veranst = getVeranstGuiInformation();
					veranstaltung.update((HashMap<String, String>) veranst, CRUD.columnId);
					finish();
				}
			}
		});
	}

	public void finish(View arg0) {
		finish();
	}
}
