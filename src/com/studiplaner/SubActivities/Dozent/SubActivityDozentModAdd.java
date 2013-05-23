package com.studiplaner.SubActivities.Dozent;

import java.util.HashMap;
import java.util.Map;

import com.studiplaner.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SubActivityDozentModAdd extends SubActivityDozentMod{
	
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		addDozent(null);
	}
	
	public void onStop(){
		super.onStop();
	}
	
	public void addDozent(View arg0) {
		setContentView(R.layout.lecturers_add);
		Button button = (Button) findViewById(R.id.save);
		button.setText(getString(R.string.add));
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Map<String, String> prof = getDozentGuiInformation();
				lehrender.saveDozent((HashMap<String, String>) prof);
				finish();
			}
		});
	}
}
