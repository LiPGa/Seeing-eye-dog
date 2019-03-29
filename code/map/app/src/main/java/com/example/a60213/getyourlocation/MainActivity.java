package com.example.a60213.getyourlocation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;

public class MainActivity extends AppCompatActivity{

    public void onSwitchButtonClick(View v){
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.mainfadein,);
    }

    private Integer time_interval = 1;
    private EditText text_time_interval;
    private TextView text_time_count;
    public void button_confirm_click(View view){
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int new_time_interval = 1;
                    synchronized (text_info) {
                        text_info.setText("confirm button click");
                    }

                    new_time_interval = Integer.parseInt(text_time_interval.getText().toString());
                    if (new_time_interval >= 1 && new_time_interval <= 9) {
                        text_time_count.setText( Integer.toString(new_time_interval) );
                        time_interval = new_time_interval;
                    }
                    else if (new_time_interval < 1) {
                        text_time_count.setText(Integer.toString(1));
                        time_interval = 1;
                    }
                    else {
                        text_time_count.setText(Integer.toString(9));
                        time_interval = 9;
                    }

                    synchronized (text_info){
                        text_info.setText("time interval = " + Integer.toString(new_time_interval) );
                    }
                }
            });
        } catch(Exception e){
            synchronized (text_info){
                text_info.setText("exception in confirm button click" );
            }
            e.printStackTrace();
        }
    }

    private static Handler handler=new Handler();

    private TextView text_latitude;
    private TextView text_longitude;

    private Integer value_la = 10;
    private Integer value_lo = 10;

    private Button button_start;
    private Button button_stop;

    private BGThread bg;

    public void onStartButtonClick(View v){
        synchronized (text_info) {
            text_info.setText("start button click");
        }
        try {
            bg = new BGThread();
            bg.setup(handler,text_latitude,text_longitude,value_la,value_lo,time_interval);
            bg.start();
        } catch(Exception e){
            synchronized (text_info) {
                text_info.setText("exception in start button on click");
            }
            e.printStackTrace();
        }
        button_start.setClickable(false);
        button_stop.setClickable(true);
        synchronized (text_info) {
            text_info.setText("start button close");
        }
    }

    public void onStopButtonClick(View v){
        try {
            synchronized (text_info) {
                text_info.setText("stop button click");
            }
            bg.stop_thread();
            button_start.setClickable(true);
            button_stop.setClickable(false);
            synchronized (text_info) {
                text_info.setText("start button close");
            }
        }catch(Exception e){
            synchronized (text_info) {
                text_info.setText("exception in stop button on click");
            }
            e.printStackTrace();
        }
    }

    private TextView text_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        text_latitude = (TextView) findViewById(R.id.text_latitude);
        text_longitude= (TextView) findViewById(R.id.text_longitude);

        button_start = (Button)findViewById(R.id.button_start);
        button_stop= (Button)findViewById(R.id.button_stop);

        button_start.setClickable(true);
        button_stop.setClickable(false);

        text_info = (TextView)findViewById(R.id.text_info);

        text_time_interval = (EditText) findViewById(R.id.input_time_interval);
        text_time_count = (TextView) findViewById(R.id.text_time_count);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
