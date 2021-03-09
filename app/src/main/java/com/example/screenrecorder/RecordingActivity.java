package com.example.screenrecorder;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class RecordingActivity extends AppCompatActivity {
    private static final String TAG = "DATARECORDER";
    private static final int PERMISSION_CODE = 1;
    private MediaProjectionManager mProjectionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (isStoragePermissionGranted())
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
        if (isStoragePermissionGranted())
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
        if (isStoragePermissionGranted())
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
        if (isStoragePermissionGranted())
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
       }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PERMISSION_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode == RESULT_OK) {
            startRecordingService(resultCode, data);
            finish();
        } else {
            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void startRecordingService(int resultCode, Intent data) {
        Intent intent = RecordService.newIntent(this, resultCode, data);
        startService(intent);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
        }
    }
}