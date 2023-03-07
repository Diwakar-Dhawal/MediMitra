package com.example.medi_mitra_v1.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.medi_mitra_v1.Fragments.HomeFragment;
import com.example.medi_mitra_v1.Fragments.SharedPdfFragment;
import com.example.medi_mitra_v1.Fragments.UploadPdfFragment;
import com.example.medi_mitra_v1.Fragments.ViewListFragment;
import com.example.medi_mitra_v1.Login_Signup.LoginActivity;
import com.example.medi_mitra_v1.R;
import com.example.medi_mitra_v1.Utility.NetworkChangeListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;


public class ClientMainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth firebaseAuth;
    public BottomNavigationView bottomNavigationView;
    ShowcaseView ShowCase;
    ImageView logoutIV,profileIv;
    ViewListFragment viewlistFragment = new ViewListFragment();
    UploadPdfFragment uploadPdfFragment = new UploadPdfFragment();
    SharedPdfFragment sharedPdfFragment = new SharedPdfFragment();
    HomeFragment homeFragment = new HomeFragment();
    ProfileSelf profileSelf = new ProfileSelf();
    MenuItem itemHome;
    FloatingActionButton uploadPdf;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        logoutIV = findViewById(R.id.logoutMain);
        profileIv = findViewById(R.id.profileIVMain);
        showcaseDialogTutorial();
        logoutIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,profileSelf).commit();
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        uploadPdf = findViewById(R.id.uploadPdfFloatingBtn);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home_menuBtn);
        uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,uploadPdfFragment).commit();
            }
        });

    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.ViewUpload_menuBtn:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,viewlistFragment).commit();
                return true;
            case R.id.shared_menuBtn:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,sharedPdfFragment).commit();
                return true;
            case R.id.home_menuBtn:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,homeFragment).commit();
                return true;


        }
        return false;
    }
    private void showcaseDialogTutorial(){
        final SharedPreferences tutorialShowcases = getSharedPreferences("showcaseTutorial", MODE_PRIVATE);

        boolean run;

        run = tutorialShowcases.getBoolean("run?", true);

        if(run){//If the buyer already went through the showcases it won't do it again.
            final ViewTarget target6 = new ViewTarget(R.id.logoutMain, this);
            final ViewTarget target5 = new ViewTarget(R.id.profileIVMain, this);
            final ViewTarget target2 = new ViewTarget(R.id.home_menuBtn, this);
            final ViewTarget target1 = new ViewTarget(R.id.uploadPdfFloatingBtn, this);
            final ViewTarget target4 = new ViewTarget(R.id.ViewUpload_menuBtn, this);
            final ViewTarget target3 = new ViewTarget(R.id.shared_menuBtn, this);
            final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            lps.addRule(RelativeLayout.CENTER_IN_PARENT);

            int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
            lps.setMargins(margin, margin, margin, margin);


            ShowCase = new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(target1)
                    .setContentTitle("Upload Documents")
                    .setContentText("Click on Button to Upload you Documents")
                    .singleShot(42)
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .build();
            ShowCase.setButtonText("Next");
            ShowCase.setButtonPosition(lps);



            //When the button is clicked then the switch statement will check the counter and make the new showcase.
            ShowCase.overrideButtonClick(new View.OnClickListener() {
                int count1 = 0;
                @Override
                public void onClick(View v) {
                    count1++;
                    switch (count1) {
                        case 1:
                            ShowCase.setTarget(target2);
                            ShowCase.setContentTitle("Home");
                            ShowCase.setContentText("Click here to visit home.");
                            ShowCase.setButtonText("next");
                            break;

                        case 2:
                            ShowCase.setTarget(target3);
                            ShowCase.setContentTitle("Shared Documents");
                            ShowCase.setContentText("Click here to view Shared documents from clinics.");
                            ShowCase.setButtonText("next");
                            break;

                        case 3:
                            ShowCase.setTarget(target4);
                            ShowCase.setContentTitle("View Documents");
                            ShowCase.setContentText("Click here to view your uploaded documents");
                            ShowCase.setButtonText("next");
                            break;

                        case 4:
                            ShowCase.setTarget(target5);
                            ShowCase.setContentTitle("Profile");
                            ShowCase.setContentText("Click here to edit your profile.");
                            ShowCase.setButtonText("next");
                            break;

                        case 5:
                            ShowCase.setTarget(target6);
                            ShowCase.setContentTitle("Logout");
                            ShowCase.setContentText("Click here to logout.");
                            ShowCase.setButtonText("next");
                            break;


                        case 6:
                            SharedPreferences.Editor tutorialShowcasesEdit = tutorialShowcases.edit();
                            tutorialShowcasesEdit.putBoolean("run?", false);
                            tutorialShowcasesEdit.apply();

                            ShowCase.hide();
                            break;
                    }
                }
            });
        }
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