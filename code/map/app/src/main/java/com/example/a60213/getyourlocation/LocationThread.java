package com.example.a60213.getyourlocation;

import android.os.Handler;
import android.widget.TextView;
import android.location.LocationManager;
import android.location.Location;

import org.w3c.dom.Text;

/**
 * Created by 60213 on 2017/5/7.
 */

public class LocationThread extends Thread {

    private int count = 3;
    private Handler handler;
    private TextView count_down;
    private LocationManager locationManager;

    private TextView edittext_latitude;
    private TextView edittext_longitude;

    private int count_fail = 1;
    private Location location = null;
    private boolean running = true;

    public void setup(Handler handler,
                      TextView count_down,
                      TextView edittext_latitude,
                      TextView edittext_longitude,
                      LocationManager locationManager){
        this.handler = handler;
        this.count_down = count_down;
        this.edittext_latitude = edittext_latitude;
        this.edittext_longitude = edittext_longitude;
        this.locationManager = locationManager;
    }

    public void stop_running(){
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            while (count > 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        count_down.setText(Integer.toString(count));
                    }
                });
                try {
                    count--;
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            count = 4;
            try {
                if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    int keep_requiring = 100;
                    while (location == null && keep_requiring > 0) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        keep_requiring--;
                    }
                }
                if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    int keep_requiring = 100;
                    while (location == null && keep_requiring > 0) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        keep_requiring--;
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (location != null) {
                        synchronized (edittext_latitude) {
                            edittext_latitude.setText(Double.toString(location.getLatitude()));
                        }
                        synchronized (edittext_longitude) {
                            edittext_longitude.setText(Double.toString(location.getLongitude()));
                        }
                    } else {
                        String fail = "require fail at " + Integer.toString(count_fail++);
                        synchronized (edittext_latitude) {
                            edittext_latitude.setText(fail);
                        }
                        synchronized (edittext_longitude) {
                            edittext_longitude.setText(fail);
                        }
                    }
                }
            });
            try {
                String ret = Util_http.sendPost("http://niugenen.6655.la:60000/PersonalTrack/PointTrackServlet",
                        "test=True&userid=1&point={'latitude':12.12,'longitude':13.13,'location':'XiaMen'}&timestramp="
                                + Long.toString(System.currentTimeMillis()));
                System.out.println("[Http reponse] " + ret);
            } catch (Exception e) {
                System.out.println("Fail to send http message.");
                e.printStackTrace();
            }

        }
    }
}
