package com.studiplaner.db;

import android.app.Activity;

public class ActivityDB extends Activity {
	public DataBaseHelper dbh;

	@Override
	protected void onStart() {
		super.onStart();
		DataBaseHelper.isFixture = true;
		dbh = new DataBaseHelper(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbh.close();
	}
}
