package com.example.medi_mitra_v1.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_mitra_v1.R;
import com.example.medi_mitra_v1.Models.SharedPdfInfoModel;
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

public class AdminSharePdfActivity extends AppCompatActivity {

    EditText pdfName,docName,hName,Suid;
    Button select,upload;
    TextView noti;
    Uri pdfUri;
    String Clientuid;
    String ClientEmail;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    String email,sender="";
    String name="";
    boolean found = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_share_pdf);
        storage  = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        Suid = findViewById(R.id.ShareUIDEt);
        select = findViewById(R.id.ShareselectPdfUPF);
        upload = findViewById(R.id.ShareToDatabaseUPF);
        noti = findViewById(R.id.SharePdfNameUPF);
        pdfName =findViewById(R.id.ShareEtPdfNameUPF);
        docName =findViewById(R.id.ShareEtDoctorNameUPF);
        hName =findViewById(R.id.ShareEtHospitalNameUPF);
        email= FirebaseAuth.getInstance().getCurrentUser().getEmail();
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
                                    setSenderName(name,email);

                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(AdminSharePdfActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(
                            AdminSharePdfActivity.this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(Suid.getText().toString()) || pdfUri==null || TextUtils.isEmpty(hName.getText().toString()) || TextUtils.isEmpty(docName.getText().toString()) || TextUtils.isEmpty(pdfName.getText().toString()))
                    Toast.makeText(getApplicationContext(), "Enter All Details", Toast.LENGTH_SHORT).show();

                else{
                    ClientEmail = Suid.getText().toString();
                    FirebaseFirestore.getInstance().collection("Users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String useremail = document.getString("UserEmail");
                                            if(useremail.equals(ClientEmail))
                                            {
                                                found = true;
                                                Clientuid=document.getString("UserUid");
                                            }
                                        }
                                        if(found) {
                                            uploadFile(pdfUri);
                                            found=false;
                                        }
                                        else
                                            Toast.makeText(AdminSharePdfActivity.this, "Email Does Not Exist in Database", Toast.LENGTH_SHORT).show();

                                    } else {

                                    }
                                }
                            });

                }
            }
        });
    }

    private void uploadFile(Uri pdfUri) {
        try {

            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Sharing File...");
            progressDialog.setProgress(0);
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            StorageReference storageReference = storage.getReference();
            String timestamp = System.currentTimeMillis() + "";

            storageReference.child("Users").child(Clientuid).child("Shared").child(timestamp).putFile(pdfUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isComplete()) ;

                            Uri uri = uriTask.getResult();


                            DatabaseReference databaseReference = database.getReference();
                            SharedPdfInfoModel obj = new SharedPdfInfoModel(pdfName.getText().toString(), docName.getText().toString(), hName.getText().toString(), uri.toString(), timestamp, "false", sender,"false");
                            databaseReference.child("Users").child(Clientuid).child("Shared").child(timestamp).setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Task Failed", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                            progressDialog.hide();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.hide();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            int currentProgress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.setProgress(currentProgress);
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Permission Denied...", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);  //open file manager
        startActivityForResult(intent,0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check valid selection
        if(requestCode==0  && resultCode==RESULT_OK && data != null)
        {
            pdfUri = data.getData();
            noti.setText(pdfUri.getPath());
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Selection Error", Toast.LENGTH_SHORT).show();
        }


    }
    public void setSenderName(String Sname, String Semail)
    {
        sender = Sname+" ("+Semail+")";
    }
}
