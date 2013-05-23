package com.studiplaner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionBar {
	public static void initActionBar(int mainLayout, final Activity activity, String title, String titleEdit, final Intent createIntent) {
		activity.setContentView(mainLayout);
		TextView titleTV = (TextView) activity.findViewById(R.id.title_dashboard);
		titleTV.setText(title);

		// home button
		ImageView homeBt = (ImageView) activity.findViewById(R.id.home);
		homeBt.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				activity.finish();
			}
		});

		// create button
		ImageView createBt = (ImageView) activity.findViewById(R.id.create);
		createBt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Bundle b = activity.getIntent().getExtras();
				if (b != null) {
					activity.startActivity(createIntent);
				} else {
					Helper.printMessage(activity, "Bitte zuerst auswählen", "OK", false);
				}
			}
		});
	}
}
