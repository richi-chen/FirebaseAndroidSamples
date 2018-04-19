package com.ebookfrenzy.anonauth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class AnonAuthActivity extends AppCompatActivity {

    private static final int ANON_MODE = 100;
    private static final int CREATE_MODE = 101;
    private int buttonMode = ANON_MODE;

    private static final String TAG = "AnonAuthSDK";
    private TextView userText;
    private TextView statusText;
    private EditText emailText;
    private EditText passwordText;
    private Button softButton;

    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anon_auth);

        userText = (TextView) findViewById(R.id.userText);
        statusText = (TextView) findViewById(R.id.statusText);
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        softButton = (Button) findViewById(R.id.softButton);

        fbAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    userText.setText(user.getEmail());
                    statusText.setText("Signed In");

                } else {
                    userText.setText("");
                    statusText.setText("Signed Out");
                    softButton.setText("Anonymous Sign-in");
                    buttonMode = ANON_MODE;
                }
            }
        };

    }

    public void softButtonClick(View view) {
        if (buttonMode == ANON_MODE) {
            anonSignIn();
        } else {
            createAccount();
        }
    }

    public void anonSignIn() {
        fbAuth.signInAnonymously()
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    Toast.makeText(AnonAuthActivity.this,
                                            "Authentication failed. "
                                                    + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    softButton.setText("Create an Account");
                                    buttonMode = CREATE_MODE;
                                }
                            }
                        });
    }

    public void createAccount() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.length() == 0) {
            emailText.setError("Enter an email address");
            return;
        }

        if (password.length() < 6) {
            passwordText.setError("Password must be at least 6 characters");
            return;
        }

        AuthCredential credential =
                EmailAuthProvider.getCredential(email, password);

        fbAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(AnonAuthActivity.this,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }

    public void signOut(View view) {
        fbAuth.signOut();
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

}
