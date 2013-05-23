package com.studiplaner.SubActivities.Notes;

import java.util.ArrayList;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.studiplaner.R;
import com.studiplaner.db.ListActivityDB;
import com.studiplaner.db.model.Subject;
import com.studiplaner.db.model.Task;


public class SubActivityNotesTyp extends ListActivityDB {
	Subject veranstaltung;
	Task notizen;
	int noteType;
	ListActivity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		veranstaltung = new Subject();
		notizen = new Task();
		thisActivity = this;
	}

	@Override
	public void onStart() {
		super.onStart();
		initActionBar();
		buildNotesList();
	}

	public void buildNotesList() {
		final ArrayList<Map<String, String>> notes = notizen.getAll();
		ListAdapter lAdapter = new ArrayAdapter<Map<String, String>>(this, R.layout.listrow_for_assignments, notes) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Map<String, String> note = notes.get(position);
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(R.layout.listrow_for_assignments, null);
				}
				TextView first = (TextView) findViewById(R.id.listrow_with_two_elements_first);
				first.setText(note.get("title"));

				TextView second = (TextView) findViewById(R.id.listrow_with_two_elements_second);
				second.setText(note.get("inhalt"));

				// Array einlesen

				return convertView;
			}
		};
		setListAdapter(lAdapter);
	}

	public void initActionBar() {
		// Intent actionBarAddIntent = new
		// Intent(thisActivity.getApplicationContext(),
		// SubActivitySubjectModAdd.class);
		// ActionBar.initActionBar(R.layout.container_with_footer, thisActivity,
		// getString(R.string.semester), getString(R.string.semester_added),
		// actionBarAddIntent);
	}
}