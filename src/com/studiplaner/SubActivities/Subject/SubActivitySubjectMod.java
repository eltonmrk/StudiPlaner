package com.studiplaner.SubActivities.Subject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.studiplaner.Helper;
import com.studiplaner.R;
import com.studiplaner.db.model.Lecturer;
import com.studiplaner.db.model.Subject;


public class SubActivitySubjectMod extends Activity {
	Lecturer lehrende;
	Subject veranstaltung;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lehrende = new Lecturer();
		veranstaltung = new Subject();
	}

	public void setDaySpinner() {
		Spinner daySpinner = (Spinner) findViewById(R.id.day);
		ArrayAdapter<CharSequence> daySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.days,
				android.R.layout.simple_spinner_item);
		daySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		daySpinner.setAdapter(daySpinnerAdapter);
		daySpinner.setSelection(1);
	}

	public void setDozentSpinner() {
		Spinner daySpinner = (Spinner) findViewById(R.id.dozent_select);
		String[] dozenten = lehrende.getInstructorsForSpinner();
		dozenten[0] = getString(R.string.not_selected);
		ArrayAdapter<String[]> spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dozenten);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		daySpinner.setAdapter(spinnerAdapter);
	}

	public void setSubjectTypeSpinner() {
		Spinner subjectTypeSpinner = (Spinner) findViewById(R.id.subject_type);
		ArrayAdapter<CharSequence> daySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.event_type,
				android.R.layout.simple_spinner_item);
		daySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subjectTypeSpinner.setAdapter(daySpinnerAdapter);
	}

	public Map<String, String> getVeranstGuiInformation() {
		EditText subject = (EditText) findViewById(R.id.subject);
		EditText room = (EditText) findViewById(R.id.room);
		TimePicker timeBegin = (TimePicker) findViewById(R.id.time_anfang);
		TimePicker timeEnd = (TimePicker) findViewById(R.id.time_ende);
		Spinner day = (Spinner) findViewById(R.id.day);
		Spinner dozent = (Spinner) findViewById(R.id.dozent_select);
		Spinner subjectType = (Spinner) findViewById(R.id.subject_type);

		String startTxt = Helper.getNullAsPrefix(String.valueOf(timeBegin.getCurrentHour())) + ":"
				+ Helper.getNullAsPrefix(String.valueOf(timeBegin.getCurrentMinute()));

		String endTxt = Helper.getNullAsPrefix(String.valueOf(timeEnd.getCurrentHour())) + ":"
				+ Helper.getNullAsPrefix(String.valueOf(timeEnd.getCurrentMinute()));

		Map<String, String> veranstBind = new HashMap<String, String>();
		veranstBind.put("title", subject.getText().toString());
		veranstBind.put("begin", startTxt);
		veranstBind.put("end", endTxt);
		veranstBind.put("day", String.valueOf(day.getSelectedItemPosition()));
		int instructorSelected = dozent.getPositionForView(dozent.getSelectedView());
		veranstBind.put("lecturerId", String.valueOf(lehrende.bindSpinnerToDb.get(instructorSelected)));
		veranstBind.put("type", getSubjectTypeString(subjectType.getSelectedItemPosition()));
		veranstBind.put("place", room.getText().toString());
		return veranstBind;
	}

	public void setVeranstGuiInformation(int veranstId) {
		Map<String, String> veranstInformation = veranstaltung.get(veranstId);
		EditText subject = (EditText) findViewById(R.id.subject);
		EditText room = (EditText) findViewById(R.id.room);
		TimePicker timeBegin = (TimePicker) findViewById(R.id.time_anfang);
		timeBegin.setIs24HourView(true);
		TimePicker timeEnd = (TimePicker) findViewById(R.id.time_ende);
		timeEnd.setIs24HourView(true);
		Spinner day = (Spinner) findViewById(R.id.day);
		day.setSelection(1);
		Spinner dozent = (Spinner) findViewById(R.id.dozent_select);
		Spinner subjectType = (Spinner) findViewById(R.id.subject_type);

		subject.setText(veranstInformation.get("title"));
		room.setText(veranstInformation.get("vPlace"));
		String[] dateBegin = new String(veranstInformation.get("begin")).split(":");
		timeBegin.setCurrentHour(Integer.valueOf(dateBegin[0]));
		timeBegin.setCurrentMinute(Integer.valueOf(dateBegin[1]));
		String[] dateEnd = new String(veranstInformation.get("end")).split(":");
		timeEnd.setCurrentHour(Integer.valueOf(dateEnd[0]));
		timeEnd.setCurrentMinute(Integer.valueOf(dateEnd[1]));

		try {
			int spinnerIndex = lehrende.getIndexForInstructorId(Integer.parseInt(veranstInformation.get("lecturerId")));
			dozent.setSelection(spinnerIndex);
		} catch (NumberFormatException e) {
			dozent.setSelection(0);
		}

		day.setSelection(Integer.valueOf(veranstInformation.get("day")));
		subjectType.setSelection(getSubjectTypeIndex(veranstInformation.get("type")));
	}

	public boolean checkFields() {
		TextView subjectDescr = (TextView) findViewById(R.id.subject);
		if (subjectDescr.getEditableText().toString().equals("")) {
			Helper.printMessage(this, getString(R.string.please_fill) + getString(R.string.subject), getString(R.string.warning), false);
			return false;
		}
		// check date
		TimePicker timeBegin = (TimePicker) findViewById(R.id.time_anfang);
		timeBegin.setIs24HourView(true);
		TimePicker timeEnd = (TimePicker) findViewById(R.id.time_ende);
		timeEnd.setIs24HourView(true);

		Calendar calBegin = Calendar.getInstance();
		calBegin.set(Calendar.HOUR_OF_DAY, timeBegin.getCurrentHour());
		calBegin.set(Calendar.MINUTE, timeBegin.getCurrentMinute());

		Calendar calEnd = Calendar.getInstance();
		calEnd.set(Calendar.HOUR_OF_DAY, timeEnd.getCurrentHour());
		calEnd.set(Calendar.MINUTE, timeEnd.getCurrentMinute());

		if (calEnd.before(calBegin)) {
			Helper.printMessage(this, getString(R.string.check_time), getString(R.string.warning), false);
			return false;
		}
		return true;
	}

	public int getSubjectTypeIndex(String subject) {
		Resources res = getResources();
		int intType = 0;
		String[] subjectTypes = res.getStringArray(R.array.event_type);
		for (int i = 0; i < subjectTypes.length; i++) {
			if (subject.equals(subjectTypes[i]))
				intType = i;
		}
		return intType;
	}

	public String getSubjectTypeString(int index) {
		Resources res = getResources();
		String[] subjectTypes = res.getStringArray(R.array.event_type);
		return subjectTypes[index];
	}

	public void finish(View arg0) {
		finish();
	}
}