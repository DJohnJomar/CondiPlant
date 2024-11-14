package com.example.condiplant;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeFragment extends Fragment {

    private ImageButton btnCapture, btnUpload, btnSeeDiseases;
    private Bitmap bitmap;
    private File tempImageFile;
    private Toast currentToast;
    private SharedPreferences preferences;
    private String selectedButton = "";

    // Used after successful image cropping. Prepares the image for application usage.
    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            bitmap = BitmapFactory.decodeFile(result.getUriFilePath(requireContext(), true));
            tempImageFile = createTempImageFile(bitmap);
            Intent displayIntent = new Intent(requireActivity(), DisplayImageActivityInitial.class);
            displayIntent.putExtra("image", tempImageFile.getAbsolutePath());
            startActivity(displayIntent);
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnCapture = view.findViewById(R.id.btnCapture);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnSeeDiseases = view.findViewById(R.id.btnSeeDiseases);

        preferences = requireContext().getSharedPreferences("HomePrefs", MODE_PRIVATE);

        //Permission
        getPermission();

        btnCapture.setOnClickListener(v -> {
            boolean isFirstPrediction = preferences.getBoolean("isFirstPrediction", true);
            if (isFirstPrediction) {
                selectedButton = "capture";
                showDialogMessage();
            } else {
                if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getActivity(), CameraX.class);
                    startActivityForResult(intent, 12);
                } else {
                    getPermission();
                    if (currentToast != null) {
                        currentToast.cancel();
                    }

                    // Show a new short duration Toast
                    currentToast = Toast.makeText(requireContext(), "Camera permission denied. Please grant permission to use the camera function", Toast.LENGTH_SHORT);
                    currentToast.show();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        currentToast = null; // Clear the reference after a delay
                    }, 2000);
                }
            }
        });

        btnUpload.setOnClickListener(v -> {
            boolean isFirstPrediction = preferences.getBoolean("isFirstPrediction", true);
            if (isFirstPrediction) {
                selectedButton = "upload";
                showDialogMessage();
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        btnSeeDiseases.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListOfRootcrops.class);
            startActivity(intent);
        });

        return view;
    }

    private void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.imageSourceIncludeCamera = false;
        cropImageOptions.fixAspectRatio = true;
        cropImageOptions.aspectRatioX = 1;
        cropImageOptions.aspectRatioY = 1;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        int uploadRequestCode = 10;
        int captureRequestCode = 12;


        // Image Capture
        if (requestCode == uploadRequestCode && resultCode == MainActivity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                launchImageCropper(uri);
                Log.e("Upload", "I Upload");
            }
        } else if (requestCode == captureRequestCode && resultCode == MainActivity.RESULT_OK){
            if (data != null) {
                Uri uri = data.getData();
                launchImageCropper(uri);
                Log.e("Capture", "I Capture");
            }
        } else {
            Log.e("HomeFragment", "No valid condition matched in onActivityResult()");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Creates a temporary file saved on the user's device to be used to pass data.
    //It is removed once it has been used up
    private File createTempImageFile(Bitmap bitmap) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp_image", ".png", requireContext().getCacheDir());
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Using png format to save image
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * Requests camera permission if the device has not granted its use.
     */
    void getPermission() {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 11);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int permissionRequestCode = 11;
        if (requestCode == permissionRequestCode) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                getPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showDialogMessage() {
        // Inflate the custom dialog layout
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_box, null);
        Button btnUnderstand = view.findViewById(R.id.btnUnderstand);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        // Set the button click listener
        btnUnderstand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putBoolean("isFirstPrediction", false).apply();
                alertDialog.dismiss();

                if ("capture".equals(selectedButton)) {
                    if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(getActivity(), CameraX.class);
                        startActivityForResult(intent, 12);
                    } else {
                        getPermission();
                    }
                } else if ("upload".equals(selectedButton)) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 10);
                }
            }
        });

        // Customize the dialog's background
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        // Show the dialog
        alertDialog.show();
    }
}