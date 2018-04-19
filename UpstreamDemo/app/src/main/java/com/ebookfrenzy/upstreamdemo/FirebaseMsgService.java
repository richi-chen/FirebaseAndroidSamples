package com.ebookfrenzy.upstreamdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";

    public FirebaseMsgService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            final String message = remoteMessage.getData().get("message");
            showMessage(message);
        }
    }

    public void showMessage(final String message)
    {
        final Context context = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                Toast toast = Toast.makeText(context, message,
                        Toast.LENGTH_LONG);
                toast.show();
            }
        });
    };

}
