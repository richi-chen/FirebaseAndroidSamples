package com.ebookfrenzy.appindexing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        }
    }

    private void displayLandmark(String landmarkId) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);

        landmark =
                dbHandler.findLandmark(landmarkId);

        if (landmark != null) {

            if (landmark.getPersonal() == 0) {
                deleteButton.setEnabled(false);
            } else {
                deleteButton.setEnabled(true);
            }

            titleText.setText(landmark.getTitle());
            descriptionText.setText(landmark.getDescription());
        } else {
            titleText.setText("No Match Found");
        }
    }


}
