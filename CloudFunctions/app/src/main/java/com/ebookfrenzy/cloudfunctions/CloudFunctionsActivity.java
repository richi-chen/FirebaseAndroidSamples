package com.ebookfrenzy.cloudfunctions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.firebase.ui.auth.ui.ResultCodes.RESULT_NO_NETWORK;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class CloudFunctionsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_functions);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, SignedInActivity.class));
            finish();
        } else {
            authenticateUser();
        }

        FirebaseMessaging.getInstance().subscribeToTopic("OnlineUsers");

    }

        private void authenticateUser() {
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setProviders(getProviderList())
                            .setIsSmartLockEnabled(false)
                            .build(),
                    REQUEST_CODE);
        }

        private List<AuthUI.IdpConfig> getProviderList() {

        List<AuthUI.IdpConfig> providers = new ArrayList<>();

        providers.add(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        return providers;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, SignedInActivity.class));
                return;
            }

            if (resultCode == RESULT_CANCELED) {
                // User cancelled Sign-in
                return;
            }

            if (resultCode == RESULT_NO_NETWORK) {
                // Device has no network connection
                return;
            }
        }
    }
}
