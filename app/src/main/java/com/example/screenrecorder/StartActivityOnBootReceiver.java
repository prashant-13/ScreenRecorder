package com.example.screenrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class StartActivityOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PeriodicWorkRequest mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(MyPeriodicWork.class,
                15, TimeUnit.MINUTES)
                .addTag("periodicWorkRequest")
                .build();

        WorkManager.getInstance().enqueue(mPeriodicWorkRequest);

    }

}
