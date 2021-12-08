package com.example.app_forl_iuyang;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.app_forl_iuyang.model3D.View.ModelActivity;

import org.andresoviedo.util.android.AndroidUtils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;

import org.opencv.android.JavaCamera2View;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaActivity extends CameraActivity {

    private static final String TAG = "OpencvCam";
    private Socket socket = null;
    private PrintWriter pw,ps;
	private JavaCamera2View javaCameraView;
    private OutputStream os;
    private Mat img;
    private InputStream is;
    private BufferedReader br;
    private String recevied;
    private String info;
    private String class_of_sfm = "-1";
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1000;
    private Map<String, Object> loadModelParameters = new HashMap<>();
    private CameraBridgeViewBase.CvCameraViewListener2 cvCameraViewListener2 = new CameraBridgeViewBase.CvCameraViewListener2() {
        private int pc = 10;
        @Override

        public void onCameraViewStarted(int width, int height) {
            Log.i(TAG, "onCameraViewStarted width=" + width + ", height=" + height);
        }

        @Override
        public void onCameraViewStopped() {
            Log.i(TAG, "onCameraViewStopped");

        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            img = inputFrame.rgba();
            Mat img0 =  img.clone();
            Mat dst = new  Mat();
            br=new BufferedReader(new InputStreamReader(is));
            Imgproc.resize(img0,dst, new Size(256, 256));
            Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap,true);//添加透明度
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bout);
            byte[] imgBytes = bout.toByteArray();
            String sender = Base64.encodeToString(imgBytes,Base64.DEFAULT);
            String len_str = new String(String.format("%-16d", sender.length()).getBytes());
            pw.write(len_str);
            pw.flush();
            try {
                recevied = br.readLine();
                if (recevied != "-1" && recevied !="-2"){
                    pc--;
                }
                System.out.println(recevied);
            } catch (IOException e) {
                System.out.println(e);

            }

            ps.write(sender);
            ps.flush();

            loadModel(recevied);


            return img;
    	}
	};

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            Log.i(TAG, "onManagerConnected status=" + status + ", javaCameraView=" + javaCameraView);
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    if (javaCameraView != null) {
                        javaCameraView.setCvCameraViewListener(cvCameraViewListener2);
                        // 禁用帧率显示
                        javaCameraView.disableFpsMeter();
                        javaCameraView.enableView();
                    }
                }
                break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

	//复写父类的 getCameraViewList 方法，把 javaCameraView 送到父 Activity，一旦权限被授予之后，javaCameraView 的 setCameraPermissionGranted 就会自动被调用。
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        Log.i(TAG, "getCameraViewList");
        List<CameraBridgeViewBase> list = new ArrayList<>();
        list.add(javaCameraView);
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findView();
        socket = ((MySocket)getApplication()).getSocket();
        try {


            os = socket.getOutputStream();
            pw = new PrintWriter(os);

            ps = new PrintWriter(os);

            is = socket.getInputStream();





        }catch (IOException e) {
            e.printStackTrace();}


    }
    private void loadModel(String choose_id){
        if (!AndroidUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE)) {
            return;
        }
        try {
            switch (choose_id){
                case "-1":
                    String file = "models/123.obj";
                    launchModelRendererActivity(Uri.parse("assets://"+getPackageName() + "/" + file));
            }}catch (Exception ex){
            Toast.makeText(CaActivity.this,"这是"+choose_id,Toast.LENGTH_LONG).show();

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
        CaActivity.this.finish();
        startActivity(intent);
    }
    private void findView() {
        javaCameraView = findViewById(R.id.javaCameraView);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "initDebug true");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.i(TAG, "initDebug false");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        }
    }
}
