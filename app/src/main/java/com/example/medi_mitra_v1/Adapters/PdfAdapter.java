package com.example.medi_mitra_v1.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.medi_mitra_v1.Filters.FilterPdf;
import com.example.medi_mitra_v1.Client.QRActivity;
import com.example.medi_mitra_v1.R;
import com.example.medi_mitra_v1.Models.Reports;
import com.example.medi_mitra_v1.Fragments.ViewListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.MyViewHolder> implements Filterable {
    Context context;
    public ArrayList<Reports> list;
    ArrayList<Reports> filterList;
    private Calendar calendar;
    private String date,time;
    private FilterPdf filter;
    FirebaseDatabase database;

    public PdfAdapter(Context context, ArrayList<Reports> list){
        this.context = context;
        this.list = list;
        this.filterList=list;

    }

    @NonNull

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.itemscardview,parent,false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Reports report  = list.get(position);
        holder.type.setText(report.getPdfName());
        holder.docname.setText(report.getDocName());
        holder.hname.setText(report.getHosName());
        String s = report.getFileUrl();
        calendar = Calendar.getInstance();
        String timestamp =report.getTimestamp();
        date = timestamp;
        time = timestamp;
        final String isfav = report.getIsfav();
        final String indropbox = report.getIndropbox();
        database = FirebaseDatabase.getInstance();
        DatabaseReference dbref = database.getReference();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timestamp));
            date = DateFormat.format("dd/MM/yyyy", cal).toString();
            time = DateFormat.format("HH:MM", cal).toString();

        }
        catch (Exception e)
        {
            date = "DD/MM/YYYY";
            time = "HH:MM";
        }
        holder.date.setText(date);
        holder.time.setText(time);

        if (indropbox.equals("true")) {
            holder.dropbox.setImageResource(R.drawable.ic_dropbox_added );
        }
        else
        {
            holder.dropbox.setImageResource(R.drawable.ic_add_dropbox);
        }

        if (isfav.equals("true")) {
            holder.fav.setImageResource(R.drawable.fav_fill_icon );
        }
        else
        {
            holder.fav.setImageResource(R.drawable.favorite_empty_icon);
        }
        try {
            holder.fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isfav.equals("true")) {
                        holder.fav.setImageResource(R.drawable.favorite_empty_icon);
                        dbref.child("Users").child(uid).child("Uploads").child(timestamp).child("isfav").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        holder.fav.setImageResource(R.drawable.fav_fill_icon);
                        dbref.child("Users").child(uid).child("Uploads").child(timestamp).child("isfav").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
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

        try {
            holder.dropbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (indropbox.equals("true")) {
                        holder.dropbox.setImageResource(R.drawable.favorite_empty_icon);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Dropbox").child(timestamp).removeValue();
                        dbref.child("Users").child(uid).child("Uploads").child(timestamp).child("indropbox").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Removed from Dropbox", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Task Failed", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } else if (indropbox.equals("false")) {
                        holder.dropbox.setImageResource(R.drawable.fav_fill_icon);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Dropbox").child(timestamp).setValue("Added");
                        dbref.child("Users").child(uid).child("Uploads").child(timestamp).child("indropbox").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Added to Dropbox", Toast.LENGTH_SHORT).show();
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
                ViewListFragment viewPdf = new ViewListFragment();
                viewPdf.deleteItem(timestamp,s,context);
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
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                View mView = LayoutInflater.from(context).inflate(R.layout.custom_dialog,null);
                final EditText name = (EditText)mView.findViewById(R.id.editName_ET);
                final EditText doc = (EditText)mView.findViewById(R.id.editDoc_ET);
                final EditText hos = (EditText)mView.findViewById(R.id.editHos_ET);
                Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
                Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btn_okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(name.getText()))
                            name.setError("Report Type Cannot Be Empty");
                        else
                        {
                            Toast.makeText(context, "Edit Successful", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Uploads").child(timestamp).child("pdfName").setValue(""+name.getText());
                            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Uploads").child(timestamp).child("docName").setValue(""+doc.getText());
                            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Uploads").child(timestamp).child("hosName").setValue(""+hos.getText());
                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter = new FilterPdf(filterList,this,"Search");
        }
        return filter;
    }

    public Filter FavoritesOn(){
        filter = new FilterPdf(filterList,this,"FavoritesOn");
        return filter;
    }

    public Filter FavoritesOff(){
            filter = new FilterPdf(filterList,this,"FavoritesOff");
        return filter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView type,hname,docname,date,time;
        ImageButton fav,generate,download,delete,dropbox,edit;
        ImageView pdf;
        RelativeLayout rl;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.titleTv);
            docname = itemView.findViewById(R.id.docTv);
            hname = itemView.findViewById(R.id.HospitalTv);
            rl = itemView.findViewById(R.id.pdfItem);
            fav = itemView.findViewById(R.id.FavIb);
            generate = itemView.findViewById(R.id.QrIB);
            download = itemView.findViewById(R.id.downloadBtn);
            date  = itemView.findViewById(R.id.dateTv);
            delete = itemView.findViewById(R.id.deleteBtn);
            time = itemView.findViewById(R.id.timeTv);
            dropbox = itemView.findViewById(R.id.dropboxIBtn);
            edit = itemView.findViewById(R.id.edit_pdfIB);
        }
    }


}
