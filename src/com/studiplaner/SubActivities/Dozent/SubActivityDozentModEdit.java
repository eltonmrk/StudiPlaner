package com.studiplaner.SubActivities.Dozent;

import java.util.HashMap;
import java.util.Map;

import com.studiplaner.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SubActivityDozentModEdit extends SubActivityDozentMod {
	int lecturerId;
	
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		Bundle b = this.getIntent().getExtras();
		lecturerId = b.getInt("lecturerId");
		editDozent();
	}
	
	public void onStop(){
		super.onStop();
	}
	
	public void editDozent() {
		setContentView(R.layout.lecturers_add);
		Button button = (Button) findViewById(R.id.save);
		button.setText(getString(R.string.update));
		setDozentGuiInformation(lecturerId);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Map<String, String> dozentMap = getDozentGuiInformation();
				dozentMap.put("id", new Integer(lecturerId).toString());
				lehrender.updateDozent((HashMap<String, String>) dozentMap);
				finish();
			}
		});
	}
}