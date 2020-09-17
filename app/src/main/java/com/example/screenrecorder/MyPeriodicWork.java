package com.example.screenrecorder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class MyPeriodicWork extends Worker {

    private static final String TAB = MyPeriodicWork.class.getSimpleName();
    private Context context;

    public MyPeriodicWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }


    @NonNull
    @Override
    public ListenableWorker.Result doWork() {

        Log.e(TAB, "PeriodicWork in BackGround");

        Intent intent = new Intent(context, RecordingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return Result.success();
    }
}
