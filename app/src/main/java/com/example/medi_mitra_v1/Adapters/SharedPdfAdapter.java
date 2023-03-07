package com.example.medi_mitra_v1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medi_mitra_v1.Client.DisplayPdf;
import com.example.medi_mitra_v1.Filters.FilterSharedPdf;
import com.example.medi_mitra_v1.Client.QRActivity;
import com.example.medi_mitra_v1.R;
import com.example.medi_mitra_v1.Fragments.SharedPdfFragment;
import com.example.medi_mitra_v1.Models.SharedReports;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SharedPdfAdapter extends RecyclerView.Adapter<SharedPdfAdapter.MyViewHolder> implements Filterable {
    Context context;
    public ArrayList<SharedReports> list;
    ArrayList<SharedReports> filterList;
    private Calendar calendar;
    private String date;
    private FilterSharedPdf filter;
    FirebaseDatabase database;

    public SharedPdfAdapter(Context context, ArrayList<SharedReports> list) {
        this.context = context;
        this.list = list;
        this.filterList=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.shareditemscardview,parent,false);
        return new MyViewHolder(v);
    }

     @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SharedReports report  = list.get(position);
        holder.type.setText(report.getSharedpdfName());
        holder.docname.setText(report.getShareddocName());
        holder.hname.setText(report.getSharedhosName());
        String s = report.getSharedfileUrl();
        calendar = Calendar.getInstance();
        String timestamp =report.getSharedtimestamp();
        date = timestamp;
        final String isfav = report.getSharedisfav();
        database = FirebaseDatabase.getInstance();
        DatabaseReference dbref = database.getReference();
         final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timestamp));
            date = DateFormat.format("dd/MM/yyyy", cal).toString();

        }
        catch (Exception e)
        {
            date = "DD/MM/YYYY";
        }
        holder.datetime.setText(date);
        if (isfav.equals("true")) {
            holder.fav.setBackgroundResource(R.drawable.fav_fill_icon );
        }
        else
        {
            holder.fav.setBackgroundResource(R.drawable.favorite_empty_icon);
        }
         try {

             holder.fav.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (isfav.equals("true")) {
                         holder.fav.setBackgroundResource(R.drawable.favorite_empty_icon);
                         dbref.child("Users").child(uid).child("Shared").child(timestamp).child("sharedisfav").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()) {
                                     Toast.makeText(context, "Removed from Favorite", Toast.LENGTH_SHORT).show();
                                 } else {
                                     Toast.makeText(context, "Task Failed", Toast.LENGTH_SHORT).show();

                                 }

                             }
                         });
                     } else if (isfav.equals("false")) {
                         holder.fav.setBackgroundResource(R.drawable.fav_fill_icon);
                         dbref.child("Users").child(uid).child("Shared").child(timestamp).child("sharedisfav").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()) {
                                     Toast.makeText(context, "Added to Favorite", Toast.LENGTH_SHORT).show();
                                 } else {
                                     Toast.makeText(context, "Task Failed", Toast.LENGTH_SHORT).show();

                                 }
                             }

                         });
                     }
                 }
             });
         }
         catch(Exception e)
         {
             Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
         }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPdfFragment sharedPdf = new SharedPdfFragment();
                sharedPdf.deleteItem(timestamp,context);

            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(s),"application/pdf");
                context.startActivity(intent);

            }
        });
        holder.generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inte  = new Intent(context, QRActivity.class);
                inte.putExtra("URL",s);
                context.startActivity(inte);

            }
        });
        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte  = new Intent(context, DisplayPdf.class);
                inte.putExtra("URL",s);
                context.startActivity(inte);
            }
        });
        holder.name.setText(report.getShareduid());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterSharedPdf(filterList,this,"Search");
        }
        return filter;
    }

    public Filter FavoritesOn(){

        filter = new FilterSharedPdf(filterList,this,"FavoritesOn");

        return filter;
    }
    public Filter FavoritesOff(){

        filter = new FilterSharedPdf(filterList,this,"FavoritesOff");

        return filter;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView type,hname,docname,datetime,name;
        ImageButton fav,generate,download,delete;
        ImageView pdf;
        RelativeLayout rl;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.SharedIDTV);
            type = itemView.findViewById(R.id.SharedtitleTv);
            docname = itemView.findViewById(R.id.ShareddocTv);
            hname = itemView.findViewById(R.id.SharedHospitalTv);
            rl = itemView.findViewById(R.id.SharedpdfRl);
            fav = itemView.findViewById(R.id.SharedFavIb);
            generate = itemView.findViewById(R.id.SharedQrIB);
            download = itemView.findViewById(R.id.ShareddownloadBtn);
            datetime  = itemView.findViewById(R.id.ShareddateTv);
            delete = itemView.findViewById(R.id.ShareddeleteBtn);




        }
    }

}
