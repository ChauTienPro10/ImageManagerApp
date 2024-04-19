package com.example.gk1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class ImageListAdapter  extends ArrayAdapter<ImageModel> {
    private Context context;
    private List<ImageModel> imageList;
    private int selectedPosition = -1;

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public ImageListAdapter(Context context, List<ImageModel> imageList) {
        super(context, 0, imageList);
        this.context = context;
        this.imageList = imageList;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageViewFlag);
        TextView description = convertView.findViewById(R.id.textViewDesc);
        ImageModel imageModel = imageList.get(position);
        String imageUrl = imageModel.getImageUrl();
        description.setText("this is image : " +position);
        TextView download=convertView.findViewById(R.id.download);
        Picasso.get().load(imageUrl).into(imageView);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });




        if (selectedPosition == position) {
            // Nếu vị trí hiện tại là vị trí được chọn, cập nhật giao diện người dùng tương ứng
            // Ví dụ: thay đổi màu nền hoặc hiển thị một biểu tượng được chọn
            convertView.setBackgroundColor(context.getResources().getColor(R.color.black));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}
