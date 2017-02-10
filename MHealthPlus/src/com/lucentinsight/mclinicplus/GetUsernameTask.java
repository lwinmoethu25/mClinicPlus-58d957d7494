package com.lucentinsight.mclinicplus;

import java.io.IOException;


import android.app.Activity;
import android.os.AsyncTask;

public class GetUsernameTask extends AsyncTask<Activity, String, String>{

	 Activity mActivity;
	    String mScope;
	    String mEmail;

	    GetUsernameTask(Activity activity, String name, String scope) {
	        this.mActivity = activity;
	        this.mScope = scope;
	        this.mEmail = name;
	    }

		@Override
		protected String doInBackground(Activity... params) {
			try {
	            String token = fetchToken();
	            if (token != null) {
	                // Insert the good stuff here.
	                // Use the token to access the user's Google data.
	     
	            }
	        } catch (IOException e) {
	            // The fetchToken() method handles Google-specific exceptions,
	            // so this indicates something went wrong at a higher level.
	            // TIP: Check for network connectivity before starting the AsyncTask.
	   
	        }
	        return null;
		}

		/**
	     * Gets an authentication token from Google and handles any
	     * GoogleAuthException that may occur.
	     */
	    protected String fetchToken() throws IOException {
//	        try {
//	            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
//	        } catch (UserRecoverableAuthException userRecoverableException) {
//	            // GooglePlayServices.apk is either old, disabled, or not present
//	            // so we need to show the user some UI in the activity to recover.
//	        //    mActivity.handleException(userRecoverableException);
//	        } catch (GoogleAuthException fatalException) {
//	            // Some other type of unrecoverable exception has occurred.
//	            // Report and log the error as appropriate for your app.
//
//	        }
	        return null;
	    }

}
