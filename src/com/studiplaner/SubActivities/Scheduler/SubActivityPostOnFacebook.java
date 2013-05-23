package com.studiplaner.SubActivities.Scheduler;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseDialogListener;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginButton;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;
import com.studiplaner.R;
import com.studiplaner.db.ActivityDB;
import com.studiplaner.db.model.Subject;


public class SubActivityPostOnFacebook extends ActivityDB {

	// Your Facebook Application ID must be set before running this example
	// See http://www.facebook.com/developers/createapp.php
	public static final String APP_ID = "149258585100848";

	private static final String[] PERMISSIONS = new String[] { "publish_stream", "read_stream", "offline_access" };
	private LoginButton mLoginButton;
	private TextView mText;
	private Button mRequestButton;
	private Button mPostButton;
	private Button mDeleteButton;
	private ImageButton mBack;

	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;

	private Subject veranstaltung;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (APP_ID == null) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see Example.java");
		}
		veranstaltung = new Subject();
	}

	@Override
	protected void onStart() {
		super.onStart();
		buildFacebook();
	}

	public void buildFacebook() {
		Bundle b = this.getIntent().getExtras();
		int subjectId = b.getInt("veranstaltungId");
		final Map<String, String> subjectMap = veranstaltung.get(subjectId);

		setContentView(R.layout.fb_main);
		mLoginButton = (LoginButton) findViewById(R.id.login);
		mText = (TextView) SubActivityPostOnFacebook.this.findViewById(R.id.txt);
		mRequestButton = (Button) findViewById(R.id.requestButton);
		mPostButton = (Button) findViewById(R.id.postButton);
		mDeleteButton = (Button) findViewById(R.id.deletePostButton);
		// mBack = (ImageButton) findViewById(R.id.back);

		mFacebook = new Facebook();
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);

		SessionStore.restore(mFacebook, this);
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		mLoginButton.init(mFacebook, PERMISSIONS);

		mRequestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mAsyncRunner.request("me", new SampleRequestListener());
			}
		});
		mRequestButton.setVisibility(mFacebook.isSessionValid() ? View.VISIBLE : View.INVISIBLE);

		mPostButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String attachment = "";
				attachment += "{\"name\":\"" + subjectMap.get("type") + ": " + subjectMap.get("title") + "\",";
				attachment += "\"caption\":\"" + getString(R.string.time) + ": " + subjectMap.get("begin") + " ~ " + subjectMap.get("end")
						+ "\"";
				if (subjectMap.get("place") != null) {
					attachment += ", \"description\":\"" + getString(R.string.room) + ": " + subjectMap.get("place") + "\"}";
				} else {
					attachment += "}";
				}
				Bundle parameters = new Bundle();
				parameters.putString("message", "");
				parameters.putString("attachment", attachment);
				mFacebook.dialog(SubActivityPostOnFacebook.this, "stream.publish", parameters, new SampleDialogListener());
			}
		});
		mPostButton.setVisibility(mFacebook.isSessionValid() ? View.VISIBLE : View.INVISIBLE);
	}

	public class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {
			mText.setText("You have logged in! ");
			mRequestButton.setVisibility(View.VISIBLE);
			mPostButton.setVisibility(View.VISIBLE);
		}

		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
		}
	}

	public class SampleLogoutListener implements LogoutListener {
		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		public void onLogoutFinish() {
			mText.setText("You have logged out! ");
			mRequestButton.setVisibility(View.INVISIBLE);
			mPostButton.setVisibility(View.INVISIBLE);
		}
	}

	public class SampleRequestListener extends BaseRequestListener {

		public void onComplete(final String response) {
			try {
				// process the response here: executed in background thread
				Log.d("Facebook-Example", "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				final String name = json.getString("name");

				// then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."
				SubActivityPostOnFacebook.this.runOnUiThread(new Runnable() {
					public void run() {
						mText.setText("Hello there, " + name + "!");
					}
				});
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
		}
	}

	public class WallPostRequestListener extends BaseRequestListener {

		public void onComplete(final String response) {
			Log.d("Facebook-Example", "Got response: " + response);
			String message = "<empty>";
			try {
				JSONObject json = Util.parseJson(response);
				message = json.getString("message");
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
			final String text = "Your Wall Post: " + message;
			SubActivityPostOnFacebook.this.runOnUiThread(new Runnable() {
				public void run() {
					mText.setText(text);
				}
			});
		}
	}

	public class WallPostDeleteListener extends BaseRequestListener {

		public void onComplete(final String response) {
			if (response.equals("true")) {
				Log.d("Facebook-Example", "Successfully deleted wall post");
				SubActivityPostOnFacebook.this.runOnUiThread(new Runnable() {
					public void run() {
						mDeleteButton.setVisibility(View.INVISIBLE);
						mText.setText("Deleted Wall Post");
					}
				});
			} else {
				Log.d("Facebook-Example", "Could not delete wall post");
			}
		}
	}

	public class SampleDialogListener extends BaseDialogListener {
		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
				mAsyncRunner.request(postId, new WallPostRequestListener());
				mDeleteButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mAsyncRunner.request(postId, new Bundle(), "DELETE", new WallPostDeleteListener());
					}
				});
				mDeleteButton.setVisibility(View.VISIBLE);
			} else {
				Log.d("Facebook-Example", "No wall post made");
			}
		}
	}

	public void finish(View arg0) {
		finish();
	}
}