package com.example.medi_mitra_v1.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.medi_mitra_v1.Adapters.postAdapter;
import com.example.medi_mitra_v1.Models.Posts;
import com.example.medi_mitra_v1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FirebaseStorage storage;

    Button favPdfBtn;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    DatabaseReference database;
    EditText search;
    public postAdapter adapter;
    Query query;
    TextView textView;
    ArrayList<Posts> list;
    ProgressDialog dialog;
    boolean state=false,deleted=false;

    public HomeFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_home, container, false);

        Context context = v.getContext();
        storage = FirebaseStorage.getInstance();
        recyclerView = v.findViewById(R.id.PostsRV);
        swipeRefreshLayout = v.findViewById(R.id.swipePost);
        recyclerView.setHasFixedSize(true);
        textView= v.findViewById(R.id.postnoItem);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading..");
        dialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference("Posts");
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        query=  database.orderByChild("timestamp");
        list = new ArrayList<>();
        adapter = new postAdapter(context,list);
        recyclerView.setAdapter(adapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Posts reports = dataSnapshot.getValue(Posts.class);

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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return v;
    }
}