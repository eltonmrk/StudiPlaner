package com.studiplaner.SubActivities.Notes;

import java.util.HashMap;
import java.util.Map;

import com.studiplaner.R;
import com.studiplaner.db.model.CRUD;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SubActivityNotesModEdit extends SubActivityNotesMod {
	int noteId;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		Bundle b = this.getIntent().getExtras();
		noteId = b.getInt("noteId");
	}

	@Override
	public void onStart() {
		super.onStart();
		editNote(noteId);
	}

	public void editNote(final int noteId) {
		setView(noteId);
		Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (checkElements()) {
					Map<String, String> note = getView();
					notizen.update((HashMap<String, String>) note, note.get(CRUD.columnId));
					finish();
				}
			}
		});
	}
}
