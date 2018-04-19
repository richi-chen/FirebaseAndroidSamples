package com.ebookfrenzy.upstreamdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class UpstreamDemoActivity extends AppCompatActivity {

    static final String SenderID = "<your firebase sender ID here>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upstream_demo);
    }

    public void registerUser(View view) {

        EditText username = (EditText) findViewById(R.id.usernameText);

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SenderID + "@gcm.googleapis.com")
                .setMessageId(getRandomMessageId())
                .addData("action", "REGISTER")
                .addData("account", username.getText().toString())
                .build());
    }

    public void sendMessage(View view) {
        EditText recipient = (EditText) findViewById(R.id.recipientText);
        EditText message = (EditText) findViewById(R.id.messageText);

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SenderID + "@gcm.googleapis.com")
                .setMessageId(getRandomMessageId())
                .addData("action", "MESSAGE")
                .addData("recipient", recipient.getText().toString())
                .addData("message", message.getText().toString())
                .build());
    }
    static Random random = new Random();

    public String getRandomMessageId() {
        return "m-" + Long.toString(random.nextLong());
    }

}
