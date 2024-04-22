package com.example.cameragall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private Camera camera;
    private SurfaceView surfaceViewCamera;
    private SurfaceHolder surfaceHolder;
    private Button btnTakePhoto;
    private RecyclerView recyclerViewGallery;
    private List<String> photoPaths;
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceViewCamera = findViewById(R.id.surfaceViewCamera);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        recyclerViewGallery = findViewById(R.id.recyclerViewGallery);

        surfaceHolder = surfaceViewCamera.getHolder();
        surfaceHolder.addCallback(surfaceCallback);

        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(this));
        photoPaths = new ArrayList<>();
        photoAdapter = new PhotoAdapter(this, photoPaths);
        recyclerViewGallery.setAdapter(photoAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewGallery.setLayoutManager(layoutManager);
        btnTakePhoto.setOnClickListener(v -> takePhoto());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            setupCamera();
        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Toast.makeText(MainActivity.this, "Ошибка при сохранении изображения.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Toast.makeText(MainActivity.this, "Фотография сохранена: " + pictureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                addPhotoToGallery(pictureFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Файл изображения не найден.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Ошибка при сохранении изображения.", Toast.LENGTH_SHORT).show();
            }

            camera.startPreview();
        }
    };

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open();
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    };

    private void setupCamera() {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void takePhoto() {
        if (camera != null) {
            camera.takePicture(null, null, pictureCallback);
        } else {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show();
        }
    }

    private File getOutputMediaFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e("MainActivity", "Внешнее хранилище недоступно");
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("MainActivity", "Не удалось создать директорию для сохранения фотографий");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private void addPhotoToGallery(String photoPath) {
        photoPaths.add(photoPath);
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCamera();
            } else {
                Toast.makeText(this, "Разрешение на использование камеры не получено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
        }
    }
}
