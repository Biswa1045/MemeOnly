package com.biswa1045.MemeOnly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {
    private String[]        FilePathStrings;
    private File[]          listFile;
    GridView grid;
    GridViewAdapter         adapter;
    File                    file;
    public static Bitmap bmp = null;
    ImageView imageview;
    Dialog dialog;
    Boolean dialog_open=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        dialog = new Dialog(this);

        file = new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS+"/memeonly");

        if (file.isDirectory())
        {
            listFile = file.listFiles();
            FilePathStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++)
            {
                FilePathStrings[i] = listFile[i].getAbsolutePath();
            }
        }
        grid = (GridView)findViewById(R.id.gridview);
        grid.setNumColumns(3);
        adapter = new GridViewAdapter(this, FilePathStrings);

        grid.setAdapter(adapter);

       // GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
       // rcv.setLayoutManager(gridLayoutManager);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view,
                                     int position, long id)
            {
                showpopup(position);

            }
        });


        findViewById(R.id.back_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent o = new Intent(DownloadActivity.this,MainActivity.class);
                startActivity(o);
                finish();
            }
        });
    }
    public void showpopup(int position){
        ImageView img;
        dialog_open=true;

        dialog.setContentView(R.layout.custom_dialog);
        img = dialog.findViewById(R.id.dialog_img);

        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(FilePathStrings[position],
                bmpOptions);
        bmpOptions.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(FilePathStrings[position],
                bmpOptions);
        img.setImageBitmap(bmp);

        bmp = null;


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    @Override
    public void onBackPressed() {
        if (dialog_open==true){
            dialog.dismiss();
            dialog_open=false;
        }else{
            Intent k = new Intent(DownloadActivity.this,MainActivity.class);
            startActivity(k);
            finish();
        }


    }
    /*
    public void past_grid(){
        imageview = (ImageView)findViewById(R.id.imageView1);
        int targetWidth = 700;
        int targetHeight = 500;
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(FilePathStrings[position],
                bmpOptions);
        int currHeight = bmpOptions.outHeight;
        int currWidth = bmpOptions.outWidth;
        int sampleSize = 1;
        if (currHeight > targetHeight || currWidth > targetWidth)
        {
            if (currWidth > currHeight)
                sampleSize = Math.round((float)currHeight
                        / (float)targetHeight);
            else
                sampleSize = Math.round((float)currWidth
                        / (float)targetWidth);
        }
        bmpOptions.inSampleSize = sampleSize;
        bmpOptions.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(FilePathStrings[position],
                bmpOptions);
        imageview.setImageBitmap(bmp);
        imageview.setScaleType(ImageView.ScaleType.FIT_XY);
        bmp = null;
    }

     */
    public void ImginDialog(){

    }
}