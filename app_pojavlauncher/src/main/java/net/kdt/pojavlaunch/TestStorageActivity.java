package net.kdt.pojavlaunch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mio.activity.NewLauncherActivity;

import net.kdt.pojavlaunch.tasks.AsyncAssetManager;

import java.util.function.Consumer;

public class TestStorageActivity extends Activity {
    private final int REQUEST_STORAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                showDialog(v -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_STORAGE_REQUEST_CODE);
                });
            } else {
                exit();
            }
        } else {
            if (!isStorageAllowed(this)) {
                showDialog(v -> requestStoragePermission());
            } else {
                exit();
            }
        }
    }

    private void showDialog(Consumer<Void> consumer) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.request_storage)
                .setNegativeButton(R.string.exit, (d, w) -> {
                    finish();
                })
                .setPositiveButton(R.string.confirm, (d, w) -> {
                    consumer.accept(null);
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_STORAGE_REQUEST_CODE) {
            checkPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_REQUEST_CODE) {
            checkPermissions();
        }
    }

    public static boolean isStorageAllowed(Context context) {
        //Getting the permission status
        int result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);


        //If permission is granted returning true
        return result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_REQUEST_CODE);
    }

    private void exit() {
        if (!Tools.checkStorageRoot(this)) {
            startActivity(new Intent(this, MissingStorageActivity.class));
            return;
        }
        //Only run them once we get a definitive green light to use storage
        AsyncAssetManager.unpackComponents(this);
        AsyncAssetManager.unpackSingleFiles(this);

        Intent intent = new Intent(this, NewLauncherActivity.class);
        startActivity(intent);
        finish();
    }
}
