package com.example.medi_mitra_v1.Client;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_mitra_v1.Adapters.postAdapter;
import com.example.medi_mitra_v1.Models.Posts;
import com.example.medi_mitra_v1.R;
import com.example.medi_mitra_v1.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    DatabaseReference database;
    postAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    TextView name,Email,phone;
    Query query;
    TextView textView;
    ArrayList<Posts> list;
    ProgressDialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        name = findViewById(R.id.NameProEt);
        Email = findViewById(R.id.EmailProEt);
        phone = findViewById(R.id.phoneProET);
        String Puid = getIntent().getStringExtra("uid");
        setDetails(Puid);
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
                        Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        storage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.ProfilePostsRV);
        recyclerView.setHasFixedSize(true);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.show();
        textView = findViewById(R.id.ProfilenoItem);
        database = FirebaseDatabase.getInstance().getReference("Posts");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        query=  database.orderByChild("timestamp");
        list = new ArrayList<>();
        adapter = new postAdapter(this,list);
        recyclerView.setAdapter(adapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Posts reports = dataSnapshot.getValue(Posts.class);
                    String PUID = reports.getUid();
                    if(uid.equals(PUID))
                        list.add(0,reports);
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
    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}