package com.studiplaner.SubActivities.Notes;

import java.util.HashMap;
import java.util.Map;

import com.studiplaner.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SubActivityNotesModAdd extends SubActivityNotesMod {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			String tag = b.getString("tag");
			if (tag != null)
				setViewWithInformation(tag);
		}
		Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (checkElements()) {
					addNote();
					finish();
				}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void addNote() {
		Map<String, String> note = getView();
		notizen.save((HashMap<String, String>) note);
	}
}
