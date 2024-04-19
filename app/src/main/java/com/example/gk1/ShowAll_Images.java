package com.example.gk1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ShowAll_Images extends AppCompatActivity {
    private ListView listView;
    private ImageListAdapter adapter;
    private List<ImageModel> imageList;

    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    boolean isZooming=false;
    boolean isMoving=false;

    private float lastTouchX;
    private float lastTouchY;
    private float imageX;
    private float imageY;
    private int threshold = 1;
    private int moveCount = 0;
    private int maxMoveCount=1;

    private Handler handler = new Handler();
    FirebaseStorage storage;
    StorageReference imagesRef;
    private Runnable checkFingerMovementRunnable = new Runnable() {
        @Override
        public void run() {
            // Xử lý khi ngón tay không di chuyển sau 2 giây
            Toast.makeText(ShowAll_Images.this, "Ngón tay không di chuyển!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_all_images);

        listView = findViewById(R.id.list);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button toupload=findViewById(R.id.toUpload);
        toupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowAll_Images.this, uploadFile.class);
                startActivity(intent);
            }
        });
        storage= FirebaseStorage.getInstance();
        imagesRef = storage.getReference().child("images");
        imageList = new ArrayList<>();
        imagesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Log.d("Tag", "Đường dẫn đầy đủ của tệp tin: " + imageUrl);
                            imageList.add(new ImageModel(imageUrl));
                            adapter = new ImageListAdapter(ShowAll_Images.this, imageList);
                            listView.setAdapter(adapter);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xử lý lỗi nếu không thể lấy URL công khai
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý lỗi nếu không thể lấy danh sách tệp tin
            }
        });







        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPosition(position);
                ImageModel selectedImage = imageList.get(position);
                String selectedImageUrl = selectedImage.getImageUrl();

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowAll_Images.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_image_view, null);
                ImageView imageView = dialogView.findViewById(R.id.imageViewDialog);
                Picasso.get().load(selectedImageUrl).into(imageView);

                enablePinchToZoom(imageView);

                builder.setView(dialogView);
                builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Picasso.get()
                                .load(selectedImageUrl)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        // Lưu ảnh vào bộ nhớ
                                        Utls utls=new Utls();
                                        utls.saveImageToStorage(bitmap,"image"+position+".jpg");
                                        Toast.makeText(ShowAll_Images.this, "Downloaded!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        // Xử lý lỗi tải xuống ảnh
                                        e.printStackTrace();

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        // Chuẩn bị trước khi tải xuống ảnh
                                    }
                                });
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(window.getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                    window.setAttributes(layoutParams);
                }



                dialog.show();
            }
        });



    }

    @SuppressLint("ClickableViewAccessibility")
    public void enablePinchToZoom(ImageView imageView) {

        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(imageView.getContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    float scaleFactor = 1.0f;

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scaleFactor *= detector.getScaleFactor();
                        scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
                        imageView.setScaleX(scaleFactor);
                        imageView.setScaleY(scaleFactor);
                        return true;
                    }
                });

        imageView.setOnTouchListener((v, event) -> {

            int pointerCount = event.getPointerCount();
            if (pointerCount == 1 && !isZooming) {

                float currentTouchX = event.getX();
                float currentTouchY = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchX = currentTouchX;
                        lastTouchY = currentTouchY;

                        break;
                    case MotionEvent.ACTION_MOVE:

                        float deltaX = currentTouchX - lastTouchX;
                        float deltaY = currentTouchY - lastTouchY;

                        imageX += deltaX;
                        imageY += deltaY;

                        imageView.setTranslationX(imageX);
                        imageView.setTranslationY(imageY);


                        lastTouchX = currentTouchX;
                        lastTouchY = currentTouchY;
                        break;


                }

//                Toast.makeText(this, "Hello, World!", Toast.LENGTH_SHORT).show();


            } else if (pointerCount == 2) {
                isZooming=true;
                scaleGestureDetector.onTouchEvent(event);

            } else if (event.getAction() == MotionEvent.ACTION_UP ) {

                isZooming = false;
            }


            return true;
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    public void touchToMove(ImageView imageView){
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float currentTouchX = event.getX();
                float currentTouchY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchX = currentTouchX;
                        lastTouchY = currentTouchY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = currentTouchX - lastTouchX;
                        float deltaY = currentTouchY - lastTouchY;
                        imageX += deltaX;
                        imageY += deltaY;

                        imageView.setTranslationX(imageX);
                        imageView.setTranslationY(imageY);

                        lastTouchX = currentTouchX;
                        lastTouchY = currentTouchY;
                        break;
                }
                return false;
            }
        });
    }






}