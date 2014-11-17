package com.aml.missaolen;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

public class LocationService extends Service{
    LocationManager lm;
    PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //---use the LocationManager class to obtain locations data---
        lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Intent i = new Intent(this, LocationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        //---request for location updates using GPS---
        lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000,
                10,
                pendingIntent);
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //---remove the pending intent---
        lm.removeUpdates(pendingIntent);

        super.onDestroy();
    }
}
