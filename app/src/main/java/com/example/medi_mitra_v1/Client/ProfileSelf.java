package com.example.medi_mitra_v1.Client;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_mitra_v1.Adapters.DropboxAdapter;
import com.example.medi_mitra_v1.Models.Reports;
import com.example.medi_mitra_v1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSelf extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    DatabaseReference database;
    DropboxAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    TextView name,Email,phone;
    Query query;
    CircleImageView profile;
    ImageButton addImg;
    TextView textView;
    ArrayList<Reports> list;
    ProgressDialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    public ProfileSelf() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile_self, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        name = v.findViewById(R.id.NameProEtProSelf);
        profile = v.findViewById(R.id.profile_image_ivProSelf);
        Email = v.findViewById(R.id.EmailProEtProSelf);
        phone = v.findViewById(R.id.phoneProETProSelf);
        String Puid = firebaseAuth.getCurrentUser().getUid();
        addImg =v.findViewById(R.id.profile_add_profilePicProSelf);
        try {
            FirebaseDatabase.getInstance().getReference().child("Users").child(Puid).child("Images").child("ProfilePic").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {

                        Picasso.get().load(snapshot.getValue().toString()).into(profile);
                    }
                    else
                    {
                        profile.setImageResource(R.drawable.ic_person_fill);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        catch (Exception e)
        {

        }
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {

                    openGallery();

                }
                else {
                    Toast.makeText(getContext(), "Please Grant Permission..", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);

                }
            }
        });
        recyclerView = v.findViewById(R.id.ProfilePostsRVProSelf);
        textView = v.findViewById(R.id.ProfilenoItemProSelf);
        setDetails(Puid);
        return v;
    }
    public void setDetails(String uid)
    {
        DocumentReference df = firebaseFirestore.collection("Users").document(uid);
        //extract data
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "onSuccess: " + documentSnapshot.getData());
                        String email = documentSnapshot.getString("UserEmail");
                        String full_name = documentSnapshot.getString("Full Name");
                        String Phone_number = documentSnapshot.getString("PhoneNumber");

                        name.setText(full_name);
                        Email.setText(email);
                        phone.setText(Phone_number);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        storage = FirebaseStorage.getInstance();

        recyclerView.setHasFixedSize(true);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading..");
        dialog.show();
        database = FirebaseDatabase.getInstance().getReference("Users").child(""+uid).child("Uploads");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        query=  database.orderByChild("timestamp");
        list = new ArrayList<>();
        adapter = new DropboxAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Reports reports = dataSnapshot.getValue(Reports.class);
                    if(reports.getIndropbox().equals("true"))
                        list.add(reports);
                }

                adapter.notifyDataSetChanged();
                if(list.size()<1)
                {
                    textView.setVisibility(View.VISIBLE);
                }
                else
                {
                    textView.setVisibility(View.INVISIBLE);
                }
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.hide();
            }
        });
    }
    private void openGallery()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.ACTION_GET_CONTENT, true);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check valid selection

        if(requestCode==1  && resultCode==RESULT_OK && data != null)
        {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading Profile Image...");
            progressDialog.setProgress(0);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String timestamp = System.currentTimeMillis()+"";
            StorageReference storageReference = storage.getReference();
            storageReference.child("Users").child(uid).child("Images").child("Profile").putFile(data.getData())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isComplete());
                            Uri uri = uriTask.getResult();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Users").child(uid).child("Images").child("ProfilePic").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(getActivity(), "Profile Uploaded", Toast.LENGTH_SHORT).show();
                                        Picasso.get().load(uri.toString()).into(profile);
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(), "Task Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            progressDialog.hide();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
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
        else
        {
            Toast.makeText(getContext(), "Selection Error", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==9&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getContext(), "Permission Allowed....", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getContext(), "Permission Denied....", Toast.LENGTH_SHORT).show();
        }
    }
}