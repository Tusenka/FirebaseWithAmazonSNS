package com.licon.gcmasnssample.firebase.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.licon.gcmasnssample.activity.MainActivity;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // Saving reg id to shared preferences
        saveRegId(refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

    private void saveRegId(String token) {
        SharedPreferences pref =
            getApplicationContext().getSharedPreferences(MainActivity.KEY_PREF_REG_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(MainActivity.REG_ID, token);
        editor.commit();
    }
}
