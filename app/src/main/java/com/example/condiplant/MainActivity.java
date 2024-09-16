package com.example.condiplant;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.Manifest;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.condiplant.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private ImageButton btnCapture, btnUpload, btnSeeDiseases, btnGuide;
    private Bitmap bitmap;
    private File tempImageFile;


    // Used after successful image cropping. Prepares the image for application usage.
    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            bitmap = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
            tempImageFile = createTempImageFile(bitmap);
            Intent displayIntent = new Intent(MainActivity.this, DisplayImageActivity.class);
            displayIntent.putExtra("image", tempImageFile.getAbsolutePath());
            startActivity(displayIntent);
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home){
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.about_us){
                replaceFragment(new AboutUsFragment());
            } else{
                replaceFragment(new HelpFragment());
            }

            return true;
        });


        //Permission
        getPermission();


    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    /**
     * Launches image cropper for the input image (Uri)
     */
    private void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.imageSourceIncludeCamera = true;
        cropImageOptions.fixAspectRatio = true;
        cropImageOptions.aspectRatioX = 1;
        cropImageOptions.aspectRatioY = 1;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        int captureRequestCode = 10;
        int uploadRequestCode = 12;
        //Image Capture
        if(requestCode == captureRequestCode){
            if (data != null){
                Uri uri = data.getData();
                launchImageCropper(uri);
            }
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

    /**
     * Requests camera permission if the devices has not granted its use.
     */
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