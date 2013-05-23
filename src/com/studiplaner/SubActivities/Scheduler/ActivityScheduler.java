package com.studiplaner.SubActivities.Scheduler;

import java.util.Calendar;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.studiplaner.ActivityHome;
import com.studiplaner.R;
import com.studiplaner.db.TabActivityDB;
import com.studiplaner.db.model.Subject;


public class ActivityScheduler extends TabActivityDB {
	public static String[] days;
	TabActivity thisActivity;
	Subject veranstaltung = null;
	Integer semesterId = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		veranstaltung = new Subject();
		thisActivity = this;
		days = new String[] {	getString(R.string.mo),
								getString(R.string.tu),
								getString(R.string.wed),
								getString(R.string.thu),
								getString(R.string.fr),
								getString(R.string.sa),
								getString(R.string.su) };
		Bundle b = this.getIntent().getExtras();
		semesterId = b.getInt("semesterId");

		setContentView(R.layout.container_with_tabhost_without_footer);
		initActionBar();
		buildStundenplan();
	}

	public void buildStundenplan() {
		TabHost tabHost = getTabHost();

		for (int i = 0; i < days.length; i++) {
			TabSpec spec;
			int subjectsAmoundForThatDay = 0;
			int queryDay = i;
			Bundle b = new Bundle();
			if (i == 6)
				queryDay = 0;

			else
				queryDay = i + 1;

			b.putInt("day", queryDay);
			if (semesterId != null)
				subjectsAmoundForThatDay = veranstaltung.countSubjectsForDay(semesterId, queryDay);

			Intent intent = new Intent().setClass(this, SubActivityDayScheduler.class);
			intent.putExtras(b);
			spec = tabHost.newTabSpec(days[i]).setIndicator(days[i]).setContent(intent);

			View view = prepareTabView(tabHost.getContext(), days[i], subjectsAmoundForThatDay);
			spec.setIndicator(view);
			tabHost.addTab(spec);
		}

		int todaysDate;
		if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1)
			todaysDate = 6;
		else
			todaysDate = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;

		tabHost.setCurrentTab(todaysDate);
	}

	private static View prepareTabView(Context context, String title, int subjectsAmoundForThatDay) {
		View view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.tab, null);
		TextView dayName = (TextView) view.findViewById(R.id.dayName);
		dayName.setText(title);
		TextView count = (TextView) view.findViewById(R.id.countDay);
		if (subjectsAmoundForThatDay > 0)
			count.setText(String.valueOf(subjectsAmoundForThatDay));
		else
			count.setText("-");
		return view;
	}

	public void initActionBar() {
		Intent homeI = new Intent(this, ActivityHome.class);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getString(R.string.schedule));
		actionBar.setHomeAction(new IntentAction(this, homeI, R.drawable.home_actionbar));
	}
}