package com.example.screenrecorder;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION;
import static android.os.Environment.DIRECTORY_MOVIES;

/**
 * RecordService Class
 * <p>
 * Background service for recording the device screen.
 * Listens for commands to stop or start recording by the user
 * and by screen locked/unlock events. Notification in the
 * notification center informs user about the running recording
 * service.
 */
public final class RecordService extends Service {

    private ServiceHandler mServiceHandler;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaRecorder mMediaRecorder;
    private int resultCode;
    private Intent data;

    private static final String TAG = "RECORDERSERVICE";
    private static final String EXTRA_RESULT_CODE_D = "resultcode";
    private static final String EXTRA_DATA_D = "data";
    private static final int ONGOING_NOTIFICATION_ID = 23;

    /*
     *
     */
    static Intent newIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(EXTRA_RESULT_CODE_D, resultCode);
        intent.putExtra(EXTRA_DATA_D, data);
        return intent;
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (resultCode == RESULT_OK) {
                startRecording(resultCode, data);
            } else {
            }
        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Starting recording service", Toast.LENGTH_SHORT).show();

        resultCode = intent.getIntExtra(EXTRA_RESULT_CODE_D, 0);
        data = intent.getParcelableExtra(EXTRA_DATA_D);

        if (resultCode == 0 || data == null) {
            throw new IllegalStateException("Result code or data missing.");
        }

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_REDELIVER_INTENT;
    }

    private void startRecording(int resultCode, Intent data) {

        MediaProjectionManager mProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMediaRecorder = new MediaRecorder();

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(metrics);

        int mScreenDensity = metrics.densityDpi;
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(8 * 1000 * 1000);
        mMediaRecorder.setVideoFrameRate(15);
        mMediaRecorder.setVideoSize(displayWidth, displayHeight);

        String videoDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES).getAbsolutePath();
        Long timestamp = System.currentTimeMillis();

        String orientation = "portrait";
        if (displayWidth > displayHeight) {
            orientation = "landscape";
        }
        String filePathAndName = videoDir + "/time_" + timestamp.toString() + "_mode_" + orientation + ".mp4";

        mMediaRecorder.setOutputFile(filePathAndName);

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        Surface surface = mMediaRecorder.getSurface();
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("RecordingActivity",
                displayWidth, displayHeight, mScreenDensity, VIRTUAL_DISPLAY_FLAG_PRESENTATION,
                surface, null, null);
        mMediaRecorder.start();

        startTimer();
        Log.v(TAG, "Started recording");
        Log.e(TAG, "Started recording");
    }

    private void startTimer() {
        Handler h = new Handler();
        long delayInMilliseconds = 60000;
        Runnable runnable = new Runnable() {
            public void run() {
                stopRecording();
                Log.v(TAG, "Recording Stopped");
                Log.e(TAG, "Recording Stopped");
            }
        };
        h.postDelayed(runnable, delayInMilliseconds);
    }

    private void stopRecording() {
        try {
            mMediaRecorder.stop();
            mMediaProjection.stop();
            mMediaRecorder.release();
            mVirtualDisplay.release();
        } catch (Exception e) {

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        stopSelf();
        Toast.makeText(this, "Recorder service stopped", Toast.LENGTH_SHORT).show();
    }
}