package com.example.lesson14_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class StorageActivity extends AppCompatActivity {

    private final String TAG = "java.StorageActivity";
    StorageReference storageRef;

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        storageRef = FirebaseStorage.getInstance().getReference();



        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("UserInfo", currentUser.getDisplayName());
        Log.d("UserInfo", currentUser.getEmail());



        imageView = findViewById(R.id.iv);
        //downloadFile();

        checkForStartUploadFile();
    }

    private void downloadFile () {
        StorageReference imageRef = storageRef.child("thumucanh/Banner-08-QuangCaoWeb-hocLapTrinh-Online.png");

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                //imageView.setImageURI(uri);

                Glide.with(StorageActivity.this )
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e(TAG, exception.getMessage());
            }
        });
    }

    private boolean hasStoragePermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void checkForStartUploadFile() {
        boolean hasPermission = hasStoragePermission();
        if (hasPermission) {
            try {
                uploadFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    111);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                uploadFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() throws FileNotFoundException {

        InputStream stream = new FileInputStream(new File("/storage/emulated/0/DCIM/Camera/IMG_20211213_184620.jpg"));

        UploadTask uploadTask = storageRef.child("thumucanh/anhuplen.jpg").putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

                Log.d(TAG, taskSnapshot.getMetadata().getPath());
            }
        });
    }


}