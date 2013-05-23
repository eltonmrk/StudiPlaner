package com.studiplaner.SubActivities.Dozent;

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
import com.studiplaner.SubActivities.Notes.SubActivityNotesModAdd;

import db.ActivityDB;
import db.DataBaseHelper;
import db.Lecturer;
import db.TaskCategory;

public class ActivityDozent extends ActivityDB {
	Lecturer lehrender;
	TaskCategory notizen;
	Activity thisActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lehrender = new Lecturer(this);
		notizen = new TaskCategory(this);
		thisActivity = this;
	}

	@Override
	public void onStart() {
		super.onStart();
		setContentView(R.layout.container_without_footer);
		initActionBar();
		buildDozentList();
	}

	public void buildDozentList() {
		final ArrayList<Map<String, String>> lehrende = lehrender.getDozenten();
		ListView listView = (ListView) findViewById(R.id.container_list);
		ListAdapter lAdapter = new ArrayAdapter<Map<String, String>>(this, R.layout.listrow, lehrende) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Map<String, String> dozent = lehrende.get(position);
				if (dozent.size() == 1)
				{
					convertView = (LinearLayout) getLayoutInflater().inflate(R.layout.listitem_seperator, null);
					TextView sepTitle = (TextView) convertView.findViewById(R.id.sepTitle);
					sepTitle.setText(dozent.get("buchstabe"));
				}
				else
				{
					convertView = (LinearLayout) getLayoutInflater().inflate(R.layout.listrow, null);
					LinearLayout container = (LinearLayout) convertView.findViewById(R.id.container_for_rows);
					
					TextView listTitle = (TextView) convertView.findViewById(R.id.listitem_txt);
					listTitle.setText(dozent.get("name"));
					if (dozent.get("place") != null)
					{
						LinearLayout linearLayout = (LinearLayout) thisActivity.getLayoutInflater().inflate(R.layout.row_list_item, null);
						TextView nameTV = (TextView) linearLayout.findViewById(R.id.row_content);
						nameTV.setText(dozent.get("place"));
						
						TextView label = (TextView) linearLayout.findViewById(R.id.label);
						label.setText(getString(R.string.room));
						container.addView(linearLayout);
					}
					
					if (dozent.get("email") != null)
					{
						LinearLayout linearLayout = (LinearLayout) thisActivity.getLayoutInflater().inflate(R.layout.row_list_item, null);
						TextView nameTV = (TextView) linearLayout.findViewById(R.id.row_content);
						nameTV.setText(dozent.get("email"));
						
						TextView label = (TextView) linearLayout.findViewById(R.id.label);
						label.setText(getString(R.string.mail));
						container.addView(linearLayout);
					}
					
					ArrayList<Map<String, Object>> quickElements = new ArrayList<Map<String, Object>>();
					Map<String, Object> quickElement1 = new HashMap<String, Object>();
					Intent i = new Intent(thisActivity.getApplicationContext(), SubActivityDozentModEdit.class);
					Bundle b = new Bundle();
					b.putInt("lecturerId", Integer.parseInt(dozent.get("id")));
					i.putExtras(b);
					quickElement1.put("title", getString(R.string.edit));
					quickElement1.put("intent", i);
					quickElements.add(quickElement1);
					
					Map<String, Object> quickElement2 = new HashMap<String, Object>();
					quickElement2.put("title", getString(R.string.delete));
					quickElement2.put("lecturerId", dozent.get("id"));
					quickElements.add(quickElement2);
					
					Map<String, Object> quickElement3 = new HashMap<String, Object>();
					Intent iAssignment = new Intent(thisActivity.getApplicationContext(), SubActivityNotesModAdd.class);
					Bundle bAssignment = new Bundle();
					bAssignment.putString("tag", dozent.get("name"));
					iAssignment.putExtras(bAssignment);
					quickElement3.put("title", getString(R.string.note));
					quickElement3.put("intent", iAssignment);
					quickElements.add(quickElement3);
					
					QuickActions.initQuickActions(quickElements, thisActivity, (ImageView) convertView.findViewById(R.id.quick_action), null, null);
				}
				return convertView;
			}
		};
		listView.setAdapter(lAdapter);
	}
	
	public void initActionBar(){
		Intent homeI = new Intent(this, ActivityHome.class);
		Intent createI = new Intent(this, SubActivityDozentModAdd.class);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getString(R.string.lecturer));
		actionBar.setHomeAction(new IntentAction(this, homeI, R.drawable.home_actionbar));
		actionBar.addAction(new IntentAction(this, createI, R.drawable.new_actionbar));
	}
}