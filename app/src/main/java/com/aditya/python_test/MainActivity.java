package com.aditya.python_test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView mtext;
    Button compare, open, capture;
    Uri selectedfile;
    ImageView mimg;
    File file;
    Uri outputfileuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mtext = (TextView)findViewById(R.id.mtext);
        compare = (Button)findViewById(R.id.compare);
        open = (Button)findViewById(R.id.open);
        mimg = (ImageView)findViewById(R.id.mimg);
        capture = (Button)findViewById(R.id.capture);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 100);
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
            }
        });

        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Python py = Python.getInstance();
                PyObject pyObject = py.getModule("myscript");
                PyObject pobj = pyObject.callAttr("main");
                mtext.setText(pobj.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mimg.setImageBitmap(photo);

            File filename = new File("/data/user/0/com.aditya.python_test/files/chaquopy/AssetFinder/app/test.jpg");
            try (FileOutputStream out = new FileOutputStream(filename)) {
                float aspectRatio = photo.getWidth() /
                        (float) photo.getHeight();
                int width = 250;
                int height = Math.round(width / aspectRatio);

                Bitmap.createScaledBitmap(photo, width, height, false).compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored

                Python py = Python.getInstance();

                PyObject pyObject = py.getModule("myscript");

                PyObject pobj = pyObject.callAttr("main", "/data/user/0/com.aditya.python_test/files/chaquopy/AssetFinder/app/test.jpg");

                checkResult(pobj.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 123 && resultCode == RESULT_OK) {
            selectedfile = data.getData(); //The uri with the location of the file
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                selectedfile);

                float aspectRatio = bitmap.getWidth() /
                        (float) bitmap.getHeight();
                int width = 250;
                int height = Math.round(width / aspectRatio);

                FileOutputStream outStream = null;
                File dir = new File("/data/user/0/com.aditya.python_test/files/chaquopy/AssetFinder/app/");
                dir.mkdirs();
                String fileName = "test.jpg";
                File outFile = new File(dir, fileName);
                outStream = new FileOutputStream(outFile);
                Bitmap.createScaledBitmap(bitmap, width, height, false).compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();

                Python py = Python.getInstance();

                PyObject pyObject = py.getModule("myscript");

                PyObject pobj = pyObject.callAttr("main", "/data/user/0/com.aditya.python_test/files/chaquopy/AssetFinder/app/test.jpg");

                File imgFile = new  File("/data/user/0/com.aditya.python_test/files/chaquopy/AssetFinder/app/test.jpg");

                if(imgFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    mimg.setImageBitmap(myBitmap);

                }

                checkResult(pobj.toString());
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }

        }
    }

    private void checkResult(String res) {
        if(res.equals("True")){
            mtext.setText("It is Aditya");
        }else {
            mtext.setText("It is not Aditya");
        }
    }
}