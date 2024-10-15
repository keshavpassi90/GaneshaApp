package com.ganeshji.app;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static android.content.ContentValues.TAG;


public class MusicService extends Service implements AudioManager.OnAudioFocusChangeListener {
    public static Boolean serviceRunning = false;
    String LOG_CLASS = "MusicService";
    int NOTIFICATION_ID = 121212;
    public static final String NOTIFY_STOP = "com.ganeshji.nametestapp.stop";
    public static final String NOTIFY_DELETE = "com.ganeshji.nametestapp.delete";
    public static final String NOTIFY_PLAY = "com.ganeshji.nametestapp.play";
    MediaPlayer mediaPlayer;
    Uri url;
    String dataUrl;
    String filename;
    String filepath;
    String title;
    File foundFile;
    AudioManager audioManager;
    private WifiManager.WifiLock wifiLock;

    MainActivity mainActivity;
    Notification notification;
    private Context mContext;
    private boolean mAudioFocusGranted = false;
    private boolean mAudioIsPlaying = false;
    public static final String CHANNEL_ID = "com.ganeshji.nametestapp";
    String channelName = "Music Background Service";
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //returns the instance of the service
    public class LocalBinder extends Binder {
        public MusicService getServiceInstance() {
            return MusicService.this;
        }
    }

    public void registerClient(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Controls.stopControl(getApplicationContext());
            }
        });
        wifiLock = ((WifiManager) Objects.requireNonNull(getApplicationContext().getSystemService(Context.WIFI_SERVICE)))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock");
        mainActivity = new MainActivity();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceRunning = true;
        dataUrl = PlayerConstants.SONG_URL;
        filename = PlayerConstants.SONG_NAME;
        title = PlayerConstants.SONG_TITLE;

        playSong();
        notifications();

        PlayerConstants.PLAY_STOP_HANDLER = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String message = (String)msg.obj;
                if(mediaPlayer == null)
                    return false;
                if(message.equalsIgnoreCase(getResources().getString(R.string.stop))){
                    PlayerConstants.SONG_STOPPED = true;
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                }else if(message.equalsIgnoreCase(getResources().getString(R.string.play))){
                    PlayerConstants.SONG_STOPPED = false;
                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                    }
                }else if(message.equalsIgnoreCase(getResources().getString(R.string.cancelNotify))){
                    PlayerConstants.SONG_STOPPED = true;
                    mainActivity.cancelNotify(PlayerConstants.SONG_NAME);
                    return false;
                }
                notifications();
                try{
                    mainActivity.changeButton(PlayerConstants.SONG_NAME);
                }catch(Exception e){
                    Log.d("TAG", "TAG Pressed: ");
                    e.printStackTrace();
                }
                return false;
            }
        });

        return START_STICKY;
    }


    public void setListeners(RemoteViews view) {
        // Define the context and target class for each Intent
        Context context = getApplicationContext();
        Intent deleteIntent = new Intent(context, NotificationBroadcast.class);
        deleteIntent.setAction(MusicService.NOTIFY_DELETE); // Replace with your delete action string

        Intent stopIntent = new Intent(context, NotificationBroadcast.class);
        stopIntent.setAction(MusicService.NOTIFY_STOP); // Replace with your stop action string

        Intent playIntent = new Intent(context, NotificationBroadcast.class);
        playIntent.setAction(MusicService.NOTIFY_PLAY); // Replace with your play action string

        // Create PendingIntents for each action with the correct flags
        PendingIntent pStop = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        view.setOnClickPendingIntent(R.id.btnStop, pStop);

        PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);
    }



    public void stop() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (mAudioFocusGranted) {
                abandonAudioFocus();
            }
            wifiLockRelease();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDestroy() {
        /*if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }*/
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (mAudioFocusGranted) {
            abandonAudioFocus();
        }
        wifiLockRelease();
        serviceRunning = false;
    }

    private void wifiLockRelease(){

        if (wifiLock != null && wifiLock.isHeld()) {

            wifiLock.release();
        }
    }

     void notifications() {
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.music_notification);
        try {
            simpleContentView.setImageViewResource(R.id.imageViewAlbumArt, R.mipmap.ic_launcher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (PlayerConstants.SONG_STOPPED) {
            simpleContentView.setViewVisibility(R.id.btnStop, View.GONE);
            simpleContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
        } else {
            simpleContentView.setViewVisibility(R.id.btnStop, View.VISIBLE);
            simpleContentView.setViewVisibility(R.id.btnPlay, View.GONE);
        }

        simpleContentView.setTextViewText(R.id.textSongName, PlayerConstants.SONG_TITLE);
        simpleContentView.setTextViewText(R.id.textAlbumName, "is playing now.. ");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_ID, channelName);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(false)  // Make sure it's not auto-cancelable
                .setOngoing(true)  // Make notification ongoing to make it sticky
                .setContentText("Playing Music")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(simpleContentView)
                .build();

        setListeners(simpleContentView);

        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        startForeground(NOTIFICATION_ID, notification);
    }

    @SuppressLint("NewApi")
    private void playSong() {
        if (wifiLock != null && !wifiLock.isHeld()) {
            wifiLock.acquire();
        }
        filepath=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+filename + ".mp3";
        foundFile = new File (filepath);

        try {
            mediaPlayer.reset();
            if (foundFile.exists()){
                mediaPlayer.setDataSource(filepath);
            }else {
                url = Uri.parse(dataUrl);
                mediaPlayer.setDataSource(getApplicationContext(), url);
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer player) {
                    if (!mAudioFocusGranted ) {
                        requestAudioFocus();
                    }
                    player.start();
                }

            });
            PlayerConstants.SONG_STOPPED = false;
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                PlayerConstants.SONG_STOPPED = false;
                mediaPlayer.start();

                break;

            case AudioManager.AUDIOFOCUS_LOSS:

                PlayerConstants.SONG_STOPPED = true;
                mainActivity.cancelNotify(PlayerConstants.SONG_NAME);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT :

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                PlayerConstants.SONG_STOPPED = true;
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }

                break;
        }

    }

    private boolean requestAudioFocus() {
        if (!mAudioFocusGranted) {

            int result = audioManager.requestAudioFocus(this,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusGranted = true;
            } else {
                // FAILED
                Log.e("Music",">>>>>>>>>>>>> FAILED TO GET AUDIO FOCUS <<<<<<<<<<<<<<<<<<<<<<<<");
            }
        }
        return mAudioFocusGranted;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId , String channelName){
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW);
        chan.setSound(null, null);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        Objects.requireNonNull(manager).createNotificationChannel(chan);
    }

    private void abandonAudioFocus() {

        int result = audioManager.abandonAudioFocus(this);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocusGranted=false;
        } else {
            // FAILED
            Log.e(TAG,
                    ">>>>>>>>>>>>> FAILED TO ABANDON AUDIO FOCUS <<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }
}
