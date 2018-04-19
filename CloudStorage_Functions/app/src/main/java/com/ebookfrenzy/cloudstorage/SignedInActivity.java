package com.ebookfrenzy.cloudstorage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import static android.R.attr.bitmap;

public class SignedInActivity extends AppCompatActivity {

    private Bitmap mImageBitmap;
    private static final String TAG = "CloudStorage";
    private Uri photoUri;
    private static final int REQUEST_CODE = 101;
    private StorageReference photoRef;
    private StorageReference storageRef;
    private ImageView imageView;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        storageRef =
                FirebaseStorage.getInstance().getReference();
        photoRef = storageRef.child("/photos/" + uid + "/mountain.jpg");
        photoUri = Uri.parse("android.resource://com.ebookfrenzy.cloudstorage/" + R.drawable.mountain);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.mountain);
    }


    public void upload(View view) {
        if (photoUri != null) {
            UploadTask uploadTask = photoRef.putFile(photoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignedInActivity.this, "Upload failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SignedInActivity.this, "Upload complete",
                            Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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

    public void download(View view) {

        try {

            photoRef = storageRef.child("/photos/" + uid + "/mono_mountain.jpg");

            final File localFile = File.createTempFile("mono_mountain", "jpg");

            photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                    Toast.makeText(SignedInActivity.this, "Download complete",
                            Toast.LENGTH_LONG).show();

                    Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                    imageView.setImageBitmap(myBitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignedInActivity.this, "Download failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(SignedInActivity.this, "Failed to create temp file: " + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {
        @SuppressWarnings("VisibleForTests") long fileSize = taskSnapshot.getTotalByteCount();
        @SuppressWarnings("VisibleForTests") long uploadBytes = taskSnapshot.getBytesTransferred();
        long progress = (100 * uploadBytes) / fileSize;
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbar);
        progressBar.setProgress((int) progress);
    }
}
