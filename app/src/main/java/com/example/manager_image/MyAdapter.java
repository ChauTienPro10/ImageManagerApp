package com.example.manager_image;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<DataClass> dataList;
    private Context context;



    public MyAdapter(Context context, ArrayList<DataClass> dataList) {

        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(dataList.get(position).getImageURL()).into(holder.recyclerImage);
        holder.recyclerCaption.setText(dataList.get(position).getCaption());
        holder.mdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = dataList.get(position).getImageURL();
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Downloading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Picasso.get()
                        .load(imageUrl)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                String rdom=generateRandomString();
                                boolean succ=
                                dataList.get(position).saveBitmapToStorage(bitmap,"image"+position+rdom+".jpg");
                                if(succ){
                                    Toast.makeText(context, "Downloaded!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
                                }

                                progressDialog.dismiss();
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                // Xử lý trước khi bắt đầu tải xuống
                            }
                        });

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerCaption;

        AppCompatButton mdownload;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaption);
            mdownload=itemView.findViewById(R.id.downlButton);



        }
    }

    public String generateRandomString() {
        String characters = "abcdefghijklmnopqrstuvwxyz";

        String randomString = "";

        // Tạo chuỗi gồm 10 chữ cái ngẫu nhiên
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            randomString += characters.charAt(index);
        }


        return randomString;
    }
}