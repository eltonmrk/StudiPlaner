package com.studiplaner.SubActivities.Subject;

import java.util.HashMap;
import java.util.Map;

import com.studiplaner.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

public class SubActivitySubjectModAdd extends SubActivitySubjectMod {
	private int semesterId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = this.getIntent().getExtras();
		semesterId = b.getInt("semesterId");
	}

	@Override
	public void onStart() {
		super.onStart();
		build(semesterId);
	}

	public void build(final int semesterId) {
		setContentView(R.layout.subjects_add);
		setDaySpinner();
		setDozentSpinner();
		setSubjectTypeSpinner();
		TimePicker begin = (TimePicker) findViewById(R.id.time_anfang);
		TimePicker end = (TimePicker) findViewById(R.id.time_ende);
		begin.setIs24HourView(true);
		end.setIs24HourView(true);

		Button saveButton = (Button) findViewById(R.id.save);
		saveButton.setText(getString(R.string.save));
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (checkFields()) {
					Map<String, String> veranst = getVeranstGuiInformation();
					veranst.put("semester_id", new Integer(semesterId).toString());
					veranstaltung.save((HashMap<String, String>) veranst);
					finish();
				}
			}
		});
	}

	public void finish(View arg0) {
		finish();
	}
}