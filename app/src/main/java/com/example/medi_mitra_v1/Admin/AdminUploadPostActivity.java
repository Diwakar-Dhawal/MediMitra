package com.example.medi_mitra_v1.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.medi_mitra_v1.Models.PostInfoModel;
import com.example.medi_mitra_v1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminUploadPostActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ImageView post;
    Button select,upload;
    EditText caption;
    Uri postUri;
    String name,email;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_upload_post);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        post = findViewById(R.id.uploadPostIV);
        select = findViewById(R.id.SelectPostBtn);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        upload = findViewById(R.id.UploadPostBtn);
        caption = findViewById(R.id.captionPostET);
        FirebaseFirestore.getInstance().collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String useremail = document.getString("UserEmail");
                                if (useremail.equals(email)) {
                                    name = document.getString("Full Name");

                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(AdminUploadPostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectPDF();
                }
                else {
                    ActivityCompat.requestPermissions(
                            AdminUploadPostActivity.this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);


                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(AdminUploadPostActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("Uploading File...");
                progressDialog.setProgress(0);
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference storageReference = storage.getReference();
                String timestamp = System.currentTimeMillis()+"";
                storageReference.child("Posts").child(timestamp).putFile(postUri)

                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while(!uriTask.isComplete());
                                Uri uri = uriTask.getResult();
                                DatabaseReference databaseReference = database.getReference();
                                PostInfoModel obj = new PostInfoModel("None",name,caption.getText().toString(),uri.toString(),"0",timestamp,FirebaseAuth.getInstance().getCurrentUser().getUid());
                                databaseReference.child("Posts").child(timestamp).setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(AdminUploadPostActivity.this, "Task Completed", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(AdminUploadPostActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                                progressDialog.hide();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.hide();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                int currentProgress = (int)(100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                                progressDialog.setProgress(currentProgress);
                            }
                        });

            }
        });
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);  //open file manager
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),0);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check valid selection
        if(requestCode==0  && resultCode==RESULT_OK && data != null)
        {
            postUri = data.getData();
            post.setImageURI(postUri);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Selection Error", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==9&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectPDF();
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Storage Permission Needed")
                    .setMessage("Storage Permission is Needed to Access External Files")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AdminUploadPostActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                            Intent intent=new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri=Uri.fromParts("package",getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Grant Permission from Permission Tab",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        }
    }

}