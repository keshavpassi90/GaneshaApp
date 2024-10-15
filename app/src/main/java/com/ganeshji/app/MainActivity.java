package com.ganeshji.app;


import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.FirebaseApp;

import com.vorlonsoft.android.rate.AppRate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class MainActivity extends AppCompatActivity {
    String LOG_CLASS = "MainActivity";
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    Integer tabPosition=0;
    Intent playbackServiceIntent;
    private FloatingActionMenu fam;
    Boolean isPlaying=false;
    String filepath;
    Long downloadID;
    File foundFile;
    MusicService service;
    ArrayList<Long> list = new ArrayList<>();
    private DownloadManager downloadManager;
    EqualizerView equalizer;
    private static final int PERMISSION_REQUEST_CODE = 100;
    ConnectivityManager cm;

    private boolean isDialogOpen = false;
    private AdView mAdView;
    private AppOpenAd appOpenAd;
    private boolean isLoadingAd = false;
    private FloatingActionButton fabPlay,fab2Play,fab3Play,fab4Play,fab5Play;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);
        MobileAds.initialize(this, initializationStatus -> {});
        // Setting up test device IDs
        List<String> testDevices = new ArrayList<>();
        testDevices.add(getResources().getString(R.string.testDevice));

        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);


        // Load the App Open Ad
//        loadAppOpenAd();
        cm = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mAdView = findViewById(R.id.adView);
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        fabPlay = findViewById(R.id.fab1Play);
        fab2Play = findViewById(R.id.fab2Play);
        fab3Play = findViewById(R.id.fab3Play);
        fab4Play = findViewById(R.id.fab4Play);
        fab5Play = findViewById(R.id.fab5Play);
        fam =  findViewById(R.id.fab_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    //showToast("Menu is opened");
                } else {
                    //showToast("Menu is closed");
                }
            }
        });

        //handling each floating action button clicked
        fabPlay.setOnClickListener(onButtonClick());
        fab2Play.setOnClickListener(onButtonClick());
        fab3Play.setOnClickListener(onButtonClick());
        fab4Play.setOnClickListener(onButtonClick());
        fab5Play.setOnClickListener(onButtonClick());


        viewPager = findViewById(R.id.viewpager_id);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabSix(),"गजानन श्री गणराय");
        adapter.addFragment(new TabTwo(),"सुखकर्ता दुखहर्ता");
        adapter.addFragment(new TabOne(),"जय गणेश देवा");
        adapter.addFragment(new TabThree(),"वक्रतुण्ड महाकाय");
        adapter.addFragment(new TabFour(),"मंत्र");
        adapter.addFragment(new TabFive(),"गणनायक शुभदायका");
        adapter.addFragment(new TabSeven(),"कथा");

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabPosition=position;
                switch (position) {
                    case 0:
                        toolbar.setTitle("गजानन श्री गणराय");
                        break;
                    case 1:
                        toolbar.setTitle(R.string.tab_view2);
                        break;
                    case 2:
                        toolbar.setTitle("जय गणेश देवा");
                        break;
                    case 3:
                        toolbar.setTitle("वक्रतुण्ड महाकाय मंत्र");
                        break;
                    case 4:
                        toolbar.setTitle("मंत्र");
                        break;
                    case 5:
                        toolbar.setTitle("गणनायक शुभदायका");
                        break;
                    case 6:
                        toolbar.setTitle("कथा");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);

        tabLayout.setupWithViewPager(viewPager);

        playbackServiceIntent = new Intent(this, MusicService.class);



        AppRate.with(this)
                .setInstallDays((byte) 0)                  // default is 10, 0 means install day, 10 means app is launched 10 or more days later than installation
                .setLaunchTimes((byte) 10)               // default is 10, 3 means app is launched 3 or more times
                .setRemindInterval((byte) 1)               // default is 1, 1 means app is launched 1 or more days after neutral button clicked
                .setRemindLaunchesNumber((byte) 1)         // default is 0, 1 means app is launched 1 or more times after neutral button clicked
                .monitor();

        // Monitors the app launch times
        AppRate.showRateDialogIfMeetsConditions(this);


        toolbar.setTitle("गजानन श्री गणराय");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED);
        }else {
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
//        registerReceiver(onComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        equalizer = (EqualizerView) findViewById(R.id.equalizer_view);
        equalizer.setVisibility(View.INVISIBLE);
        //equalizer.stopBars();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //main item selected
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                //finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.fab1Play:
                        if(isPlaying) {
                            stopMusicService(playbackServiceIntent);
                        }
                        PlayerConstants.SONG_NAME="JaiGaneshDeva";
                        PlayerConstants.SONG_TITLE="Jai Ganesh Deva (Aarti)";
                        PlayerConstants.SONG_URL="https://hindi3.djpunjab.app/load-hindi/GdF6lVj-ja8egvSsHaxoUQ==/Jai%20Ganesh%20Deva-Aarti.mp3";
                        startIntent();
                        showEquilizer();
                        filepath=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ PlayerConstants.SONG_NAME + ".mp3";
                        foundFile = new File (filepath);
                        if(!checkNetwork() && !foundFile.exists()){
                            networkAlert();
                            return;
                        }
                        fabPlay.setImageResource(R.drawable.pause);
                        fabPlay.setId(R.id.fab1Pause);
                        fab2Play.setImageResource(R.drawable.play);
                        fab2Play.setId(R.id.fab2Play);
                        fab3Play.setImageResource(R.drawable.play);
                        fab3Play.setId(R.id.fab3Play);
                        fab4Play.setImageResource(R.drawable.play);
                        fab4Play.setId(R.id.fab4Play);
                        fab5Play.setImageResource(R.drawable.play);
                        fab5Play.setId(R.id.fab5Play);
                        isPlaying=true;
//                        if (!checkPermissions()) {
//                            requestPermissions();
//                        }else{
//                            beginDownload();
//                        }
                        break;
                    case R.id.fab1Pause:
                        if(isPlaying){
                            stopMusicService(playbackServiceIntent);
                            fabPlay.setImageResource(R.drawable.play);
                            fabPlay.setId(R.id.fab1Play);
                            isPlaying=false;
                        }
                        hideEquilizer();
                        break;
                    case R.id.fab2Play:
                        if(isPlaying) {
                            stopMusicService(playbackServiceIntent);
                        }
                        PlayerConstants.SONG_NAME="SukhHartaDukhHarta";
                        PlayerConstants.SONG_TITLE="SukhHarta DukhHarta";
                        PlayerConstants.SONG_URL="https://hindi3.djpunjab.app/load/lxwGqp0owlhIsBbudhRSGw==/Sukhkarta%20Dukhharta.mp3";
                        startIntent();
                        showEquilizer();
                        filepath=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ PlayerConstants.SONG_NAME + ".mp3";
                        foundFile = new File (filepath);
                        if(!checkNetwork() && !foundFile.exists()){
                            networkAlert();
                            return;
                        }
                        fab2Play.setImageResource(R.drawable.pause);
                        fab2Play.setId(R.id.fab2Pause);
                        fabPlay.setImageResource(R.drawable.play);
                        fabPlay.setId(R.id.fab1Play);
                        fab3Play.setImageResource(R.drawable.play);
                        fab3Play.setId(R.id.fab3Play);
                        fab4Play.setImageResource(R.drawable.play);
                        fab4Play.setId(R.id.fab4Play);
                        fab5Play.setImageResource(R.drawable.play);
                        fab5Play.setId(R.id.fab5Play);
                        isPlaying=true;

//                        if (!checkPermissions()) {
//                            requestPermissions();
//                        }else{
//                            beginDownload();
//                        }
                        break;
                    case R.id.fab2Pause:
                        if(isPlaying){
                            stopMusicService(playbackServiceIntent);
                            fab2Play.setImageResource(R.drawable.play);
                            fab2Play.setId(R.id.fab2Play);
                            isPlaying=false;
                        }
                        hideEquilizer();
                        break;
                    case R.id.fab3Play:
                        if(isPlaying) {
                            stopMusicService(playbackServiceIntent);
                        }

                        PlayerConstants.SONG_NAME="VakratundaMahakaya";
                        PlayerConstants.SONG_TITLE="Vakratunda Mahakaya";
                        PlayerConstants.SONG_URL="https://hindi3.djpunjab.app/load/OhWSgu_w8hDAzw8O30F-1g==/Vakratunda%20Mahakaya.mp3";
                        filepath=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ PlayerConstants.SONG_NAME + ".mp3";
                        foundFile = new File (filepath);
                        if(!checkNetwork() && !foundFile.exists()){
                            networkAlert();
                            return;
                        }
                        startIntent();
                        showEquilizer();

                        fab3Play.setImageResource(R.drawable.pause);
                        fab3Play.setId(R.id.fab3Pause);
                        fabPlay.setImageResource(R.drawable.play);
                        fabPlay.setId(R.id.fab1Play);
                        fab2Play.setImageResource(R.drawable.play);
                        fab2Play.setId(R.id.fab2Play);
                        fab4Play.setImageResource(R.drawable.play);
                        fab4Play.setId(R.id.fab4Play);
                        fab5Play.setImageResource(R.drawable.play);
                        fab5Play.setId(R.id.fab5Play);
                        isPlaying=true;

//                        if (!checkPermissions()) {
//                            requestPermissions();
//                        }else{
//                            beginDownload();
//                        }
                        break;
                    case R.id.fab3Pause:
                        if(isPlaying){
                            stopMusicService(playbackServiceIntent);
                            fab3Play.setImageResource(R.drawable.play);
                            fab3Play.setId(R.id.fab3Play);
                            isPlaying=false;
                        }
                        hideEquilizer();
                        break;
                    case R.id.fab4Play:
                        if(isPlaying) {
                            stopMusicService(playbackServiceIntent);
                        }

                        PlayerConstants.SONG_NAME="OmGanGanpataye";
                        PlayerConstants.SONG_TITLE="Om Gan Ganpataye";
                        PlayerConstants.SONG_URL="https://hindi3.djpunjab.app/load/NOerQ3iIPVQlFvuwfZx_LA==/Om%20Gan%20Ganapataye%20Namo%20Namah.mp3";
                        filepath=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ PlayerConstants.SONG_NAME + ".mp3";
                        foundFile = new File (filepath);
                        if(!checkNetwork() && !foundFile.exists()){
                            networkAlert();
                            return;
                        }
                        startIntent();
                        showEquilizer();

                        fab4Play.setImageResource(R.drawable.pause);
                        fab4Play.setId(R.id.fab4Pause);
                        fabPlay.setImageResource(R.drawable.play);
                        fabPlay.setId(R.id.fab1Play);
                        fab3Play.setImageResource(R.drawable.play);
                        fab3Play.setId(R.id.fab3Play);
                        fab2Play.setImageResource(R.drawable.play);
                        fab2Play.setId(R.id.fab2Play);
                        fab5Play.setImageResource(R.drawable.play);
                        fab5Play.setId(R.id.fab5Play);
                        isPlaying=true;
//                        if (!checkPermissions()) {
//                            requestPermissions();
//                        }else{
//                            beginDownload();
//                        }
                        break;
                    case R.id.fab4Pause:
                        if(isPlaying){
                            stopMusicService(playbackServiceIntent);
                            fab4Play.setImageResource(R.drawable.play);
                            fab4Play.setId(R.id.fab4Play);
                            isPlaying=false;
                        }
                        hideEquilizer();
                        break;
                    case R.id.fab5Play:
                        if(isPlaying) {
                            stopMusicService(playbackServiceIntent);
                        }

                        PlayerConstants.SONG_NAME="GananayakaShubhadayaka";
                        PlayerConstants.SONG_TITLE="Gananayaka Shubhadayaka";
                        PlayerConstants.SONG_URL="https://hindi3.djpunjab.app/load-hindi/BnuIOu8wwpzxBpnYKi0dZw==/Gananayaka%20Shubhadayaka.mp3";
                        filepath=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ PlayerConstants.SONG_NAME + ".mp3";
                        foundFile = new File (filepath);
                        if(!checkNetwork() && !foundFile.exists()){
                            networkAlert();
                            return;
                        }
                        startIntent();
                        showEquilizer();

                        fab5Play.setImageResource(R.drawable.pause);
                        fab5Play.setId(R.id.fab5Pause);
                        fabPlay.setImageResource(R.drawable.play);
                        fabPlay.setId(R.id.fab1Play);
                        fab3Play.setImageResource(R.drawable.play);
                        fab3Play.setId(R.id.fab3Play);
                        fab4Play.setImageResource(R.drawable.play);
                        fab4Play.setId(R.id.fab4Play);
                        fab2Play.setImageResource(R.drawable.play);
                        fab2Play.setId(R.id.fab2Play);
                        isPlaying=true;

//                        if (!checkPermissions()) {
//                            requestPermissions();
//                        }else{
//                            beginDownload();
//                        }
                        break;
                    case R.id.fab5Pause:
                        if(isPlaying){
                            stopMusicService(playbackServiceIntent);
                            fab5Play.setImageResource(R.drawable.play);
                            fab5Play.setId(R.id.fab5Play);
                            isPlaying=false;
                        }
                        hideEquilizer();
                        break;
                        // Do something
                }

                fam.close(true);
            }
        };
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {

            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.e("IN", "" + referenceId);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(referenceId);
            Cursor cursor = downloadManager.query(query);
            if( cursor != null && cursor.moveToFirst() ) {
                int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String savedFilePath = cursor.getString(fileNameIndex);
                String currentFileName = savedFilePath.substring(savedFilePath.lastIndexOf('/')+1, savedFilePath.length());

                /*String currentFileName = savedFilePath.substring(savedFilePath.lastIndexOf("/"), savedFilePath.length());
                currentFileName = currentFileName.substring(1);*/
                Log.e("Current file name", currentFileName);
                File oldFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),currentFileName);
                File newFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),currentFileName.replace(".tmp",""));
                oldFile.renameTo(newFile);
                Log.e("New file name", newFile.toString());
                cursor.close();
            }
            list.remove(referenceId);
            if (list.isEmpty())
            {
                Log.e("INSIDE", "" + referenceId);

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
        stopMusicService(playbackServiceIntent);

    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void beginDownload() {
        filepath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + PlayerConstants.SONG_NAME + ".mp3";
        foundFile = new File(filepath);
        String tmpFilePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + PlayerConstants.SONG_NAME + ".mp3.tmp";
        File foundFile2 = new File(tmpFilePath);
        if (!foundFile.exists() && !foundFile2.exists()) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(PlayerConstants.SONG_URL))
                    .setTitle(PlayerConstants.SONG_TITLE)// Title of the Download Notification
                    .setDescription("Saving offline")// Description of the Download Notification
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                    //.setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
                    .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, PlayerConstants.SONG_NAME + ".mp3.tmp");
            downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
            list.add(downloadID);
        }
    }



    public void startIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // startForegroundService is required for Android O and above
            startService(playbackServiceIntent);  // Use startService() to avoid foreground service
        } else {
            startService(playbackServiceIntent);
        }
        bindService(playbackServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }







    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                Log.e("Data", "permission ok");
                // All permissions are granted
                beginDownload();
            } else {
                // Some permissions are denied
                if (!isDialogOpen) {
                    // Optionally, show a dialog to inform the user about the necessity of the permissions
                    showErrorDialog(this, "Go to settings. Please enable the required permissions in Settings.", getString(R.string.app_name));
                }
            }
        }
    }

    public  void showErrorDialog(final Context context, String message, String title) {
        try {
            if (!isDialogOpen) {
                isDialogOpen = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                isDialogOpen = false;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                intent.setData(uri);
                                context.startActivity(intent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);
                builder.show();
            }
        } catch (WindowManager.BadTokenException e) {
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeButton(String filename){

        switch (filename) {
            case "JaiGaneshDeva":
                if(PlayerConstants.SONG_STOPPED){
                    fabPlay.setImageResource(R.drawable.play);
                    fabPlay.setId(R.id.fab1Play);
                    isPlaying=false;
                    hideEquilizer();
                }else{
                    fabPlay.setImageResource(R.drawable.pause);
                    fabPlay.setId(R.id.fab1Pause);
                    fab2Play.setImageResource(R.drawable.play);
                    fab2Play.setId(R.id.fab2Play);
                    fab3Play.setImageResource(R.drawable.play);
                    fab3Play.setId(R.id.fab3Play);
                    fab4Play.setImageResource(R.drawable.play);
                    fab4Play.setId(R.id.fab4Play);
                    fab5Play.setImageResource(R.drawable.play);
                    fab5Play.setId(R.id.fab5Play);
                    isPlaying=true;
                    showEquilizer();
                }

                break;
            case "SukhHartaDukhHarta":
                if(PlayerConstants.SONG_STOPPED){
                    fab2Play.setImageResource(R.drawable.play);
                    fab2Play.setId(R.id.fab2Play);
                    isPlaying=false;
                    hideEquilizer();
                }else{
                    fab2Play.setImageResource(R.drawable.pause);
                    fab2Play.setId(R.id.fab2Pause);
                    fabPlay.setImageResource(R.drawable.play);
                    fabPlay.setId(R.id.fab1Play);
                    fab3Play.setImageResource(R.drawable.play);
                    fab3Play.setId(R.id.fab3Play);
                    fab4Play.setImageResource(R.drawable.play);
                    fab4Play.setId(R.id.fab4Play);
                    fab5Play.setImageResource(R.drawable.play);
                    fab5Play.setId(R.id.fab5Play);
                    isPlaying=true;
                    showEquilizer();
                }

                break;
            case "VakratundaMahakaya":
                if(PlayerConstants.SONG_STOPPED){
                    fab3Play.setImageResource(R.drawable.play);
                    fab3Play.setId(R.id.fab3Play);
                    isPlaying=false;
                    hideEquilizer();
                }else{
                    fab3Play.setImageResource(R.drawable.pause);
                    fab3Play.setId(R.id.fab3Pause);
                    fab2Play.setImageResource(R.drawable.play);
                    fab2Play.setId(R.id.fab2Play);
                    fabPlay.setImageResource(R.drawable.play);
                    fabPlay.setId(R.id.fab1Play);
                    fab4Play.setImageResource(R.drawable.play);
                    fab4Play.setId(R.id.fab4Play);
                    fab5Play.setImageResource(R.drawable.play);
                    fab5Play.setId(R.id.fab5Play);
                    isPlaying=true;
                    showEquilizer();
                }

                break;
            case "OmGanGanpataye":
                if(PlayerConstants.SONG_STOPPED){
                    fab4Play.setImageResource(R.drawable.play);
                    fab4Play.setId(R.id.fab4Play);
                    isPlaying=false;
                    hideEquilizer();
                }else{
                    fab4Play.setImageResource(R.drawable.pause);
                    fab4Play.setId(R.id.fab4Pause);
                    fab2Play.setImageResource(R.drawable.play);
                    fab2Play.setId(R.id.fab2Play);
                    fab3Play.setImageResource(R.drawable.play);
                    fab3Play.setId(R.id.fab3Play);
                    fabPlay.setImageResource(R.drawable.play);
                    fabPlay.setId(R.id.fab1Play);
                    fab5Play.setImageResource(R.drawable.play);
                    fab5Play.setId(R.id.fab5Play);
                    isPlaying=true;
                    showEquilizer();
                }

                break;
            case "GananayakaShubhadayaka":
                if(PlayerConstants.SONG_STOPPED){
                    fab5Play.setImageResource(R.drawable.play);
                    fab5Play.setId(R.id.fab5Play);
                    isPlaying=false;
                    hideEquilizer();
                }else{
                    fab5Play.setImageResource(R.drawable.pause);
                    fab5Play.setId(R.id.fab5Pause);
                    fab2Play.setImageResource(R.drawable.play);
                    fab2Play.setId(R.id.fab2Play);
                    fab3Play.setImageResource(R.drawable.play);
                    fab3Play.setId(R.id.fab3Play);
                    fab4Play.setImageResource(R.drawable.play);
                    fab4Play.setId(R.id.fab4Play);
                    fabPlay.setImageResource(R.drawable.play);
                    fabPlay.setId(R.id.fab1Play);
                    isPlaying=true;
                    showEquilizer();
                }

                break;
            // Do something
        }
    }

    protected void onResume() {
        super.onResume();

        try{

            boolean isServiceRunning = MusicService.serviceRunning;
            if (isServiceRunning) {
                changeButton(PlayerConstants.SONG_NAME);
            }
//            handleAdLogic();
        }catch(Exception e){}

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder _service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) _service;
            service = binder.getServiceInstance(); //Get instance of your service!
            service.registerClient(MainActivity.this); //Activity register in the service as client for callabcks!

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            service = null;

        }
    };

    public void stopMusicService(Intent intent) {
        if (service != null) {
            try {
                service.stop();
                stopService(intent);
                unbindService(mConnection);
                service = null;
            } catch (IllegalArgumentException ex) {
                stopService(intent);
                service = null;
                ex.printStackTrace();
            }
        }else{
            Log.d("stop", "stopMusicService: ");
        }
    }

    public void cancelNotify(String filename) {
        if (service != null && playbackServiceIntent!=null) {
            try {
                service.stop();
                stopService(playbackServiceIntent);
                unbindService(mConnection);
                service = null;
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            } catch (IllegalArgumentException ex) {
                stopService(playbackServiceIntent);
                service = null;
                ex.printStackTrace();
            }

            changeButton(filename);
        }else{
            Log.d("stop", "stopMusicService: ");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int yes;
        int no;
        new AlertDialog.Builder(this)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    public void showEquilizer(){
        equalizer.animateBars();
        equalizer.setVisibility(View.VISIBLE);
    }
    public void hideEquilizer(){
        equalizer.stopBars();
        equalizer.setVisibility(View.INVISIBLE);
    }


    public void networkAlert(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Network Error");
        mBuilder.setMessage("Please check your internet connection");
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private boolean checkNetwork(){
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = true;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    result = activeNetwork.isConnectedOrConnecting();
                }
            }
        }
        return result;

    }

}
