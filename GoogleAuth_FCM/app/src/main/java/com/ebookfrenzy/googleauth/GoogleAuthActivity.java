package com.ebookfrenzy.googleauth;

import android.content.Intent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.support.annotation.NonNull;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.common.ConnectionResult;

import android.net.Uri;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

public class GoogleAuthActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleAuth";
    private String idToken = null;
    private String userEmail = null;
    private static final String sender_id = "<your sender id here>";

    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1001;

    private ImageView profileImage;
    private TextView emailText;
    private TextView statusText;
    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

        profileImage = (ImageView) findViewById(R.id.profileImage);
        emailText = (TextView) findViewById(R.id.emailText);
        statusText = (TextView) findViewById(R.id.statusText);

        emailText.setText("");
        statusText.setText("Signed Out");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("773874449145-jto3ucs6e8k3824e3uvv9v5bnkglrvqh.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        fbAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    emailText.setText(user.getEmail());
                    statusText.setText("Signed In");
                    if (user.getPhotoUrl() != null) {
                        displayImage(user.getPhotoUrl());
                    }
                } else {
                    emailText.setText("");
                    statusText.setText("Signed Out");
                    profileImage.setImageResource(
                            R.drawable.common_google_signin_btn_icon_dark);

                }
            }
        };
    }

    public void signIn(View view) {
        Intent signInIntent =
                Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                idToken = account.getIdToken();
                userEmail = account.getEmail();
                Log.i(TAG, "Token = " + idToken);
                Log.i(TAG, "Email = " + userEmail);
                authWithFirebase(account);
            } else {
                this.notifyUser("Google sign-in failed.");
            }
        }
    }

    private void authWithFirebase(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(
                acct.getIdToken(), null);

        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    notifyUser("Firebase authentication failed.");
                                }
                            }
                        });
    }

    public void signOut(View view) {
        fbAuth.signOut();

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        notifyUser("Google Play Services failure.");
    }

    void displayImage(Uri imageUrl) {
        new DownloadImageTask((ImageView) findViewById(R.id.profileImage))
                .execute(imageUrl.toString());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            fbAuth.removeAuthStateListener(authListener);
        }
    }

    private void notifyUser(String message) {
        Toast.makeText(GoogleAuthActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }

    public void createGroup(View view) {

        String token1 = "<token 1 goes here>";
        String token2 = "<token 2 goes here>";

        CreateDeviceGroup task = new CreateDeviceGroup();
        task.execute(new String[] { token1, token2 });
    }

    public class CreateDeviceGroup extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                URL url =
                        new URL("https://android.googleapis.com/gcm/googlenotification");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);

                con.setRequestProperty("project_id", sender_id);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.connect();

                JSONObject data = new JSONObject();
                data.put("operation", "add");
                data.put("notification_key_name", userEmail);
                data.put("registration_ids",
                        new JSONArray(Arrays.asList(params)));
                data.put("id_token", idToken);

                OutputStream os = con.getOutputStream();
                os.write(data.toString().getBytes("UTF-8"));
                os.close();

                InputStream is = con.getInputStream();
                String responseString =
                        new Scanner(is, "UTF-8").useDelimiter("\\A").next();
                is.close();

                JSONObject response = new JSONObject(responseString);
                return response.getString("notification_key");

            } catch (Exception e) {
                Log.i(TAG, "FAILED " + e);
            }

            return "Failed to create device group";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Notification key: " +  result);
        }
    }
}
