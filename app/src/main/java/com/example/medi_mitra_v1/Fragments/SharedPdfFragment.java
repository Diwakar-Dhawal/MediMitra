package com.example.medi_mitra_v1.Fragments;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_mitra_v1.Adapters.SharedPdfAdapter;
import com.example.medi_mitra_v1.Models.SharedReports;
import com.example.medi_mitra_v1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SharedPdfFragment extends Fragment {
    EditText SharedsearchEt;
    Button SharedFav;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    DatabaseReference database;
    public SharedPdfAdapter Sadapter;
    Query query;
    TextView textView;
    ArrayList<SharedReports> list;
    ProgressDialog dialog;
    boolean state=false;


    public SharedPdfFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shared_pdf, container, false);
        // Inflate the layout for this fragment
        try {
            Context context = v.getContext();
            recyclerView = v.findViewById(R.id.SharedViewPdfRV);
            recyclerView.setHasFixedSize(true);
            textView = v.findViewById(R.id.SharednoItem);
            SharedsearchEt = v.findViewById(R.id.SharedPdfsearchEt);
            SharedFav = v.findViewById(R.id.SharedPdfFavButton);
            swipeRefreshLayout = v.findViewById(R.id.SharedswipeViewItem);

            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading..");
            dialog.show();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            database = FirebaseDatabase.getInstance().getReference("Users").child(""+uid).child("Shared");
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            query = database.orderByChild("SharedpdfName");
            list = new ArrayList<>();
            Sadapter = new SharedPdfAdapter(context, list);
            recyclerView.setAdapter(Sadapter);

            SharedsearchEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try{
                        Sadapter.getFilter().filter(s);
                    }catch (Exception e)
                    {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            SharedFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {


                        if (state) {
                            CharSequence cs = "";
                            SharedFav.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                            SharedFav.setTextColor(getResources().getColor(R.color.teal_700));
                            Sadapter.FavoritesOff().filter(cs);
                            state = false;
                        } else {
                            CharSequence cs = "";
                            SharedFav.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_700)));
                            SharedFav.setTextColor(getResources().getColor(R.color.white));
                            Sadapter.FavoritesOn().filter(cs);
                            state = true;
                        }
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        SharedReports reports = dataSnapshot.getValue(SharedReports.class);
                        list.add(0,reports);

                    }
                    Sadapter.notifyDataSetChanged();
                    if (list.size() < 1) {
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        textView.setVisibility(View.INVISIBLE);
                    }
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.hide();
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Sadapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            return v;
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }




        return v;
    }
    public void deleteItem(String timestamp,Context context)
    {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Users").child(""+uid).child("Shared").child(timestamp+"");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }

        });

    }


}