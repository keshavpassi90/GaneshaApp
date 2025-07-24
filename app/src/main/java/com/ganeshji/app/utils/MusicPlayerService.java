package com.ganeshji.app.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ganeshji.app.R;

public class MusicPlayerService extends Service {

    public static final String CHANNEL_ID = "MusicChannel";
    public static final int NOTIFICATION_ID = 1;

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_STOP = "ACTION_STOP";

    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();
    private boolean isPaused = false;
    private int imageResId = R.drawable.ic_placeholder;
    private String title = "Music Player";
    private int audioResId = -1; // ✅ store audio resource
    private static OnMusicCompletionListener completionListener;

    public static void setOnMusicCompletionListener(OnMusicCompletionListener listener) {
        completionListener = listener;
    }

    public class LocalBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getStringExtra("ACTION");
        int newAudioResId = intent.getIntExtra("AUDIO_RES_ID", -1);
        imageResId = intent.getIntExtra("IMAGE_RES_ID", R.drawable.ic_placeholder);
        title = intent.getStringExtra("NOTIFY_TITLE");

        // ✅ If new audio ID is provided, store it:
        if (newAudioResId != -1) {
            audioResId = newAudioResId;
        }

        if (action != null) {
            switch (action) {
                case ACTION_PLAY:
                    play();
                    break;
                case ACTION_PAUSE:
                    pause();
                    break;
                case ACTION_STOP:
                    stopMusic();
                    stopForeground(true);
                    break;
            }
        } else if (audioResId != -1) {
            if (mediaPlayer == null) {
                initMediaPlayer();
                mediaPlayer.start();
                isPaused = false;
                showNotification();
            } else if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPaused = false;
                updateNotification();
            }
        }

        return START_STICKY;
    }

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, audioResId);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(mp -> {
            if (completionListener != null) {
                completionListener.onMusicCompleted();
            }
            stopMusic();
            stopForeground(true);

        });
    }

    public void play() {
        if (mediaPlayer == null) {
            if (audioResId != -1) {
                initMediaPlayer();
                mediaPlayer.start();
                isPaused = false;
                showNotification();
            }
        } else if (isPaused) {
            mediaPlayer.start();
            isPaused = false;
            updateNotification();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
            updateNotification();
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPaused = false;

        // ✅ Always stop foreground
        stopForeground(true);

        // ✅ Also stop the service
        stopSelf();
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void showNotification() {
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, buildNotification());
    }

    private Notification buildNotification() {
        PendingIntent playIntent = PendingIntent.getBroadcast(
                this, 0,
                new Intent(this, NotificationActionReceiver.class).setAction(ACTION_PLAY),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        PendingIntent pauseIntent = PendingIntent.getBroadcast(
                this, 1,
                new Intent(this, NotificationActionReceiver.class).setAction(ACTION_PAUSE),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        PendingIntent stopIntent = PendingIntent.getBroadcast(
                this, 2,
                new Intent(this, NotificationActionReceiver.class).setAction(ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(isPaused ? "Paused" : "Playing")
                .setSmallIcon(R.drawable.ganesha)
                .addAction(R.drawable.ic_notify_play, "Play", playIntent)
                .addAction(R.drawable.ic_notify_pause, "Pause", pauseIntent)
                .addAction(R.drawable.ic_notify_stop, "Stop", stopIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(!isPaused)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Music Playback", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
}
