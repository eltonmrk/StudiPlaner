package com.studiplaner.SubActivities.Notes;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.studiplaner.Helper;
import com.studiplaner.R;
import com.studiplaner.db.model.Task;


public class SubActivityNotesMod extends Activity {
	Task notizen;

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		notizen = new Task();
		setContentView(R.layout.assignment_add);
	}

	public boolean checkElements() {
		TextView title = (TextView) findViewById(R.id.assignment_title);
		if (title.getText().toString().compareTo("") == 0) {
			Helper.printMessage(this, getString(R.string.please_fill) + getString(R.string.title), getString(R.string.warning), false);
			return false;
		}
		return true;
	}

	public void setView(int notesId) {
		Map<String, String> note = notizen.get(notesId);
		TextView title = (TextView) findViewById(R.id.assignment_title);
		TextView descr = (TextView) findViewById(R.id.assignment_descr);
		TextView tags = (TextView) findViewById(R.id.assignment_tag);
		title.setText(note.get("title"));
		descr.setText(note.get("content"));
		tags.setText(note.get("tags"));
	}

	public void setViewWithInformation(String tag) {
		TextView tags = (TextView) findViewById(R.id.assignment_tag);
		tags.setText(tag + ",");
	}

	public Map<String, String> getView() {
		Map<String, String> note = new HashMap<String, String>();
		TextView title = (TextView) findViewById(R.id.assignment_title);
		TextView descr = (TextView) findViewById(R.id.assignment_descr);
		TextView tags = (TextView) findViewById(R.id.assignment_tag);
		note.put("title", title.getText().toString());
		note.put("content", descr.getText().toString());
		note.put("tags", tags.getText().toString());
		return note;
	}

	public void finish(View arg0) {
		finish();
	}
}
