package com.example.a60213.getyourlocation;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by 60213 on 2017/5/7.
 */

public class MyLocationListener implements LocationListener {

    private Handler handler;

    private TextView edittext_longitude;
    private TextView edittext_latitude;

    public void setup(
            Handler handler,
            TextView edittext_latitude,
            TextView edittext_longitude
    ){
        this.handler = handler;
        this.edittext_latitude = edittext_latitude;
        this.edittext_longitude = edittext_longitude;
    }

    @Override
    public void onLocationChanged(final Location location) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (edittext_longitude){
                    edittext_longitude.setText( Double.toString(location.getLatitude()) );
                }
                synchronized (edittext_latitude){
                    edittext_latitude.setText( Double.toString(location.getLatitude()) );
                }
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
