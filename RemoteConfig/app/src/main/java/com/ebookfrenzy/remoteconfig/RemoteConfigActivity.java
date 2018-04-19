package com.ebookfrenzy.remoteconfig;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class RemoteConfigActivity extends AppCompatActivity {

    private FirebaseRemoteConfig fbRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_config);

        fbRemoteConfig = FirebaseRemoteConfig.getInstance();
        fbRemoteConfig.setDefaults(R.xml.remote_config_params);

        fbRemoteConfig.setConfigSettings(
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build());

        fbRemoteConfig.activateFetched();
        applyConfig();
        fbRemoteConfig.fetch(0);

    }

    protected void applyConfig() {

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
        TextView textView = (TextView) findViewById(R.id.welcomeText);

        String bg_color = fbRemoteConfig.getString("main_background_color");
        layout.setBackgroundColor(Color.parseColor(bg_color));
        textView.setTextSize(fbRemoteConfig.getLong("welcome_font_size"));
        textView.setText(fbRemoteConfig.getString("welcome_text"));
    }

    public void updateConfig(View view) {
        fbRemoteConfig.fetch(0).addOnCompleteListener(this,
                new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            fbRemoteConfig.activateFetched();
                            applyConfig();
                        } else {
                            // Fetch failed
                        }
                    }
                });;
    }


}
