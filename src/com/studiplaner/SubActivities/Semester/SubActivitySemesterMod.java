package com.studiplaner.SubActivities.Semester;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.studiplaner.Helper;
import com.studiplaner.R;
import com.studiplaner.SubActivities.Subject.ActivitySubjects;
import com.studiplaner.db.model.Semester;


public class SubActivitySemesterMod extends Activity {
	Semester semester;
	ActivitySubjects activitySubjects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		semester = new Semester();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public HashMap<String, String> getSemesterGuiInformation() {
		EditText name = (EditText) findViewById(R.id.semester_name);
		DatePicker begin = (DatePicker) findViewById(R.id.semester_begin);
		String beginTxt = begin.getYear() + "-" + Helper.getNullAsPrefix(String.valueOf(begin.getMonth() + 1)) + "-"
				+ Helper.getNullAsPrefix(String.valueOf(begin.getDayOfMonth()));
		DatePicker end = (DatePicker) findViewById(R.id.semester_end);
		String endTxt = end.getYear() + "-" + Helper.getNullAsPrefix(String.valueOf(end.getMonth() + 1)) + "-"
				+ Helper.getNullAsPrefix(String.valueOf(end.getDayOfMonth()));

		HashMap<String, String> semesterBind = new HashMap<String, String>();
		semesterBind.put("title", name.getEditableText().toString());
		semesterBind.put("begin", beginTxt);
		semesterBind.put("end", endTxt);

		return semesterBind;
	}

	public void setSemesterGuiInformation(int semesterId) {
		EditText name = (EditText) findViewById(R.id.semester_name);
		DatePicker begin = (DatePicker) findViewById(R.id.semester_begin);
		DatePicker end = (DatePicker) findViewById(R.id.semester_end);

		Map<String, String> semesterBind = semester.get(semesterId);
		name.setText(semesterBind.get("title"));
		String[] dateBegin = new String(semesterBind.get("begin")).split("-");
		String[] dateEnd = new String(semesterBind.get("end")).split("-");
		begin.init(Integer.parseInt(dateBegin[0]), Integer.parseInt(dateBegin[1]) - 1, Integer.parseInt(dateBegin[2]), null);
		end.init(Integer.parseInt(dateEnd[0]), Integer.parseInt(dateEnd[1]) - 1, Integer.parseInt(dateEnd[2]), null);
	}

	public boolean checkFields() {
		TextView subjectDescr = (TextView) findViewById(R.id.semester_name);
		if (subjectDescr.getEditableText().toString().equals("")) {
			Helper.printMessage(this, getString(R.string.please_fill) + getString(R.string.semester), getString(R.string.warning), false);
			return false;
		}

		// check date
		DatePicker begin = (DatePicker) findViewById(R.id.semester_begin);
		DatePicker end = (DatePicker) findViewById(R.id.semester_end);
		Calendar beginCal = Calendar.getInstance();
		beginCal.set(begin.getYear(), begin.getMonth(), begin.getDayOfMonth());
		Calendar endCal = Calendar.getInstance();
		endCal.set(end.getYear(), end.getMonth(), end.getDayOfMonth());

		if (endCal.before(beginCal)) {
			Helper.printMessage(this, getString(R.string.check_date), getString(R.string.warning), false);
			return false;
		}
		return true;
	}

	public void finish(View arg0) {
		finish();
	}
}