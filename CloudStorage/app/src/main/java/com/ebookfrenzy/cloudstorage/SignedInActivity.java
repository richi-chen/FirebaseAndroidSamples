package com.ebookfrenzy.cloudstorage;

import android.content.Intent;
import android.provider.MediaStore;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class SignedInActivity extends AppCompatActivity {

    private Uri videoUri;
    private static final int REQUEST_CODE = 101;
    private StorageReference videoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference storageRef =
                FirebaseStorage.getInstance().getReference();
        videoRef = storageRef.child("/videos/" + uid + "/userIntro.3gp");

    }

    public void upload(View view) {
        if (videoUri != null) {
            UploadTask uploadTask = videoRef.putFile(videoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignedInActivity.this,
                            "Upload failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(SignedInActivity.this, "Upload complete",
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            updateProgress(taskSnapshot);
                        }
                    });
        } else {
            Toast.makeText(SignedInActivity.this, "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress = (100 * uploadBytes) / fileSize;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbar);
        progressBar.setProgress((int) progress);
    }

    public void record(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void download(View view) {

        try {
            final File localFile = File.createTempFile("userIntro", "3gp");

            videoRef.getFile(localFile).addOnSuccessListener(
                    new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(
                                FileDownloadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(SignedInActivity.this, "Download complete",
                                    Toast.LENGTH_LONG).show();

                            final VideoView videoView =
                                    (VideoView) findViewById(R.id.videoView);
                            videoView.setVideoURI(Uri.fromFile(localFile));
                            videoView.start();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignedInActivity.this,
                            "Download failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(SignedInActivity.this,
                    "Failed to create temp file: " + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        videoUri = data.getData();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video saved to:\n" +
                        videoUri, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
