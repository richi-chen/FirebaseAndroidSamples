package com.ebookfrenzy.upstreamdemo;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.FirebaseInstanceId;

import android.util.Log;

public class FirebaseIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Registration Token: = " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

    }

}
