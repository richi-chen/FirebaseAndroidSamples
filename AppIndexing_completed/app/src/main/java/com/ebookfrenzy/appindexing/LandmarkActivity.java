package com.ebookfrenzy.appindexing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;

public class LandmarkActivity extends AppCompatActivity {

    private static final String TAG = "Database";
    private TextView titleText;
    private TextView descriptionText;
    private Button deleteButton;
    private Landmark landmark = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        titleText = (TextView) findViewById(R.id.titleText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        
        handleIntent(getIntent());

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (landmark != null) {
            indexLandmark();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void logPublicAction() {
        Action action = Actions.newView(landmark.getTitle(),
                landmark.getLandmarkURL());
        FirebaseUserActions.getInstance().end(action);
    }

    private void logPersonalAction() {
        Action action = new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(landmark.getTitle(),landmark.getLandmarkURL())
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();

        FirebaseUserActions.getInstance().end(action);
    }

    private void handleIntent(Intent intent) {

        String action = intent.getAction();
        String data = intent.getDataString();

        Log.i(TAG, "In handleIntent and data = " + data);

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String landmarkId = data.substring(data.lastIndexOf("/") + 1);
            Log.i(TAG, "landmarkId = " + landmarkId);
            displayLandmark(landmarkId);
        }

    }

    public void deleteLandmark(View view) {

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);

        if (landmark != null) {
            dbHandler.deleteLandmark(landmark.getID());
            titleText.setText("");
            descriptionText.setText("");
            deleteButton.setEnabled(false);
            FirebaseAppIndex.getInstance().remove(landmark.getLandmarkURL());
        }
    }

    private void displayLandmark(String landmarkId) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);

        landmark =
                dbHandler.findLandmark(landmarkId);

        if (landmark != null) {

            if (landmark.getPersonal() == 0) {
                logPublicAction();
                deleteButton.setEnabled(false);
            } else {
                logPersonalAction();
                deleteButton.setEnabled(true);
            }

            titleText.setText(landmark.getTitle());
            descriptionText.setText(landmark.getDescription());
        } else {
            titleText.setText("No Match Found");
        }
    }
    private void indexLandmark() {

        Indexable indexableLandmark = new Indexable.Builder()
                .setName(landmark.getTitle())
                .setUrl(landmark.getLandmarkURL())
                .setDescription(landmark.getDescription())
                .build();

        Task<Void> task =
                FirebaseAppIndex.getInstance().update(indexableLandmark);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "App Indexing added "
                        + landmark.getTitle() + " to " +
                        "index");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "App Indexing failed to add " +
                        landmark.getTitle() + " to index. " +
                        "" + exception.getMessage());
            }
        });
    }



}
