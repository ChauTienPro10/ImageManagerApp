package com.example.manager_image;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DataClass {
    private String imageURL, caption;

    public DataClass(){

    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public DataClass(String imageURL, String caption) {
        this.imageURL = imageURL;
        this.caption = caption;
    }

    public boolean saveBitmapToStorage(Bitmap bitmap, String fileName) {
        boolean success = false;

        // Tạo thư mục để lưu trữ ảnh
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "MyApp");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Tạo file lưu trữ ảnh
        File imageFile = new File(directory, fileName);

        // Lưu ảnh vào bộ nhớ
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return success;
    }

}