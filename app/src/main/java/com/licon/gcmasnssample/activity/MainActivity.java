package com.licon.gcmasnssample.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.licon.gcmasnssample.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String KEY_PREF_REG_ID = "KEY_PREF_REG_ID";
    public static final String REG_ID = "REG_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseMessaging.getInstance().subscribeToTopic("global"); // random name
        logFirebaseRegId();
    }

    private void logFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(KEY_PREF_REG_ID, 0);
        String regId = pref.getString(REG_ID, null);
        Log.e(TAG, "Firebase reg id: " + regId);
    }
}
