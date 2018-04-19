package com.ebookfrenzy.realtimedb;

import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignedInActivity extends AppCompatActivity {

    private static FirebaseUser currentUser;
    private static final String TAG = "RealtimeDB";
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private EditText userText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        userText = (EditText) findViewById(R.id.userText);

        currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, RealtimeDBActivity.class));
            finish();
            return;
        }

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("/data");
        dbRef.addValueEventListener(changeListener);

    }

    ValueEventListener changeListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            String change = dataSnapshot.child(
                    currentUser.getUid()).child("message")
                    .getValue(String.class);

            userText.setText(change);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            notifyUser("Database error: " + databaseError.toException());
        }
    };

    private void notifyUser(String message) {
        Toast.makeText(SignedInActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }

    public void saveData(View view) {
        dbRef.child(currentUser.getUid()).child("message")
                .setValue(userText.getText().toString(), completionListener);
    }

    DatabaseReference.CompletionListener completionListener =
            new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        notifyUser(databaseError.getMessage());
                    }
                }
            };

    public void signOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(
                                    SignedInActivity.this,
                                    RealtimeDBActivity.class));
                            finish();
                        } else {
                            // Report error to user
                        }
                    }
                });
    }
}
