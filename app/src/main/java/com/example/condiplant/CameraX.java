package com.example.condiplant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.condiplant.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraX extends AppCompatActivity {
    private Timer timer;
    private ImageButton capture, toggleFlash;
    private PreviewView previewView;
    private BoundingBoxView boundingBoxView;
    private Camera camera;
    private int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                startCamera(cameraFacing);
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerax_frame);

        previewView = findViewById(R.id.cameraPreview);
        capture = findViewById(R.id.capture);
        toggleFlash = findViewById(R.id.toggleFlash);
        boundingBoxView = findViewById(R.id.bounding_box_view);
        capture.setVisibility(View.INVISIBLE);

        if (ContextCompat.checkSelfPermission(CameraX.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (capture.getVisibility() == View.INVISIBLE) {
                            Toast.makeText(CameraX.this, "Move Around the target object to focus", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 15000);
    }

    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);
        listenableFuture.addListener(() -> {
            try {

                ProcessCameraProvider cameraProvider =  listenableFuture.get();

                Preview preview = new Preview.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).setTargetResolution(new Size(1080, 1920)).build();



                cameraProvider.unbindAll();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), image -> {
                    int rotationDegrees = image.getImageInfo().getRotationDegrees();


                    // Convert ImageProxy to Bitmap and detect objects
                    Bitmap bitmap = imageProxyToBitmap(image); // Convert ImageProxy to Bitmap
                    detectObject(bitmap, rotationDegrees);
                    image.close();
                });

                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
                capture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if (ContextCompat.checkSelfPermission(CameraX.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                        }
                        takePicture(imageCapture);
                    }
                });

                toggleFlash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setFlashIcon(camera);
                    }
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture(ImageCapture imageCapture) {
        imageCapture.takePicture(Executors.newCachedThreadPool(), new ImageCapture.OnImageCapturedCallback() {

            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                if (capture.getVisibility() == View.INVISIBLE) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraX.this, "Image Capture Failed. Please hold the camera still...", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    super.onCaptureSuccess(image);
                    timer.cancel();
                    timer.purge();
                    Uri imageUri = saveImageToFile(image);
                    Intent resultIntent = new Intent();
                    resultIntent.setData(imageUri);
                    setResult(MainActivity.RESULT_OK, resultIntent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraX.this, "Image Captured. Preparing image for cropping...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    image.close(); // Always close the ImageProxy
                    finish();
                }
            }

        });
    }

    private void setFlashIcon(Camera camera) {
        if (camera.getCameraInfo().hasFlashUnit()) {
            if (camera.getCameraInfo().getTorchState().getValue() == 0) {
                camera.getCameraControl().enableTorch(true);
                toggleFlash.setImageResource(R.drawable.baseline_flash_off_24);
            } else {
                camera.getCameraControl().enableTorch(false);
                toggleFlash.setImageResource(R.drawable.baseline_flash_on_24);
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CameraX.this, "Flash is not available currently", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void detectObject(Bitmap bitmap, int rotationDegrees) {
        ObjectDetectorOptions options =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                        .build();
        ObjectDetector objectDetector = ObjectDetection.getClient(options);
        InputImage inputImage = InputImage.fromBitmap(bitmap, rotationDegrees);
        objectDetector.process(inputImage)
                .addOnSuccessListener(detectedObjects -> {
                    if (!detectedObjects.isEmpty()) {
                        List<Rect> boundingBoxes = new ArrayList<>();
                        for (DetectedObject detectedObject : detectedObjects) {
                            // Get the bounding box
                            Rect boundingBox = detectedObject.getBoundingBox();
                            boundingBoxes.add(boundingBox);
                        }

                        // Get dimensions of the preview view
                        int previewWidth = previewView.getWidth();
                        int previewHeight = previewView.getHeight();

                        // Get dimensions of the original image
                        int imageWidth = bitmap.getWidth();
                        int imageHeight = bitmap.getHeight();

                        List<Rect> scaledBoundingBoxes = scaleBoundingBoxes(boundingBoxes, previewWidth, previewHeight, imageWidth, imageHeight);

                        // Draw bounding boxes on the Preview
                        drawBoundingBoxes(scaledBoundingBoxes);

                        // Object is recognizable
                        capture.setVisibility(View.VISIBLE);
                    } else {
                        boundingBoxView.clearBoundingBoxes();
                        capture.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    boundingBoxView.clearBoundingBoxes();
                    capture.setVisibility(View.INVISIBLE);
                });
    }


    @OptIn(markerClass = ExperimentalGetImage.class)
    public Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
        if (image == null) {
            return null;
        }

        // Ensure the image format is YUV_420_888
        if (image.getFormat() != ImageFormat.YUV_420_888) {
            throw new IllegalArgumentException("Image format must be YUV_420_888");
        }

        // Create a Bitmap from the ImageProxy
        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();

        // Create a Bitmap and copy the pixel data
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] yBytes = new byte[ySize];
        byte[] uBytes = new byte[uSize];
        byte[] vBytes = new byte[vSize];

        // Copy the Y, U, V data to the byte arrays
        yBuffer.get(yBytes);
        uBuffer.get(uBytes);
        vBuffer.get(vBytes);

        // Fill the bitmap with YUV data
        // Use YUV_420_888 to ARGB_8888 conversion
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int yIndex = j * width + i;
                int y = (yBytes[yIndex] & 0xff);

                // Calculate UV indices
                int uIndex = (j / 2) * (width / 2) + (i / 2);
                int vIndex = (j / 2) * (width / 2) + (i / 2);

                int u = (uBytes[uIndex] & 0xff) - 128;
                int v = (vBytes[vIndex] & 0xff) - 128;

                // Convert YUV to RGB
                int r = y + (int)(1.402 * v);
                int g = y - (int)(0.344136 * u + 0.714136 * v);
                int b = y + (int)(1.772 * u);

                r = Math.min(255, Math.max(0, r));
                g = Math.min(255, Math.max(0, g));
                b = Math.min(255, Math.max(0, b));

                // Set pixel in bitmap
                bitmap.setPixel(i, j, 0xff000000 | (r << 16) | (g << 8) | b);
            }
        }

        // Close the imageProxy
        imageProxy.close();

        return bitmap;
    }

    private void drawBoundingBoxes(List<Rect> boundingBoxes) {
        if (!boundingBoxes.isEmpty()) {
            boundingBoxView.setBoundingBoxes(boundingBoxes);
        } else {
            boundingBoxView.clearBoundingBoxes();
        }
    }

    private float getAspectRatio(int width, int height) {
        return (float) width / (float) height;
    }

    private List<Rect> scaleBoundingBoxes(List<Rect> boundingBoxes, int previewWidth, int previewHeight, int imageWidth, int imageHeight) {

        Log.e("CameraX", previewHeight + ", " + previewWidth + ", " + imageHeight + ", " + imageWidth);
        float widthScale = (float)  previewWidth / imageHeight;
        float heightScale = (float) previewHeight / imageWidth;
        Log.e("CameraX", heightScale + ", " + widthScale);
        List<Rect> scaledBoxes = new ArrayList<>();
        int adjustments = 200;
        for (Rect rect : boundingBoxes) {
            Log.e("Orig Box", "Left: " + rect.left + ", Top: " + rect.top + ", Right: " + rect.right + ", Bottom: " + rect.bottom);
            int left = Math.round(rect.left * widthScale);
            int right = Math.round(rect.right * widthScale);
            int top = Math.round(rect.top * heightScale);
            int bottom = Math.round(rect.bottom * heightScale);

            scaledBoxes.add(new Rect(left, top, right, bottom));
            Log.e("ScaledBox", "Left: " + left + ", Top: " + top + ", Right: " + right + ", Bottom: " + bottom);
        }
        return scaledBoxes;
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private Uri saveImageToFile(@NonNull ImageProxy imageProxy) {

        int targetWidth = boundingBoxView.getWidth();
        int targetHeight = boundingBoxView.getHeight();
        // Convert ImageProxy to Bitmap
        Image image = imageProxy.getImage();
        if (image == null) {
            return null;
        }

        // Get the image format and dimensions
        int imageWidth = image.getHeight();
        int imageHeight = image.getWidth();

        // Create a ByteBuffer to hold the image data
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);;

        // Calculate the aspect ratio
        float aspectRatio = (float) imageWidth / (float) imageHeight;
        int newWidth, newHeight;

        if (aspectRatio > 1) { // Landscape
            newWidth = targetWidth;
            newHeight = Math.round(targetWidth / aspectRatio);
        } else { // Portrait or square
            newHeight = targetHeight;
            newWidth = Math.round(targetHeight * aspectRatio);
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);



        Uri uri = null;
        try {
            // Create a temporary file in the cache directory
            File tempFile = File.createTempFile("captured_image_" + System.currentTimeMillis(), ".jpg", getCacheDir());
            tempFile.deleteOnExit(); // Optional: Ensure the file is deleted on exit

            // Write the bitmap to the temporary file
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                uri = Uri.fromFile(tempFile); // Get URI of the saved temporary file
            }
        } catch (IOException e) {
            Log.e("SaveImage", "Error saving image: " + e.getMessage());
        }

        // Close ImageProxy
        imageProxy.close();

        return uri;
    }
}
