package com.studiplaner.SubActivities.Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.studiplaner.Helper;
import com.studiplaner.QuickActions;
import com.studiplaner.R;
import com.studiplaner.SubActivities.Notes.SubActivityNotesModAdd;
import com.studiplaner.SubActivities.Subject.SubActivitySubjectModEdit;
import com.studiplaner.db.ListActivityDB;
import com.studiplaner.db.model.Subject;
import com.studiplaner.db.model.Task;


public class SubActivityDayScheduler extends ListActivityDB {
	Subject veranstaltung;
	Task notizen;
	ListActivity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		veranstaltung = new Subject();
		notizen = new Task();
		thisActivity = this;
	}

	@Override
	protected void onStart() {
		super.onStart();
		buildSchedulerDay();
	}

	public void buildSchedulerDay() {
		setContentView(R.layout.scheduler_list);
		Bundle b = this.getIntent().getExtras();
		int day = b.getInt("day");
		final ArrayList<Map<String, String>> subjectsForDay = veranstaltung.getSubjectsForDayOfWeek(day);
		ListAdapter lAdapter = new ArrayAdapter<Map<String, String>>(this, R.layout.listrow, subjectsForDay) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Map<String, String> subject = subjectsForDay.get(position);
				if (convertView == null) {
					convertView = (LinearLayout) getLayoutInflater().inflate(R.layout.listrow, null);
				}

				convertView = (LinearLayout) getLayoutInflater().inflate(R.layout.listrow, null);
				LinearLayout container = (LinearLayout) convertView.findViewById(R.id.container_for_rows);

				TextView listTitle = (TextView) convertView.findViewById(R.id.listitem_txt);
				listTitle.setText(subject.get("title"));
				if (subject.get("name") != null) {
					LinearLayout linearLayout = (LinearLayout) thisActivity.getLayoutInflater().inflate(R.layout.row_list_item, null);
					TextView nameTV = (TextView) linearLayout.findViewById(R.id.row_content);
					nameTV.setText(subject.get("name"));

					TextView label = (TextView) linearLayout.findViewById(R.id.label);
					label.setText(getString(R.string.lecturer));
					container.addView(linearLayout);
				}

				if (subject.get("vPlace") != null) {
					LinearLayout linearLayout = (LinearLayout) thisActivity.getLayoutInflater().inflate(R.layout.row_list_item, null);
					TextView nameTV = (TextView) linearLayout.findViewById(R.id.row_content);
					nameTV.setText(subject.get("vPlace"));

					TextView label = (TextView) linearLayout.findViewById(R.id.label);
					label.setText(getString(R.string.room));
					container.addView(linearLayout);
				}

				if (subject.get("begin") != null) {
					LinearLayout linearLayout = (LinearLayout) thisActivity.getLayoutInflater().inflate(R.layout.row_list_item, null);
					TextView nameTV = (TextView) linearLayout.findViewById(R.id.row_content);
					nameTV.setText(subject.get("begin"));

					TextView label = (TextView) linearLayout.findViewById(R.id.label);
					label.setText(getString(R.string.time));
					container.addView(linearLayout);
				}

				ArrayList<Map<String, Object>> quickElements = new ArrayList<Map<String, Object>>();
				Map<String, Object> quickElement1 = new HashMap<String, Object>();
				Intent i = new Intent(thisActivity.getApplicationContext(), SubActivitySubjectModEdit.class);
				Bundle b = new Bundle();
				b.putInt("veranstaltungId", Integer.parseInt(subject.get("veranstaltungId")));
				i.putExtras(b);
				quickElement1.put("title", getString(R.string.edit));
				quickElement1.put("intent", i);
				quickElements.add(quickElement1);

				Map<String, Object> quickElement2 = new HashMap<String, Object>();
				quickElement2.put("veranstaltungId", subject.get("veranstaltungId"));
				quickElement2.put("title", getString(R.string.delete));
				quickElements.add(quickElement2);

				Map<String, Object> quickElement3 = new HashMap<String, Object>();
				Intent iFB = new Intent(thisActivity.getApplicationContext(), SubActivityPostOnFacebook.class);
				Bundle bFB = new Bundle();
				bFB.putInt("veranstaltungId", Integer.parseInt(subject.get("veranstaltungId")));
				iFB.putExtras(bFB);
				quickElement3.put("title", getString(R.string.post));
				quickElement3.put("intent", iFB);
				quickElements.add(quickElement3);

				Map<String, Object> quickElement4 = new HashMap<String, Object>();
				Intent iAssignment = new Intent(thisActivity.getApplicationContext(), SubActivityNotesModAdd.class);
				Bundle bAssignment = new Bundle();
				bAssignment.putString("tag", subject.get("title"));
				iAssignment.putExtras(bAssignment);
				quickElement4.put("title", getString(R.string.note));
				quickElement4.put("intent", iAssignment);
				quickElements.add(quickElement4);

				QuickActions.initQuickActions(quickElements, thisActivity, (ImageView) convertView.findViewById(R.id.quick_action), null,
						null);

				// highlight row
				if (Helper.dateIsBetweenTwoDates(subject.get("begin"), subject.get("end"))) {
					convertView.setBackgroundResource(R.color.highlightRow);
				}
				return convertView;
			}
		};

		setListAdapter(lAdapter);
	}
}