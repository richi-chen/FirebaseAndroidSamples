package com.ebookfrenzy.appindexing;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;

import java.util.ArrayList;
import java.util.List;
public class AppIndexingService extends IntentService {

    public static final String TAG = "AppIndexingService";

    public AppIndexingService() {
        super("AppIndexingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<Indexable> indexableLandmarks = new ArrayList<>();

        for (Landmark landmark : getPersonalLandmarks()) {

            if (landmark != null) {

                Indexable personalLandmark = Indexables.digitalDocumentBuilder()
                        .setName(landmark.getTitle())
                        .setText(landmark.getDescription())
                        .setUrl(landmark.getLandmarkURL())
                        .build();

                Task<Void> task =
                        FirebaseAppIndex.getInstance().update(personalLandmark);

                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "AppIndexService: Successfully added landmark");

                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "AppIndexService: Failed to add landmark " +
                                "" + exception.getMessage());
                    }
                });

            }
        }
    }

    private List<Landmark> getPersonalLandmarks() {

        ArrayList<Landmark> landmarks = new ArrayList();

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);

        landmarks = dbHandler.findAllLandmarks();

        for (Landmark landmark : landmarks) {
            if (landmark.getPersonal() == 1) {
                landmarks.add(landmark);
            }
        }
        return landmarks;
    }
}

