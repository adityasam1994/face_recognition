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
    Python py;
    PyObject pyObject;
    Bitmap myimage;

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

            py = Python.getInstance();

            pyObject = py.getModule("myscript");
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
                compare_image();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            myimage = photo;
            mimg.setImageBitmap(photo);
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

                myimage = bitmap;
                mimg.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }

        }
    }

    private void compare_image(){
        if(myimage != null) {
            File filename = new File("/data/user/0/com.aditya.python_test/files/chaquopy/AssetFinder/app/test.jpg");
            try (FileOutputStream out = new FileOutputStream(filename)) {
                float aspectRatio = myimage.getWidth() /
                        (float) myimage.getHeight();
                int width = 250;
                int height = Math.round(width / aspectRatio);

                Bitmap.createScaledBitmap(myimage, width, height, false).compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored


                PyObject pobj = pyObject.callAttr("main", "/data/user/0/com.aditya.python_test/files/chaquopy/AssetFinder/app/test.jpg");

                mtext.setText(pobj.toString());
                //checkResult(pobj.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "First select or capture an Image", Toast.LENGTH_SHORT).show();
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