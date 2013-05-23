package com.studiplaner;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ActivityExport extends Activity{
	Activity activity;
	@Override
	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.export_data);
		activity = this;
		super.onCreate(savedInstanceState);
		Bundle b = this.getIntent().getExtras();
		final int semesterId = b.getInt("semesterId");
		Button export = (Button) findViewById(R.id.save);
		final CheckBox exportAll = (CheckBox) findViewById(R.id.export_all_semesters);
		export.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Export export = new Export(activity);
				export.openDB();
				try {
					String exportXML;
					if (exportAll.isChecked())
						exportXML = export.createXMLFromAllSemester(null);
					else
						exportXML = export.createXMLFromAllSemester(semesterId);
					if (!exportXML.equals(""))
					{
						TextView receiver = (TextView) findViewById(R.id.email_input);
						Helper.sendEmail(activity, receiver.getText().toString(), activity.getString(R.string.subjectEmail), activity.getString(R.string.contentEmail) + exportXML);
					}
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				export.closeDB();
			}
		});
	}
	
	public void finish(View arg0){
		finish();
	}
}
