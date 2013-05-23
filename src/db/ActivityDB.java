package db;

import android.app.Activity;

public class ActivityDB extends Activity {
	DataBaseHelper dbh;

	@Override
	protected void onStart() {
		super.onStart();
		dbh = new DataBaseHelper(this);
	}

	@Override
	protected void onResume() {
		super.onPause();
		dbh.close();
	}
}
