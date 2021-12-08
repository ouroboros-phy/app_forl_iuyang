package com.example.app_forl_iuyang;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



import com.example.app_forl_iuyang.R;
import com.example.app_forl_iuyang.model3D.View.ModelActivity;

import org.andresoviedo.util.android.AndroidUtils;
import org.andresoviedo.util.android.ContentUtils;

import java.util.HashMap;
import java.util.Map;

public class StartActivity extends Activity {
    private Button button1;
    private Button button2;
    private Button button3;
    private int class_of_sfm = 0;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1000;
    private Map<String, Object> loadModelParameters = new HashMap<>();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        button1 = findViewById(R.id.camera);
        button2 = findViewById(R.id.question);
        //button3.findViewById(R.id.waxi);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, Test.class);
                StartActivity.this.startActivity(intent);//启动新的Intent

            }});
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, Select_Model.class);
                StartActivity.this.startActivity(intent);//启动新的Intent

            }});
     

    }
    private void loadModel(){
        if (!AndroidUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE)) {
            return;
        }
        try {
            switch (class_of_sfm){
                case 0:
                    String file = "models/123.obj";
                    ContentUtils.provideAssets(this);
                    launchModelRendererActivity(Uri.parse("assets://"+ getPackageName() + "/"+ file));
            }}catch (Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }}
    private void launchModelRendererActivity(Uri uri) {
        System.out.println(uri.toString());
        Log.i("Menu", "Launching renderer for '" + uri + "'");
        Intent intent = new Intent(getApplicationContext(), ModelActivity.class);
        intent.putExtra("uri", uri.toString());
        intent.putExtra("immersiveMode", "true");

        // content provider case
        if (!loadModelParameters.isEmpty()) {
            intent.putExtra("type", loadModelParameters.get("type").toString());
            loadModelParameters.clear();
        }

        startActivity(intent);
    }}
