package com.biswa1045.MemeOnly;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView memeImage, shareButton,save;
    private BottomSheetDialog bottomSheetDialog;
    ProgressBar progressBar;
    Boolean ani=true;
    String url1 = "";
    LottieAnimationView l;
    private AppUpdateManager mAppUpdateManager;
    private  static final int RC_APP_UPDATE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memeImage = (ImageView) findViewById(R.id.memeImage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        shareButton = (ImageView) findViewById(R.id.shareButton);
        save = (ImageView) findViewById(R.id.save);
        l=findViewById(R.id.swipe_right_animation);
        checkConnection();
        loadMeme();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    BitmapDrawable drawable = (BitmapDrawable)memeImage.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    String bitmappath = MediaStore.Images.Media.insertImage(getContentResolver() ,bitmap,"",null);
                    Uri uri = Uri.parse(bitmappath);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpg");
                    intent.putExtra(Intent.EXTRA_STREAM,uri);
                    intent.putExtra(Intent.EXTRA_TEXT,"\nhey!! Come look at this... \n"+"Platstore Link : https://play.google.com/store/apps/details?id="+ getPackageName());
                    startActivity(Intent.createChooser(intent,"share"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdff = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String currentDateTime = sdff.format(new Date());
                String filename = currentDateTime+".jpg";

                File f = new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS+"/memeonly");

                if(!f.isDirectory()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            Files.createDirectory(Paths.get(f.getAbsolutePath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        f.mkdir();
                        f.mkdirs();
                        Toast.makeText(getApplicationContext(), f.getPath(), Toast.LENGTH_LONG).show();
                    }
                }
                DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url1));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.allowScanningByMediaScanner();
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/memeonly/"+filename);
                downloadManager.enqueue(request);
                save.setEnabled(false);
                //save.setImageDrawable(getResources().getDrawable(R.drawable.save_blue));
                save.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);

            }
        });
        findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(MainActivity.this,R.style.BottomSheetTheme);
                View sheetview= LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                        (ViewGroup)findViewById(R.id.bottom_sheet_l));
                bottomSheetDialog.setContentView(sheetview);
                bottomSheetDialog.show();
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog. findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,DownloadActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                bottomSheetDialog.findViewById(R.id.rate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rateapp();
                    }
                });
                bottomSheetDialog. findViewById(R.id.pp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent3 = new Intent(MainActivity.this,PrivacyActivity.class);
                        startActivity(intent3);
                        finish();
                    }
                });
            }
        });
/*


 */
        memeImage.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {


            public void onSwipeLeft() {
                //Toast.makeText(MainActivity.this, "Loading new meme", Toast.LENGTH_SHORT).show();
                save.setEnabled(true);
                //save.setImageDrawable(getResources().getDrawable(R.drawable.save_blue));
                save.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                l.setVisibility(View.INVISIBLE);
                loadMeme();
            }


        });
        //app update
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if(result.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result,AppUpdateType.FLEXIBLE,MainActivity.this,RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//update end
        mAppUpdateManager.registerListener(installStateUpdatedListener);
    }
    //app update
    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if(state.installStatus()== InstallStatus.DOWNLOADED){
                showCompletedUpdate();
            }
        }
    };

    @Override
    protected void onStop() {
        if(mAppUpdateManager!=null)
        {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
        super.onStop();
    }

    private void showCompletedUpdate(){
        Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content),"New app is ready!",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==RC_APP_UPDATE && requestCode!=RESULT_OK){
            Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // update end
    public void rateapp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
    public void loadMeme() {


        progressBar.setVisibility(View.VISIBLE);
        //RequestQueue queue = Volley.newRequestQueue(this);
      String url = "https://meme-api.herokuapp.com/gimme";
       // String url = "https://v2.jokeapi.dev/joke/Any";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("json parsed", "WORKED");

                        try {
                            url1 = response.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Glide.with(MainActivity.this)
                                .load(url1)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                        memeImage.setImageResource(R.drawable.img_not_supported);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        if(ani==true){
                                            l.setVisibility(View.VISIBLE);
                                            l.playAnimation();
                                            ani = false;
                                        }

                                        return false;
                                    }
                                })
                                .error(R.drawable.img_not_supported)
                                .into(memeImage);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        // queue.add(jsonObjectRequest);
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

            }
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {

        finishAffinity();
    }
}

