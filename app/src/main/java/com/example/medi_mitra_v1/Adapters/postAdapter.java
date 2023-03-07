package com.example.medi_mitra_v1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medi_mitra_v1.Filters.FilterPdf;
import com.example.medi_mitra_v1.Models.Posts;
import com.example.medi_mitra_v1.Client.ProfileActivity;
import com.example.medi_mitra_v1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class postAdapter extends RecyclerView.Adapter<postAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<Posts> list,filterList;
    private Calendar calendar;
    private String date;
    private FilterPdf filter;
    FirebaseDatabase database;

    boolean likedPost=false;

    int likes=0;

    public postAdapter(Context context, ArrayList<Posts> list) {
        this.context = context;
        this.list = list;
        this.filterList=list;
    }

    @NonNull

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.postcardview,parent,false);
        return new MyViewHolder(v);
        }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Posts report  = list.get(position);
        holder.uploaderName.setText(report.getUploaderName());
        holder.caption.setText(report.getCaption());
        calendar = Calendar.getInstance();
        String url = report.getPostImg();
        String timestamp =report.getTimestamp();
        date = timestamp;
        String Puid = report.getUid();
        likes = Integer.parseInt(report.getLike());
        database = FirebaseDatabase.getInstance();
        DatabaseReference dbref = database.getReference();
        holder.likeCount.setText(""+likes);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timestamp));
            date = DateFormat.format("dd/MM/yyyy", cal).toString();
            FirebaseDatabase.getInstance().getReference().child("Likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(timestamp).hasChild(uid))
                    {
                        holder.likeImg.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24);
                        likedPost = true;
                        holder.like.setText("Liked");
                    }
                    else
                    {
                        holder.likeImg.setBackgroundResource(R.drawable.ic_baseline_thumb_up_outline);
                        likedPost=false;
                        holder.like.setText("Like");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e)
        {
        date = "DD/MM/YYYY";
        }
        try {
            Picasso.get().load(url).into(holder.post);
        }
        catch (Exception e)
        {
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.time.setText(date);

        holder.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likedPost) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(timestamp).child(uid).removeValue();
                    --likes;
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(timestamp).child("like").setValue(""+likes);
                    likedPost=false;
                    holder.likeImg.setBackgroundResource(R.drawable.ic_baseline_thumb_up_outline);
                    holder.like.setText("Like");

                }
                else
                {
                    ++likes;
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(timestamp).child("like").setValue(""+likes);
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(timestamp).child(uid).setValue("Liked");
                    likedPost=true;
                    holder.likeImg.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24);
                    holder.like.setText("Liked");
                }
            }
        });
        holder.uploaderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("uid",Puid);
                context.startActivity(i);

            }
        });
        holder.uploaderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context,ProfileActivity.class);
                i.putExtra("uid",Puid);
                context.startActivity(i);

            }
        });
        holder.shareRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "This is a post from Medi-Mitra "+url;
                String sub = "Medi-Mitra";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                context.startActivity(Intent.createChooser(myIntent, "Share Using"));

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
    TextView caption,uploaderName,like,time,likeCount;
    ImageView uploaderImg,post,likeImg;
    RelativeLayout likeRl,shareRl;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        time = itemView.findViewById(R.id.postTimeTV);
        caption=itemView.findViewById(R.id.postCaptionTV);
        uploaderName = itemView.findViewById(R.id.UploaderNameTV);
        like = itemView.findViewById(R.id.postLikeTV);
        uploaderImg = itemView.findViewById(R.id.UploaderProfileIV);
        post = itemView.findViewById(R.id.postImageIV);
        likeImg = itemView.findViewById(R.id.postLikeIv);
        likeRl = itemView.findViewById(R.id.postLikeRL);
        shareRl = itemView.findViewById(R.id.postShareRl);
        likeCount = itemView.findViewById(R.id.LikeCounterTV);

    }
}

}