package com.licon.gcmasnssample.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Set;

/**
 * The FCMTokenHelper registers the app on the device with Firebase Cloud Messaging (FCM)
 */
public class FCMTokenHelper {
    public interface FCMTokenUpdateObserver {
        void onFCMTokenUpdate(final String fcmToken, boolean didTokenChange);
        void onFCMTokenUpdateFailed(final Exception ex);
    }

    private static final String LOG_TAG = FCMTokenHelper.class.getSimpleName();
    // Name of the shared preferences
    private static final String SHARED_PREFS_FILE_NAME = FCMTokenHelper.class.getName();
    // Keys in shared preferences
    private static final String SHARED_PREFS_KEY_DEVICE_TOKEN = "deviceToken";
    private final SharedPreferences sharedPreferences;
    private final FirebaseInstanceId instanceID;
    private final String fcmSenderID;
    volatile private String deviceToken;
    private Set<FCMTokenUpdateObserver> updateObservers;

    public FCMTokenHelper(final Context context, final String fcmSenderID) {
        if (fcmSenderID == null || fcmSenderID.isEmpty()) {
            throw new IllegalArgumentException("Missing FCM sender ID.");
        }
        this.fcmSenderID = fcmSenderID;
        this.instanceID = FirebaseInstanceId.getInstance();
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE_NAME,
            Context.MODE_PRIVATE);
        // load previously saved device token and endpoint arn
        deviceToken = sharedPreferences.getString(SHARED_PREFS_KEY_DEVICE_TOKEN, "");
        updateObservers = new HashSet<>();
    }

    public void init() {
        // Ensure device is registered for push and subscribe to the default topic.
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG,
                    "Initial App Startup - Ensuring device is registered for FCM push...");
                updateFCMToken();
            }
        }).start();
    }

    synchronized
    public void addTokenUpdateObserver(final FCMTokenUpdateObserver tokenUpdateObserver) {
        updateObservers.add(tokenUpdateObserver);
    }

    /**
     * Updates the FCM Token.
     */
    synchronized
    public void updateFCMToken() {
        String newDeviceToken;
        // GCM throws a NullPointerException in some failure cases.
        try {
            newDeviceToken = instanceID.getToken(fcmSenderID, FirebaseMessaging.INSTANCE_ID_SCOPE);
        } catch (final Exception re) {
            final String error = "Unable to register with FCM. " + re.getMessage();
            Log.e(LOG_TAG, error, re);
            for (FCMTokenUpdateObserver observer : updateObservers) {
                observer.onFCMTokenUpdateFailed(re);
            }
            return;
        }
        Log.d(LOG_TAG, "Current FCM Device Token:" + newDeviceToken);
        final boolean didTokenChange = !newDeviceToken.equals(deviceToken);
        if (didTokenChange) {
            Log.d(LOG_TAG, "FCM Device Token changed from: " + deviceToken);
            deviceToken = newDeviceToken;
            sharedPreferences.edit()
                .putString(SHARED_PREFS_KEY_DEVICE_TOKEN, deviceToken)
                .apply();
        }
        for (FCMTokenUpdateObserver observer : updateObservers) {
            observer.onFCMTokenUpdate(deviceToken, didTokenChange);
        }
    }

    public String getFCMToken() {
        return deviceToken;
    }
}
