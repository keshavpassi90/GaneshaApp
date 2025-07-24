package com.ganeshji.app.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ganeshji.app.HomeActivity;
import com.ganeshji.app.R;
import com.ganeshji.app.utils.AppProgress;
import com.ganeshji.app.utils.SetWallpaperTask;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class DetailWallpaperFragment extends Fragment {
    private AdView mAdView;

    String imageURL="";
    RelativeLayout backRL;
    TextView setWallpaper;
    SimpleDraweeView imageView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment_home layout
        return inflater.inflate(R.layout.fragment_detail_wallpaper, container, false);
    }
//    new SetWallpaperTask(this).execute(imageUrl);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        setUpIds(view);
        setUpClicks();
        if (getArguments() != null) {
            DetailWallpaperFragmentArgs args = DetailWallpaperFragmentArgs.fromBundle(getArguments());
            imageURL = args.getUrl();
            // Use your URL here

            Uri uri = Uri.parse(imageURL);
            Log.e("Data","URL == "+imageURL);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setOldController(imageView.getController())
                    .build();

            imageView.setController(controller);

        }


    }

    // setup ids
    void setUpIds(View view){

        backRL = view.findViewById(R.id.backRL);
        imageView = view.findViewById(R.id.imageView);
        setWallpaper = view.findViewById(R.id.setWallpaper);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest); // ✅ Load ad into XML AdView

    }

// setup Clicks
    void setUpClicks(){

        backRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(DetailWallpaperFragment.this).popBackStack();
            }
        });

        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetWallpaperTask(getActivity()).execute(imageURL);
            }
        });
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

