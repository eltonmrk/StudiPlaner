package db;

import android.app.ListActivity;

public class ListActivityDB extends ListActivity {
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
