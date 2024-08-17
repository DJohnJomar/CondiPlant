package com.example.condiplant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.Manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnCapture, btnUpload, btnSeeDiseases;
    private Bitmap bitmap;
    private File tempImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = findViewById(R.id.btnCapture);
        btnUpload = findViewById(R.id.btnUpload);
        btnSeeDiseases = findViewById(R.id.btnSeeDiseases);

        //Permission
        getPermission();
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        int captureRequestCode = 10;
        int uploadRequestCode = 12;
        //Image Capture
        if(requestCode == captureRequestCode){
            if (data != null){
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                    if (bitmap.getHeight() == bitmap.getWidth()) { // check if image is square
                    tempImageFile = createTempImageFile(bitmap);

                    // check first if the image is within restriction

                    Intent displayIntent = new Intent(MainActivity.this, DisplayImageActivity.class);
                    displayIntent.putExtra("image", tempImageFile.getAbsolutePath());
                    startActivity(displayIntent);
                    //imageView.setImageBitmap(bitmap);
//                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else if(requestCode == uploadRequestCode && resultCode == RESULT_OK && data != null){//Upload Image
            bitmap = (Bitmap) data.getExtras().get("data");
            tempImageFile = createTempImageFile(bitmap);

            // check first if the image is within restriction

            //imageView.setImageBitmap(bitmap);
            Intent displayIntent = new Intent(MainActivity.this, DisplayImageActivity.class);
            displayIntent.putExtra("image", tempImageFile.getAbsolutePath());
            startActivity(displayIntent);
        }else{
            // Log a message if no condition matches
            Log.e("MainActivity", "No valid condition matched in onActivityResult()");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createTempImageFile(Bitmap bitmap){
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp_image", ".png", getCacheDir());
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);//Using png format to save image
            out.flush();
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return tempFile;
    }
    void getPermission(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String [] {Manifest.permission.CAMERA}, 11);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int permissionRequestCode = 11;
        if(requestCode == permissionRequestCode){
            if(grantResults.length>0){
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}