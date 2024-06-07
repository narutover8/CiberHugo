package com.example.ciberhugo.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {
    private static final String TAG = "TimerService";
    private final IBinder binder = new LocalBinder();
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning = false;

    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            timeLeftInMillis = intent.getLongExtra("timeLeftInMillis", 0);
            startCountDown(timeLeftInMillis);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startCountDown(long timeLeftInMillis) {
        this.timeLeftInMillis = timeLeftInMillis;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerService.this.timeLeftInMillis = millisUntilFinished;
                timerRunning = true;
                Log.d(TAG, "Time left: " + millisUntilFinished / 1000);
                // Broadcast the time left
                Intent intent = new Intent("TIMER_UPDATE");
                intent.putExtra("timeLeftInMillis", millisUntilFinished);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                TimerService.this.timeLeftInMillis = 0;
                timerRunning = false;
                Log.d(TAG, "Timer finished");
                // Broadcast the time finished
                Intent intent = new Intent("TIMER_UPDATE");
                intent.putExtra("timeLeftInMillis", 0);
                sendBroadcast(intent);
            }
        }.start();
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }
}