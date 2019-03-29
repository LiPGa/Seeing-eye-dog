package com.example.a60213.getyourlocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.location.LocationManager;

import com.amap.api.location.*;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;

import com.amap.api.maps2d.*;

import org.w3c.dom.Text;

import java.security.Permission;

public class Main2Activity extends AppCompatActivity {

    public void onShowPathClick(View v){
        try{
            Intent intent = new Intent(Main2Activity.this, Main4Activity.class);
            synchronized (edittext_latitude) {
                intent.putExtra("latitude", Double.parseDouble(edittext_latitude.getText().toString()));
            }
            synchronized (edittext_longitude) {
                intent.putExtra("longitude", Double.parseDouble(edittext_longitude.getText().toString()));
            }
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //if (bgthread != null)
        //  bgthread.stop_running();
        if(amapthread != null)
            amapthread.stop_thread();
        //do something
    }

    //private LocationThread bgthread;
    private static AMapThread amapthread;
    public static AMapThread getAmapThread(){
        return amapthread;
    }

    private String str1 = "asd";
    private String str2 = "qwe";

   // private LocationManager locationManager;
    //private int count = 3;
    //private int count_fail = 1;
    //private MyLocationListener my_location_listener;

    public void onShowAmapClick(View v){
        try{
            Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
            synchronized (edittext_latitude) {
                intent.putExtra("latitude", Double.parseDouble(edittext_latitude.getText().toString()));
            }
            synchronized (edittext_longitude) {
                intent.putExtra("longitude", Double.parseDouble(edittext_longitude.getText().toString()));
            }
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void onStopGPSClick(View v){
        try{
            if(amapthread != null){
                amapthread.stop_thread();
                amapthread = null;
            }

            button_start.setClickable(true);
            button_start.setTextColor( getResources().getColor(R.color.white) );
            button_stop.setEnabled(false);
            button_stop.setTextColor( getResources().getColor(R.color.lightgray) );
            input_interval.setEnabled(true);
            input_userid.setEnabled(true);

            button_show_in_amap.setVisibility(View.INVISIBLE);

            synchronized (text_2_msg){
                text_2_msg.setText("GPS stop.");
            }

            button_show_path.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            synchronized (text_2_msg){
                text_2_msg.setText("exception in stop");
            }
            e.printStackTrace();
        }
    }

    private int userid;
    private int interval;

    public void onStartGPSClick(View v) {
        try {
            String userid_txt = input_userid.getText().toString();
            if(userid_txt == null || userid_txt == ""){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (text_2_msg){
                            text_2_msg.setText("Please enter user id");
                        }
                    }
                });
                return;
            }
            userid = Integer.parseInt( userid_txt );

            String interval_txt = input_interval.getText().toString();
            if(interval_txt == null || interval_txt == ""){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (text_2_msg){
                            text_2_msg.setText("No interval entered. Default is 10");
                        }
                    }
                });
                interval = 10;
            }
            else{
                interval = Integer.parseInt( interval_txt );
            }

            amapthread = new AMapThread();
            amapthread.setup(handler,mLocationClient,
                    edittext_latitude,edittext_longitude,
                    interval,count_down,
                    text_2_info,text_2_msg,
                    userid);
            amapthread.start();

            button_start.setClickable(false);
            button_start.setTextColor( getResources().getColor(R.color.lightgray) );
            button_stop.setEnabled(true);
            button_stop.setTextColor( getResources().getColor(R.color.white) );
            input_interval.setEnabled(false);
            input_userid.setEnabled(false);
            button_show_in_amap.setVisibility(View.VISIBLE);
            synchronized (text_2_msg){
                text_2_msg.setText("GPS start.");
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button_show_path.setVisibility(View.VISIBLE);
                }
            },interval * 1000 + 2000 );
        }catch(Exception e){
            synchronized (text_2_msg){
                text_2_msg.setText("exception in start");
            }
            e.printStackTrace();
        }
    }

    private TextView edittext_latitude;
    private TextView edittext_longitude;
    private Handler handler;

    private EditText input_userid;
    private EditText input_interval;

    private TextView count_down;
    private TextView text_2_info;
    private TextView text_2_msg;

    private Button button_start;
    private Button button_stop;
    private Button button_show_in_amap;
    private Button button_show_path;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        input_userid = (EditText) findViewById(R.id.edittext_2_userid);
        input_interval = (EditText) findViewById(R.id.edittext_2_interval);

        button_start = (Button) findViewById(R.id.button_2_start_gps);
        button_stop = (Button) findViewById(R.id.button_2_stop_gps);
        button_start.setClickable(true);
        button_start.setTextColor(getResources().getColor(R.color.white));
        button_stop.setEnabled(false);
        button_stop.setTextColor(getResources().getColor(R.color.lightgray));

        button_show_in_amap = (Button) findViewById(R.id.button_2_show_in_amap);
        button_show_in_amap.setVisibility(View.INVISIBLE);

        button_show_path = (Button)findViewById(R.id.button_2_show_path);
        button_show_path.setVisibility(View.INVISIBLE);

        handler = new Handler();

        edittext_latitude = (TextView) findViewById(R.id.edittext_2_latitude);
        edittext_longitude = (TextView) findViewById(R.id.edittext_2_longitude);

        count_down = (TextView) findViewById(R.id.text_2_count_down);
        text_2_info = (TextView) findViewById(R.id.text_2_info);
        text_2_msg = (TextView) findViewById(R.id.text_2_msg);

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        /*mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) { }
        };*/
        //设置定位回调监听
        //mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        //mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
        //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
        //mLocationOption.setLocationMode(AMapLocationMode.Device_Sensors);

        //获取一次定位结果：
        //该方法默认为false。
        //mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        //mLocationOption.setOnceLocationLatest(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        //mLocationOption.setMockEnable(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制 default open
        mLocationOption.setLocationCacheEnable(false);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }
}
