package com.example.app_forl_iuyang;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.app_forl_iuyang.model3D.View.ModelActivity;

import org.andresoviedo.util.android.AndroidUtils;

import java.util.HashMap;
import java.util.Map;

public class Select_Model extends Activity {
    private int class_of_sfm = 0;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1000;
    private Map<String, Object> loadModelParameters = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__model);
        loadModel();

    }
    private void loadModel(){
        if (!AndroidUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE)) {
            return;
        }
        try {
            switch (class_of_sfm){
                case 0:
                    String file = "models/123.obj";
                    launchModelRendererActivity(Uri.parse("assets://"+getPackageName() + "/" + file));
    }}catch (Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }}
    private void launchModelRendererActivity(Uri uri) {
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
    }
}
