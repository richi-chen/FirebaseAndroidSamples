package com.ebookfrenzy.dynamiclinks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import android.support.annotation.NonNull;

import java.net.URL;
import java.net.URLDecoder;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

public class DynamicLinksActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    private TextView statusText;
    private Button shareButton;
    private Uri dynamicLink = null;
    private static final String TAG = "DynamicLinks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_links);

        statusText = (TextView) findViewById(R.id.statusText);
        shareButton = (Button) findViewById(R.id.shareButton);

        shareButton.setEnabled(false);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();

        boolean launchDeepLink = true;

        AppInvite.AppInviteApi.getInvitation(googleApiClient, this,
                launchDeepLink).setResultCallback(

                new ResultCallback<AppInviteInvitationResult>() {
                    @Override
                    public void onResult(
                            @NonNull AppInviteInvitationResult result) {

                        if (result.getStatus().isSuccess()) {

                            Intent intent = result.getInvitationIntent();
                            String deepLink =
                                    AppInviteReferral.getDeepLink(intent);

                            handleDeeplink(deepLink);
                        } else {
                            Log.i(TAG, "Deeplink not found");
                        }
                    }
                });

    }

    public void getLink(View view) {
        String appCode = "<app_code>";
        final Uri deepLink = Uri.parse("http://example.com/promo?discount");

        String packageName = getApplicationContext().getPackageName();

        // Build the link with all required parameters
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority(appCode + ".app.goo.gl")
                .path("/")
                .appendQueryParameter("link", deepLink.toString())
                .appendQueryParameter("apn", packageName);

        dynamicLink = builder.build();
        shareButton.setEnabled(true);
    }

    public void shareLink(View view) {
        try {
            URL url = new URL(URLDecoder.decode(dynamicLink.toString(),
                    "UTF-8"));
            Log.i(TAG, "URL = " + url.toString());
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
            intent.putExtra(Intent.EXTRA_TEXT, url.toString());
            startActivity(intent);
        } catch (Exception e) {
            Log.i(TAG, "Could not decode Uri: " + e.getLocalizedMessage());
        }
    }

    public void handleDeeplink(String deepLink) {

        Uri deepUri = Uri.parse(deepLink);

        if (deepUri.getPath().equals("/credit")) {
            statusText.setText(deepUri.getQuery() +
                    " points have been applied to your account");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services Error: "
                        + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}
