package com.studiplaner.SubActivities.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.studiplaner.SubActivities.Semester.SubActivitySemesterModAdd;
import com.studiplaner.SubActivities.Semester.SubActivitySemesterModEdit;
import com.studiplaner.db.ActivityDB;
import com.studiplaner.db.model.Lecturer;
import com.studiplaner.db.model.Semester;
import com.studiplaner.db.model.Subject;
import com.studiplaner.db.model.Task;


public class ActivitySubjects extends ActivityDB {
	Semester semester;
	Lecturer lehrende;
	Task notizen;
	Subject veranstaltung;
	ActivitySubjects thisActivity;
	String[] days;
	public Map<String, String> selectedSemester = null;
	public Integer semesterId = null;
	public boolean showOtherSemester = false;
	Integer subjectsCountForCurrentSemester = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		days = new String[] {	getString(R.string.mo),
								getString(R.string.tu),
								getString(R.string.wed),
								getString(R.string.thu),
								getString(R.string.fr),
								getString(R.string.sa),
								getString(R.string.su) };
		semester = new Semester();
		lehrende = new Lecturer();
		veranstaltung = new Subject();
		notizen = new Task();
		thisActivity = this;

		setContentView(R.layout.container_with_footer);
	}

	@Override
	public void onStart() {
		super.onStart();
		initSemester();
	}

	public void initSemester() {
		if (!showOtherSemester) {
			selectedSemester = semester.getCurrentSemester();
			if (selectedSemester.size() > 0) {
				semesterId = Integer.parseInt(selectedSemester.get("id"));
			}
		} else {
			selectedSemester = semester.get(semesterId);
		}
		subjectsCountForCurrentSemester = semester.countSemesterSubjects(semesterId);

		initActionBar();
		buildSubjectList();
		initFooter();
	}

	public void buildSubjectList() {
		final ArrayList<Map<String, String>> selectedSemesterSubjects = veranstaltung.getSubjects(semesterId);
		ListView listSubjects = (ListView) findViewById(R.id.container_list);
		ListAdapter lAdapter = new ArrayAdapter<Map<String, String>>(this, R.layout.listrow, selectedSemesterSubjects) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Map<String, String> subject = selectedSemesterSubjects.get(position);
				if (subject.size() == 1) {
					convertView = (LinearLayout) getLayoutInflater().inflate(R.layout.listitem_seperator, null);
					TextView sepTitle = (TextView) convertView.findViewById(R.id.sepTitle);
					if (subject.get("day").equals("0"))
						sepTitle.setText(days[6]);
					else
						sepTitle.setText(days[Integer.valueOf(subject.get("day")) - 1]);
				} else {
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
					Intent iAssignment = new Intent(thisActivity.getApplicationContext(), SubActivityNotesModAdd.class);
					Bundle bAssignment = new Bundle();
					bAssignment.putString("tag", subject.get("title"));
					iAssignment.putExtras(bAssignment);
					quickElement3.put("title", getString(R.string.note));
					quickElement3.put("intent", iAssignment);
					quickElements.add(quickElement3);

					QuickActions.initQuickActions(quickElements, thisActivity, (ImageView) convertView.findViewById(R.id.quick_action),
							null, null);
				}

				return convertView;
			}
		};

		listSubjects.setAdapter(lAdapter);
	}

	public void initActionBar() {
		Intent homeI = new Intent(this, ActivityHome.class);
		Intent createI = new Intent(this, SubActivitySubjectModAdd.class);
		LinearLayout ll = (LinearLayout) findViewById(R.id.actionbar_actions);
		if (ll != null) {
			ll.removeAllViewsInLayout();
		}
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getString(R.string.subjects));
		actionBar.setHomeAction(new IntentAction(this, homeI, R.drawable.home_actionbar));
		if (semesterId != null) {
			Bundle b = new Bundle();
			b.putInt("semesterId", semesterId);
			createI.putExtras(b);
			actionBar.addAction(new IntentAction(this, createI, R.drawable.new_actionbar));
		}
	}

	public void initFooter() {
		ArrayList<Map<String, String>> semesters = semester.getAll();
		Intent spinnerItemSelectedItent = new Intent(thisActivity.getApplicationContext(), ActivitySubjects.class);
		TextView footerTitle = (TextView) findViewById(R.id.footer_title);
		TextView footerCount = (TextView) findViewById(R.id.footer_count);
		TextView footerCountLabel = (TextView) findViewById(R.id.footer_count_label);
		ArrayList<Map<String, Object>> quickElements = new ArrayList<Map<String, Object>>();
		if (selectedSemester.size() > 0) {
			footerTitle.setText(selectedSemester.get("title"));
			footerCount.setVisibility(View.VISIBLE);
			footerCountLabel.setVisibility(View.VISIBLE);
			footerCount.setText(String.valueOf(subjectsCountForCurrentSemester));
			footerCountLabel.setText(getString(R.string.subjects));
		} else {
			footerTitle.setText(getString(R.string.no_semester));
			footerCount.setVisibility(View.INVISIBLE);
			footerCountLabel.setVisibility(View.INVISIBLE);
		}

		if (semesters.size() > 0) {
			Map<String, Object> quickElement0 = new HashMap<String, Object>();
			Intent selectIntent = new Intent(thisActivity.getApplicationContext(), ActivitySubjects.class);
			quickElement0.put("title", getString(R.string.select));
			quickElement0.put("intent", selectIntent);
			quickElements.add(quickElement0);
		}
		if (semesterId != null) {
			Map<String, Object> quickElement1 = new HashMap<String, Object>();
			Intent editIntent = new Intent(thisActivity.getApplicationContext(), SubActivitySemesterModEdit.class);
			Bundle editBundle = new Bundle();
			editBundle.putInt("semesterId", semesterId);
			editIntent.putExtras(editBundle);
			quickElement1.put("title", getString(R.string.edit));
			quickElement1.put("intent", editIntent);
			quickElements.add(quickElement1);

			Map<String, Object> quickElement3 = new HashMap<String, Object>();
			quickElement3.put("semesterId", String.valueOf(semesterId));
			quickElement3.put("title", getString(R.string.delete));
			quickElements.add(quickElement3);
		}
		Map<String, Object> quickElement3 = new HashMap<String, Object>();
		Intent addIntent = new Intent(thisActivity.getApplicationContext(), SubActivitySemesterModAdd.class);
		quickElement3.put("title", getString(R.string.add_semester));
		quickElement3.put("semester", "");
		quickElement3.put("intent", addIntent);
		quickElements.add(quickElement3);
		QuickActions.initQuickActions(quickElements, thisActivity, (ImageView) findViewById(R.id.quick_action_footer), semesters,
				spinnerItemSelectedItent);
	}

	public void deleteVeranstaltung(final int veranstId, final int semesterId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.confirmSubjectDelete)).setCancelable(false)
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						veranstaltung.delete(veranstId);
						buildSubjectList();
					}
				}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == 0) {
			Bundle extras = data.getExtras();
			semesterId = extras.getInt("semesterId");
			showOtherSemester = true;
		}
	}
}