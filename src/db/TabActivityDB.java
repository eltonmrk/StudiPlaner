package db;

import android.app.TabActivity;

public class TabActivityDB extends TabActivity {
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
