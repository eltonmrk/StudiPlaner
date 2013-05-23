package com.studiplaner.SubActivities.Notes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.studiplaner.ActivityHome;
import com.studiplaner.QuickActions;
import com.studiplaner.R;
import com.studiplaner.db.ActivityDB;
import com.studiplaner.db.model.Task;


public class ActivityNotes extends ActivityDB {
	Task notizen;
	int noteType;
	Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		notizen = new Task();
		thisActivity = this;
	}

	@Override
	public void onStart() {
		super.onStart();
		setContentView(R.layout.container_without_footer);
		initActionBar();
		buildNotesList();
	}

	public void buildNotesList() {
		ListView listView = (ListView) findViewById(R.id.container_list);
		final ArrayList<Map<String, String>> notes = notizen.getAll();
		ListAdapter lAdapter = new ArrayAdapter<Map<String, String>>(this, R.layout.listrow_for_assignments, notes) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Map<String, String> note = notes.get(position);
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(R.layout.listrow_for_assignments, null);
				}
				TextView first = (TextView) convertView.findViewById(R.id.listitem_txt);
				first.setText(note.get("title"));

				TextView second = (TextView) convertView.findViewById(R.id.listrow_with_two_elements_first);
				second.setText(note.get("content"));

				LinearLayout third = (LinearLayout) convertView.findViewById(R.id.listrow_with_two_elements_second);
				third.removeAllViewsInLayout();
				if (note.get("tags") != null) {
					String[] tags = note.get("tags").split(",");
					for (int i = 0; i < tags.length; i++) {
						String tag = tags[i];
						TextView tView = new TextView(thisActivity);
						tView.setText(tag);
						tView.setTextSize(12.0f);
						tView.setTextColor(R.color.form_textcolor);
						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						layoutParams.setMargins(0, 0, 10, 0);
						tView.setLayoutParams(layoutParams);
						tView.setBackgroundResource(R.layout.rounded_corner_square);
						third.addView(tView);
					}
				}

				ArrayList<Map<String, Object>> quickElements = new ArrayList<Map<String, Object>>();
				Map<String, Object> quickElement1 = new HashMap<String, Object>();
				Intent iEdit = new Intent(thisActivity.getApplicationContext(), SubActivityNotesModEdit.class);
				Bundle bEdit = new Bundle();
				bEdit.putInt("noteId", Integer.parseInt(note.get("id")));
				iEdit.putExtras(bEdit);
				quickElement1.put("title", getString(R.string.edit));
				quickElement1.put("intent", iEdit);
				quickElements.add(quickElement1);

				Map<String, Object> quickElement2 = new HashMap<String, Object>();
				quickElement2.put("assignmentId", note.get("id"));
				quickElement2.put("title", getString(R.string.delete));
				quickElements.add(quickElement2);

				QuickActions.initQuickActions(quickElements, thisActivity, (ImageView) convertView.findViewById(R.id.quick_action), null,
						null);

				return convertView;
			}
		};
		listView.setAdapter(lAdapter);
	}

	public void initActionBar() {
		Intent homeI = new Intent(this, ActivityHome.class);
		Intent createI = new Intent(this, SubActivityNotesModAdd.class);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getString(R.string.note));
		actionBar.setHomeAction(new IntentAction(this, homeI, R.drawable.home_actionbar));
		actionBar.addAction(new IntentAction(this, createI, R.drawable.new_actionbar));
	}
}