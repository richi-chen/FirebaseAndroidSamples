package com.ebookfrenzy.appindexing;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AppIndexingActivity extends AppCompatActivity {

    private static final String TAG = "AppIndexActivity";
    private EditText idText;
    private EditText titleText;
    private EditText descriptionText;
    private static final int PERSONAL = 1;
    private static final int PUBLIC = 0;
    MyDBHandler dbHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_indexing);

        idText = (EditText) findViewById(R.id.idText);
        titleText = (EditText) findViewById(R.id.titleText);
        descriptionText = (EditText) findViewById(R.id.descriptionText);

        dbHandler = new MyDBHandler(this, null, null, 1);
    }

    public void findLandmark(View view) {

        if (!idText.getText().equals("")) {
            Landmark landmark = dbHandler.findLandmark(idText.getText().toString());

            if (landmark != null) {
                Log.i(TAG, "Found landmark = " + landmark.getTitle());
                Uri uri = Uri.parse("http://example.com/landmarks/" + landmark.getID());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                titleText.setText("No Match");
            }
        }
    }

    public void addLandmark(View view) {

        Landmark landmark =
                new Landmark(idText.getText().toString(), titleText.getText().toString(),
                        descriptionText.getText().toString(), 1);

        dbHandler.addLandmark(landmark);
        idText.setText("");
        titleText.setText("");
        descriptionText.setText("");

    }
}
