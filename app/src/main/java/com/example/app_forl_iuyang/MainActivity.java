package com.example.app_forl_iuyang;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;


import com.example.app_forl_iuyang.model3D.View.ModelActivity;

import org.andresoviedo.util.android.AndroidURLStreamHandlerFactory;


import java.net.Socket;
import java.net.URL;


public class MainActivity extends Activity {


    // Custom handler: org/andresoviedo/app/util/url/android/Handler.class
    static {
        System.setProperty("java.protocol.handler.pkgs", "org.andresoviedo.util.android");
        URL.setURLStreamHandlerFactory(new AndroidURLStreamHandlerFactory());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread myThread=new Thread(){//创建子线程
            @Override
            public void run() {
                try{
                    Socket socket = new Socket("dufolk.xicp.io",Integer.parseInt("21314"));
                    ((MySocket)getApplication()).setSocket(socket);

                    ((MySocket)getApplication()).print_sock();
                    System.out.println("ok");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程
        // Set main layout controls.
        // Basically, this is a screen with the app name just in the middle of the scree
        setContentView(R.layout.activity_main);

        // Start Model activity.
        MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), StartActivity.class));
        MainActivity.this.finish();
    }

    @SuppressWarnings("unused")
    private void init() {
        MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), ModelActivity.class));
        MainActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
