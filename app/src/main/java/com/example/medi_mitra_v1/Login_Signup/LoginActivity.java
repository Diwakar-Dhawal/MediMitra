package com.example.medi_mitra_v1.Login_Signup;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_mitra_v1.Admin.AdminMainActivity;
import com.example.medi_mitra_v1.Client.ClientMainActivity;
import com.example.medi_mitra_v1.R;
import com.example.medi_mitra_v1.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    TextView signupTv,forgotPass;
    EditText emailLogin,passwordLogin;
    Button LoginBtn;
    FirebaseAuth firebaseAuth;
    ProgressBar pbLogin;
    DatabaseReference databaseReference;
    DocumentReference documentReference;
    FirebaseFirestore firebaseFirestore;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signupTv = findViewById(R.id.noAccountTv);
        emailLogin = findViewById(R.id.emailEt);
        forgotPass = findViewById(R.id.forgotTv);
        pbLogin = findViewById(R.id.ProgressBarLogin);
        passwordLogin = findViewById(R.id.passwordEt);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        LoginBtn = findViewById(R.id.loginBtn);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbLogin.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
                pbLogin.setVisibility(View.INVISIBLE);
            }
        });
        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbLogin.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                pbLogin.setVisibility(View.INVISIBLE);
            }
        });
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValid())
                {
                    pbLogin.setVisibility(View.VISIBLE);
                    LoginBtn.setEnabled(false);
                    firebaseAuth.signInWithEmailAndPassword(emailLogin.getText().toString(),passwordLogin.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    checkUserAccessLevel(authResult.getUser().getUid());
                                    LoginBtn.setEnabled(true);
                                    pbLogin.setVisibility(View.INVISIBLE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    LoginBtn.setEnabled(true);
                                    pbLogin.setVisibility(View.INVISIBLE);
                                }
                            })
                    ;
                }
            }
        });
    }
    private boolean checkValid()
    {
        boolean check=true;
        if(emailLogin.getText().toString().isEmpty()){
            emailLogin.setError("Email cannot be empty");
            check= false;
        }
        if(passwordLogin.getText().toString().isEmpty()) {
            passwordLogin.setError("Password cannot be empty");
            check= false;
        }
       return check;
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