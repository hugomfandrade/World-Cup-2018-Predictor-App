package org.hugoandrade.worldcup2018.predictor.model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public abstract class LifecycleLoggingService extends Service {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Hook method called when the Service is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Service is being created anew.
        Log.d(TAG,
                "onCreate() - service created anew");
    }

    /**
     * Hook method called to deliver an intent sent via
     * startService().
     */
    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {
        Log.d(TAG,
                "onStartCommand() - intent received");
        return super.onStartCommand(intent,
                flags,
                startId);
    }

    /**
     * Factory method that's invoked when a client calls
     * bindService().
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,
                "onBind() - client has invoked bindService()");

        return null;
    }

    /**
     * Factory method that's invoked when a client calls
     * bindService().
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,
                "onUnbind() - client has invoked unbindService()");
        return super.onUnbind(intent);
    }

    /**
     * Factory method that's invoked when new clients calls
     * bindService().
     */
    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG,
                "onRebind() - new client has invoked bindService()");

        super.onRebind(intent);
    }

    /**
     * Hook method called when the last client unbinds from the
     * Service.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,
                "onDestroy() - service is being shut down");
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG,
                "onTaskRemoved() - task is being removed");
    }
}
