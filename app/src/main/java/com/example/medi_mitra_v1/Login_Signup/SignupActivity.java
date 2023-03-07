package com.example.medi_mitra_v1.Login_Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_mitra_v1.Client.ClientMainActivity;
import com.example.medi_mitra_v1.R;
import com.example.medi_mitra_v1.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText nameSign,emailSign,phoneSign,passSign,cnfpassSign;
    Button signupBtn;
    TextView loginTv;
    ProgressBar pbSignup;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        pbSignup = findViewById(R.id.ProgressBarSignUp);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        loginTv = findViewById(R.id.alreadyAccountTV);
        nameSign = findViewById(R.id.nameEtSign);
        emailSign = findViewById(R.id.emailEtSign);
        phoneSign = findViewById(R.id.phoneEt);
        passSign = findViewById(R.id.passwordEtSign);
        cnfpassSign = findViewById(R.id.CnfpasswordEtSign);
        signupBtn = findViewById(R.id.signupBtn);

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbSignup.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                pbSignup.setVisibility(View.INVISIBLE);
                finish();
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInput())
                {
                    pbSignup.setVisibility(View.VISIBLE);
                    signupBtn.setEnabled(false);
                        firebaseAuth.createUserWithEmailAndPassword(emailSign.getText().toString(), passSign.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();


                                        DocumentReference documentReference = firebaseFirestore.collection("Users").document(user.getUid());
                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("Full Name", nameSign.getText().toString());
                                        userInfo.put("UserEmail", emailSign.getText().toString());
                                        userInfo.put("PhoneNumber", phoneSign.getText().toString());
                                        userInfo.put("Password", passSign.getText().toString());
                                        userInfo.put("isUser", "1");
                                        userInfo.put("UserUid",user.getUid().toString());

                                        documentReference.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), ClientMainActivity.class));
                                                pbSignup.setVisibility(View.INVISIBLE);
                                                signupBtn.setEnabled(true);
                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                user.delete();
                                                pbSignup.setVisibility(View.INVISIBLE);
                                                signupBtn.setEnabled(true);
                                                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pbSignup.setVisibility(View.INVISIBLE);
                                        signupBtn.setEnabled(true);
                                        Toast.makeText(getApplicationContext(), "Account Creation Failed because" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                }
            }
        });
    }

    private boolean checkInput()
    {
        boolean check=true;
        if(nameSign.getText().toString().isEmpty())
        {
            nameSign.setError("Name cannot be empty");
            check= false;
        }
        if(emailSign.getText().toString().isEmpty())
        {
            nameSign.setError("Email cannot be empty");
            check= false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailSign.getText().toString().trim()).matches())
        {
            emailSign.setError("Invalid Email Format");
            check = false;
        }
        if(phoneSign.getText().toString().isEmpty())
        {
            phoneSign.setError("Phone Number cannot be empty");
            check= false;
        }
        else if(!Patterns.PHONE.matcher(phoneSign.getText().toString().trim()).matches())
        {
            phoneSign.setError("Invalid Phone Number Format");
            check= false;
        }
        if(passSign.getText().toString().isEmpty())
        {
            passSign.setError("Password cannot be empty");
            check= false;
        }
        else if(passSign.getText().toString().trim().length()<8)
        {
            passSign.setError("Minimum Password Length is 8");
            check= false;
        }
        if(cnfpassSign.getText().toString().isEmpty())
        {
            cnfpassSign.setError("Confirm Password cannot be empty");
            check= false;
        }
        else if(!cnfpassSign.getText().toString().trim().equals(passSign.getText().toString().trim())){
            cnfpassSign.setError("Confirm Password does not matches Password");
            check= false;
        }
        return check;
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