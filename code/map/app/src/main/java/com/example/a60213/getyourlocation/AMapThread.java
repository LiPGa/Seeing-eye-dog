package com.example.a60213.getyourlocation;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;

import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.maps2d.model.Polyline;

/**
 * Created by 60213 on 2017/5/7.
 */

public class AMapThread extends Thread {

    private AMapLocationClient mLocationClient;

    private TextView text_latitude;
    private TextView text_longitude;

    private Handler handler;

    private int time_interval = 1;
    private TextView count_down;
    private Integer count = 10;

    private TextView info;
    private TextView msg;

    private int userid;

    public void setup(Handler handler,AMapLocationClient client, TextView la, TextView lo,
                      int time_interval,TextView count_down,
                      TextView info,TextView msg,
                      int userid){
        this.handler = handler;
        this.mLocationClient = client;
        this.text_latitude = la;
        this.text_longitude = lo;
        this.time_interval = time_interval;
        this.count_down = count_down;
        this.count = this.time_interval;
        this.info = info;
        this.msg = msg;
        this.userid = userid;
    }

    private Boolean running = true;

    public void stop_thread(){
        synchronized (count){
            count = -1;
        }
        synchronized (running) {
            running = false;
        }
    }

    private AMap aMap = null;
    private Handler aMapHandler = null;
    public void onPathTrack(double latitude, double longitude){
        if(aMap != null && last_latitude != -1){
            final LatLng latLng = new LatLng(latitude,longitude);
            //Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("position").snippet("DefaultMarker"));
            //aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng) );
            final List<LatLng> latLngs = new ArrayList<LatLng>();
            latLngs.add(new LatLng(last_latitude,last_longitude));
            latLngs.add( latLng );
            System.out.println("[Line][s]"+last_latitude+" "+last_longitude+"[e]"+latitude +" "+longitude);
            if(last_latitude != latitude || last_longitude != longitude) {  //make sense
                aMapHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Polyline polyline = aMap.addPolyline(new PolylineOptions().
                                addAll(latLngs).width(1).color(Color.argb(255, 1, 1, 1)));
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                    }
                });
            }
            if(aMap.getCameraPosition() != null)
                System.out.println("[zoom]" + aMap.getCameraPosition().zoom);
            System.out.println("[zoom][min]" + aMap.getMinZoomLevel() +"[max]"+aMap.getMaxZoomLevel());
        }
    }
    public void start_path_track(Handler handler,AMap aMap){
        this.aMapHandler = handler;
        this.aMap = aMap;
    }
    public void stop_path_track(){
        aMapHandler = null;
        aMap = null;
    }

    private String ret;
    private double last_latitude = -1;
    private double last_longitude = -1;
    private double latitude;
    private double longitude;
    @Override
    public void run(){
        while( running ){
            try{
                AMapLocation location = mLocationClient.getLastKnownLocation();

                if(location.getErrorCode() == 0){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    final StringBuffer location_str = new StringBuffer();
                    location_str.append( location.getCountry() );
                    location_str.append( location.getProvince() );
                    location_str.append( location.getCity() );
                    location_str.append( location.getStreet() );
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (text_longitude){
                                text_longitude.setText( Double.toString(longitude) );
                            }
                            synchronized (text_latitude){
                                text_latitude.setText( Double.toString(latitude) );
                            }
                            synchronized (info){
                                    info.setText(location_str);
                            }
                        }
                    });
                    try {
                        ret = Util_http.sendPost(
                                "http://niugenen.6655.la:60000/PersonalTrack/PointTrackServlet",
                                        "test=True&userid=" + userid +
                                        "&point={'latitude':" + latitude +
                                                ",'longitude':" + longitude +
                                        ",'location':'" + URLEncoder.encode( location_str.toString(),"utf-8") +
                                        "'}&timestramp=" + Long.toString(System.currentTimeMillis()));
                        //path track on amap
                        onPathTrack(latitude,longitude);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (msg) {
                                    msg.setText(ret);
                                }
                            }
                        });
                    } catch (Exception e) {
                        //System.out.println("Fail to send http message.");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (msg) {
                                    msg.setText("Fail to send http message.");
                                }
                            }
                        });
                        e.printStackTrace();
                    }
                }
                else{//failed
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (text_longitude){
                                text_longitude.setText( "fail to locate in amap thread" );
                            }
                            synchronized (text_latitude){
                                text_latitude.setText("fail to locate in amap thread" );
                            }
                        }
                    });
                }
                //count down with time_interval
                count = time_interval;
                while(count >= 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            count_down.setText(Integer.toString(count));
                        }
                    });
                    Thread.sleep(1000);
                    count--;
                }
                last_latitude = latitude;
                last_longitude = longitude;
            }catch(Exception e){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (msg) {
                            msg.setText("Exception in amap thread.");
                        }
                    }
                });
                e.printStackTrace();
            }
        }
    }
}
