package com.aml.missaolen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
	
public class LocationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String locationKey = LocationManager.KEY_LOCATION_CHANGED;
        if (intent.hasExtra(locationKey)) {
            Location loc = (Location)intent.getExtras().get(locationKey);
            DBAdapter db = new DBAdapter(context);
            db.open();
            int latlon[] = Cordinat.WGS84toETRSTM35FIN(loc.getLatitude(),loc.getLongitude());
            db.insertLocation(String.valueOf(latlon[0]), String.valueOf(latlon[1]));
            db.close();
        }
    }
}
