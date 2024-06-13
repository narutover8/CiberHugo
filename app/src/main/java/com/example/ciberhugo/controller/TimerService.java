/**
 * Autor: Hugo Villodres Moreno
 * Fecha de entrega: 14/06/2024
 * Proyecto TFG FINAL
 * Curso: 2ºDAM
 */

package com.example.ciberhugo.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

/**
 * Servicio para manejar un temporizador de cuenta regresiva.
 */
public class TimerService extends Service {
    private static final String TAG = "TimerService";

    // Binder para la comunicación con componentes externos
    private final IBinder binder = new LocalBinder();

    // Temporizador de cuenta regresiva
    private CountDownTimer countDownTimer;

    // Tiempo restante en milisegundos
    private long timeLeftInMillis;

    // Indicador de si el temporizador está en ejecución
    private boolean timerRunning = false;

    /**
     * Clase Binder local para permitir la conexión del servicio con clientes externos.
     */
    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    /**
     * Método llamado cuando un cliente se enlaza con el servicio.
     * @param intent Intent que inició el enlace
     * @return Binder que permite la comunicación con el servicio
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Método llamado cuando el servicio se inicia.
     * @param intent Intent recibido para iniciar el servicio
     * @param flags Indicadores adicionales sobre cómo se debe iniciar el servicio
     * @param startId ID único para la solicitud de inicio del servicio
     * @return Constante que indica cómo debe comportarse el servicio si se detiene inesperadamente
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Si el intent no es nulo, obtén el tiempo restante del intent y comienza la cuenta regresiva
        if (intent != null) {
            timeLeftInMillis = intent.getLongExtra("timeLeftInMillis", 0);
            startCountDown(timeLeftInMillis);
        }
        return START_STICKY; // El servicio se reiniciará si se cierra inesperadamente
    }

    /**
     * Método llamado cuando el servicio se destruye.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancelar el temporizador si existe
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * Método para iniciar la cuenta regresiva con el tiempo especificado.
     * @param timeLeftInMillis Tiempo restante en milisegundos para la cuenta regresiva
     */
    private void startCountDown(long timeLeftInMillis) {
        this.timeLeftInMillis = timeLeftInMillis;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerService.this.timeLeftInMillis = millisUntilFinished;
                timerRunning = true;
                Log.d(TAG, "Time left: " + millisUntilFinished / 1000);
                // Transmitir la actualización del temporizador a través de un broadcast
                Intent intent = new Intent("TIMER_UPDATE");
                intent.putExtra("timeLeftInMillis", millisUntilFinished);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                TimerService.this.timeLeftInMillis = 0;
                timerRunning = false;
                Log.d(TAG, "Timer finished");
                // Transmitir la finalización del temporizador a través de un broadcast
                Intent intent = new Intent("TIMER_UPDATE");
                intent.putExtra("timeLeftInMillis", 0);
                sendBroadcast(intent);
            }
        }.start(); // Iniciar el temporizador
    }

    /**
     * Método para verificar si el temporizador está en ejecución.
     * @return true si el temporizador está en ejecución, false de lo contrario
     */
    public boolean isTimerRunning() {
        return timerRunning;
    }

    /**
     * Método para obtener el tiempo restante en milisegundos del temporizador.
     * @return Tiempo restante en milisegundos
     */
    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }
}
