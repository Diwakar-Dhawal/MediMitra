package com.example.medi_mitra_v1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medi_mitra_v1.Client.DisplayPdf;
import com.example.medi_mitra_v1.Filters.FilterPdf;
import com.example.medi_mitra_v1.R;
import com.example.medi_mitra_v1.Models.Reports;
import com.example.medi_mitra_v1.Models.SharedReports;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DropboxAdapter extends RecyclerView.Adapter<DropboxAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<Reports> list,filterList;
    private FilterPdf filter;
    ArrayList<SharedReports> list1;
    public DropboxAdapter(Context context, ArrayList<SharedReports> list1,int s) {
        this.context = context;
        this.list1 = list1;
    }
    public DropboxAdapter(Context context, ArrayList<Reports> list) {
        this.context = context;
        this.list = list;
        this.filterList=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.dropbox_items_cardview,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Reports report  = list.get(position);
        holder.name.setText(report.getPdfName());
        holder.hos.setText(report.getHosName());
        holder.doc.setText(report.getDocName());
        String timestamp = report.getTimestamp();
        String url = report.getFileUrl();
        String uid = FirebaseAuth.getInstance().getUid();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Uploads").child(timestamp).child("indropbox").setValue("false");
            }
        });
        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte  = new Intent(context, DisplayPdf.class);
                inte.putExtra("URL",url);
                context.startActivity(inte);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public Filter FavoritesOn(){return filter;
    }
    public Filter FavoritesOff(){
        return filter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,doc,hos;
        ImageButton delete;
        RelativeLayout rl;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameDrop);
            doc = itemView.findViewById(R.id.DocDrop);
            hos = itemView.findViewById(R.id.hosDrop);
            delete = itemView.findViewById(R.id.delete_Drop);
            rl = itemView.findViewById(R.id.Drop_item);
        }
    }

}

