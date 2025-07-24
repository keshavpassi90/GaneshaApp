package com.ganeshji.app.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ganeshji.app.R;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;
import com.ganeshji.app.utils.MusicPlayerService;
import com.ganeshji.app.utils.PrefsHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HomeDetailFragment extends Fragment {
    RelativeLayout backRL;
    ConstraintLayout bottomCV;
    TextView titleTV,startTimeTV,endTimeTV,mantarTV;
    ImageView imageData,btnStop,btnPlay,btnStar;
    private SeekBar seekBar;
ArrayList<GaneshItem> mFavoriteList =new ArrayList<>();
    private AdView mAdView;

    private MusicPlayerService musicService;
    private boolean isBound = false;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private   GaneshItem item;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
            updateSeekBar.run();
            // Example: If you want, you can update UI here
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_detail, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpIds(view);
         item = HomeDetailFragmentArgs.fromBundle(getArguments())
                .getGaneshItem();
        titleTV.setText(item.getTitle());
        mantarTV.setText(item.getDescription());
        imageData.setImageResource(item.getIconData());
mFavoriteList = PrefsHelper.loadFavorites(requireContext());

        if(isInFavorites(item)){
            btnStar.setImageResource(R.drawable.ic_save_item);
        }else{
            btnStar.setImageResource(R.drawable.ic_unsave);

        }
//        // Start & Bind service
//        Intent intent = new Intent(requireContext(), MusicPlayerService.class);
//        intent.putExtra("AUDIO_RES_ID", item.getAudioResId());
//        intent.putExtra("IMAGE_RES_ID",item.getIconData()); // NEW
//        intent.putExtra("NOTIFY_TITLE",item.getTitle()); // NEW
//
//        ContextCompat.startForegroundService(requireContext(), intent);
//        requireContext().bindService(intent, serviceConnection, getActivity().BIND_AUTO_CREATE);

        // Fix: Avoid bottom navigation bar overlay
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            View bottomCV = view.findViewById(R.id.bottomCV);
            bottomCV.setPadding(
                    bottomCV.getPaddingLeft(),
                    bottomCV.getPaddingTop(),
                    bottomCV.getPaddingRight(),
                    bottomInset
            );
            return insets;
        });

        if(item.isAudio()==false){
            bottomCV.setVisibility(View.GONE);
        }

        MusicPlayerService.setOnMusicCompletionListener(() -> {
            // Update your UI
//            btnPlayPause.setImageResource(R.drawable.ic_play);
            seekBar.setProgress(0);
            startTimeTV.setText("00:00");
            btnPlay.setImageResource(R.drawable.ic_pay);
        });
        setUpClicks();
    }
    // setup ids
    void setUpIds(View view){

        backRL = view.findViewById(R.id.backRL);
        titleTV = view.findViewById(R.id.titleTV);
        imageData = view.findViewById(R.id.imageData);
        bottomCV = view.findViewById(R.id.bottomCV);
        mantarTV = view.findViewById(R.id.mantarTV);

        btnStop      = view.findViewById(R.id.btnStop);
        seekBar      = view.findViewById(R.id.seekBar);
         btnPlay = view.findViewById(R.id.btnPlay);
        btnStar = view.findViewById(R.id.btnStar);
        seekBar = view.findViewById(R.id.seekBar);
        startTimeTV = view.findViewById(R.id.startTimeTV);
        endTimeTV = view.findViewById(R.id.endTimeTV);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest); // ✅ Load ad into XML AdView

    }



    // setup Clicks
    void setUpClicks(){

        backRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//
//                // ✅ THIS LINE stops the Service completely
//                stopSelf();
                if (isBound && musicService != null) {
                    musicService.stopMusic();
                } else {
                    sendActionToService(MusicPlayerService.ACTION_STOP);
                }
                NavHostFragment.findNavController(HomeDetailFragment.this).popBackStack();
            }
        });

        btnStar.setOnClickListener(v -> {

            if(isInFavorites(item)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mFavoriteList.removeIf(item -> item.getTitle().equals(item.getTitle()));
                }

                btnStar.setImageResource(R.drawable.ic_unsave);

            }else{

                mFavoriteList.add(item);

                btnStar.setImageResource(R.drawable.ic_save_item);


            }

            PrefsHelper.saveFavorites(requireContext(), mFavoriteList);

        });


        btnPlay.setOnClickListener(v -> {
            if (isBound && musicService != null) {
                if (musicService.isPlaying()) {
                    musicService.pause();
                    isPlaying = false;
                    btnPlay.setImageResource(R.drawable.ic_pay);
                } else {
                    musicService.play();
                    isPlaying = true;
                    btnPlay.setImageResource(R.drawable.pause_icon);
                }
            } else {
                sendActionToService(
                        isPlaying ? MusicPlayerService.ACTION_PAUSE : MusicPlayerService.ACTION_PLAY
                );
                isPlaying = !isPlaying;
                btnPlay.setImageResource(
                        isPlaying ? R.drawable.pause_icon : R.drawable.ic_pay
                );
            }

        });

        btnStop.setOnClickListener(v -> {
            if (isBound && musicService != null) {
                musicService.stopMusic();
                seekBar.setProgress(0);
//                endTimeTV.setText("00:00");
                startTimeTV.setText("00:00");
                btnPlay.setImageResource(R.drawable.ic_pay);
            } else {
                sendActionToService(MusicPlayerService.ACTION_STOP);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Optional: Live preview
                if (isBound && fromUser) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (isBound && musicService.isPlaying()) {
                    int pos = musicService.getCurrentPosition();
                    int dur = musicService.getDuration();
                    seekBar.setMax(dur);
                    seekBar.setProgress(pos);
                    startTimeTV.setText(formatTime(pos));
                    endTimeTV.setText(formatTime(dur));
                }
                handler.postDelayed(this, 500);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (isBound && musicService != null) {
                            musicService.stopMusic();
                        } else {
                            sendActionToService(MusicPlayerService.ACTION_STOP);
                        }

                        NavHostFragment.findNavController(HomeDetailFragment.this).popBackStack();
                    }
                }
        );

    }
    @Override
    public void onStart() {
        super.onStart();

        // Start + Bind service when fragment starts
        startAndBindMusicService();
    }

    private void startAndBindMusicService() {

        if(item.isAudio()==true){
            Context context = requireContext();

            // Replace with your audio + image resource
            int audioResId = item.getAudioResId(); // ✅ Replace with real file
            int imageResId = item.getIconData();

            Intent startIntent = new Intent(context, MusicPlayerService.class);
            startIntent.putExtra("AUDIO_RES_ID", audioResId);
            startIntent.putExtra("IMAGE_RES_ID", imageResId);
            startIntent.putExtra("NOTIFY_TITLE",item.getTitle()); // NEW

            context.startService(startIntent);

            Intent bindIntent = new Intent(context, MusicPlayerService.class);
            context.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        }
       }

    private void sendActionToService(String action) {
        Context context = requireContext();
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.putExtra("ACTION", action);
        context.startService(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            requireContext().unbindService(serviceConnection);
            isBound = false;
        }
    }



    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private boolean isInFavorites(GaneshItem item) {
        for (GaneshItem favItem : mFavoriteList) {
            if (favItem.getTitle().equals(item.getTitle())) { // use ID or unique field
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


}
