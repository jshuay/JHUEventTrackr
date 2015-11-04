package com.jhueventtrackr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class LoginActivity extends Activity {
	private UiLifecycleHelper _uiHelper;
	private int returnCode;

	private StatusCallback _callback = new StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		_uiHelper = new UiLifecycleHelper(this, _callback);
		_uiHelper.onCreate(savedInstanceState);
		returnCode = 0;

		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setPublishPermissions(new String[] { "create_event" });
	}

	@Override
	public void onResume() {
		super.onResume();
		_uiHelper.onResume();
		checkAppState();
	}

	@Override
	public void onPause() {
		super.onPause();
		_uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		_uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		Log.d("water", "got to sessionstate change");
		if (state.isOpened()) {
			Log.d("water", "got to isopen");
			// If the session state is open:
			System.out.println("opened");
			Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						EventCollection.userId = user.getId();
						EventCollection.userFName = user.getFirstName();
						EventCollection.userLName = user.getLastName();
					}
				}
			}).executeAsync();
			redirectToMap();
		} else if (state.isClosed()) {
			// If the session state is closed:
			// Show the login fragment
			System.out.println("closed");
		}
	}

	private void redirectToMap() {
		Log.d("water", "got to redirect");
		returnCode = 1;
		finish();
	}

	private void checkAppState() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			Log.d("water", "got to redirect (2)");
			redirectToMap();
		}
	}

	@Override
	public void finish() {
		Log.d("water", "got to redirect (3)");
		Intent intent = new Intent();
		setResult(returnCode, intent);
		returnCode = 0;
		super.finish();
		overridePendingTransition(0, 0);
	}

}
