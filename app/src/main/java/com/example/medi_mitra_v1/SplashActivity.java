package com.example.medi_mitra_v1;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.medi_mitra_v1.Admin.AdminMainActivity;
import com.example.medi_mitra_v1.Client.ClientMainActivity;
import com.example.medi_mitra_v1.Login_Signup.LoginActivity;
import com.example.medi_mitra_v1.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();

            }
        },500);

    }
    private void checkUser() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            DocumentReference df = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
            //extract data
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d(TAG, "onSuccess: " + documentSnapshot.getData());
                    String email = documentSnapshot.getString("UserEmail");
                    String pass = documentSnapshot.getString("Password");
                    firebaseAuth.signInWithEmailAndPassword(email,pass)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(getApplicationContext(), "Auto-Login Successful", Toast.LENGTH_SHORT).show();
                                    checkUserAccessLevel(authResult.getUser().getUid());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Auto-Login Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                    ;
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SplashActivity.this, "Auto Login Failed : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else
        {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }


    private void checkUserAccessLevel(String uid) {
        DocumentReference df = firebaseFirestore.collection("Users").document(uid);
        //extract data
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "onSuccess: "+documentSnapshot.getData());
                        if(documentSnapshot.getString("isUser").equals("1")){
                            finish();
                            startActivity(new Intent(getApplicationContext(), ClientMainActivity.class));

                        }
                        else if(documentSnapshot.getString("isUser").equals("0")){
                            finish();
                            startActivity(new Intent(getApplicationContext(), AdminMainActivity.class));

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Unkonwn User Type", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
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








