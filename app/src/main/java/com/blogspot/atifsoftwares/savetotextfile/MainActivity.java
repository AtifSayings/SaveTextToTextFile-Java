package com.blogspot.atifsoftwares.savetotextfile;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText mInputEt;
    Button mSaveBtn;
    String mText;

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize views
        mInputEt = findViewById(R.id.inputEt);
        mSaveBtn = findViewById(R.id.saveBtn);

        //handle button click
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get text form EditTex
                mText = mInputEt.getText().toString().trim(); //.trim() will remove space from start and end
                //validate
                if (mText.isEmpty()) { //user has not entered anything
                    Toast.makeText(MainActivity.this, "Please enter something", Toast.LENGTH_SHORT).show();
                } else { //user has entered text
                    //check permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            //show popup for runtime permission
                            requestPermissions(permissions, WRITE_EXTERNAL_STORAGE_CODE);
                        } else {
                            //Permission already granted
                            //save to text file
                            saveToTxtFile(mText);
                        }
                    } else {
                        //System OS<Marshmallow
                        //save to text file
                        saveToTxtFile(mText);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, save the text
                    saveToTxtFile(mText);
                } else {
                    // permission denied, show error toast
                    Toast.makeText(this, "Enable Storage Permission from settings to save file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveToTxtFile(String content) {
        //get current time for file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        try {
            //path to external storage
            File path = Environment.getExternalStorageDirectory();
            //create folder named "My Files"
            File dir = new File(path + "/My Files/");
            dir.mkdirs();
            //file name
            String fileName = "MyFile_" + timeStamp + ".txt"; //e.g MyFile_20180623_165551.txt
            File file = new File(dir, fileName);

            //FileWriter class makes it possible to write characters to a file
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            /*BufferedWriter class provides buffering for Writer instances.
            It makes the performance fast*/
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

            Toast.makeText(MainActivity.this, fileName + " is saved to\n" + dir, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}