package com.studiplaner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

public class Footer {
	public static void initFooter(Activity activity, Map<String, String> footerDefaultElement, Intent editIntent,
			Intent spinnerSelectedIntent, ArrayList<Map<String, String>> semesterSpinner) {
		ArrayList<Map<String, Object>> quickElements = new ArrayList<Map<String, Object>>();
		Map<String, Object> quickElement1 = new HashMap<String, Object>();
		quickElement1.put("title", activity.getString(R.string.edit));
		quickElement1.put("intent", editIntent);
		quickElements.add(quickElement1);
		QuickActions.initQuickActions(quickElements, activity, (ImageView) activity.findViewById(R.id.quick_action_footer),
				semesterSpinner, spinnerSelectedIntent);
	}
}
