package com.example.a60213.getyourlocation;

/**
 * Created by 60213 on 2017/5/6.
 */

import android.os.Handler;
import android.widget.TextView;

public class BGThread extends Thread {
    /*
    android not allow other thread to access UI view
    should use handler.post
     */
    private Handler handler;
    private TextView latitude;
    private TextView longitude;
    private long value_la = 100;
    private long value_lo = 100;
    private int time_interval = 1;
    public void setup(Handler h,TextView la, TextView lo,int vla,int vlo,int time_interval){
        this.handler = h;
        this.latitude = la;
        this.longitude = lo;
        this.value_la = vla;
        this.value_lo = vlo;
        this.time_interval = time_interval;
    }
    boolean running = true;
    @Override
    public void run(){
        while(running){
            value_la--;
            value_lo++;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (latitude) {
                        latitude.setText(Long.toString(value_la));
                    }
                    synchronized (longitude) {
                        longitude.setText(Long.toString(value_lo));
                    }
                }
            });
            try {
                Thread.sleep(time_interval * 1000);
            } catch (Exception e){
                e.printStackTrace();
           }
        }
    }
    public void stop_thread(){
        running = false;
    }
}
