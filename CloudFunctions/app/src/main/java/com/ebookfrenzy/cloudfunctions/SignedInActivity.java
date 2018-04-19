package com.ebookfrenzy.cloudfunctions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignedInActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("online");

        currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, CloudFunctionsActivity.class));
            finish();
            return;
        }

        TextView email = (TextView) findViewById(R.id.email);

        email.setText(currentUser.getEmail());

        updateDatabase(currentUser);
    }

    public void updateDatabase(FirebaseUser currentUser) {
        dbRef.child(currentUser.getUid()).child("email")
                .setValue(currentUser.getEmail());
    }

    public void signOut(View view) {

        dbRef.child(currentUser.getUid()).removeValue();

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dbRef.child(currentUser.getUid()).removeValue();
                            startActivity(new Intent(
                                    SignedInActivity.this,
                                    CloudFunctionsActivity.class));
                            finish();
                        } else {
                            // Report error to user
                        }
                    }
                });
    }
}
