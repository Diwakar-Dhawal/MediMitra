package com.example.medi_mitra_v1.Fragments;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_mitra_v1.Adapters.PdfAdapter;
import com.example.medi_mitra_v1.Models.Reports;
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


public class ViewListFragment extends Fragment {
    FirebaseStorage storage;
    ImageButton favPdfBtn;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    DatabaseReference database;
    EditText search;
    public PdfAdapter adapter;
    Query query;
    TextView textView;
    ArrayList<Reports> list;
    ProgressDialog dialog;
    boolean state=false,deleted=false;
    public ViewListFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_view_list, container, false);
        Context context = v.getContext();
        swipeRefreshLayout = v.findViewById(R.id.swipeViewItem);

        storage = FirebaseStorage.getInstance();
        recyclerView = v.findViewById(R.id.ViewPdfRV);
        recyclerView.setHasFixedSize(true);
        favPdfBtn = v.findViewById(R.id.PdfFavBtn);
        textView= v.findViewById(R.id.noItem);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading..");
        dialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference("Users").child(""+uid).child("Uploads");
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        query=  database.orderByChild("pdfName");
        list = new ArrayList<>();
        adapter = new PdfAdapter(context,list);
        recyclerView.setAdapter(adapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Reports reports = dataSnapshot.getValue(Reports.class);
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
        search = v.findViewById(R.id.PdfsearchEt);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapter.getFilter().filter(s);
                }catch (Exception e)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        favPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state)
                {
                    CharSequence cs = "";
                    adapter.FavoritesOff().filter(cs);
                    state=false;
                }
                else{
                    CharSequence cs = "";
                    adapter.FavoritesOn().filter(cs);
                    state=true;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return v;
    }

    public void deleteItem(String timestamp,String url,Context context)
    {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Users").child(""+uid).child("Uploads").child(timestamp+"");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }

        });

    }


}