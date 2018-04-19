package com.ebookfrenzy.cloudfunctions;

import com.google.firebase.messaging.FirebaseMessagingService;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMsgService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    public FirebaseMsgService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showMessage(remoteMessage.getNotification().getBody());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Title: " +
                    remoteMessage.getNotification().getTitle());

            Log.d(TAG, "Notification Message: " +
                    remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " +
                    remoteMessage.getData().get("MyKey1"));
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
