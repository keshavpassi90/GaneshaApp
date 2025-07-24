package com.ganeshji.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ganeshji.app.R;
import com.ganeshji.app.adapter.GaneshItemAdapter;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;
import com.ganeshji.app.utils.InterstitialAdHelper;
import com.ganeshji.app.view_model.HomeViewModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private View rootView; // 👈 persistent root view
    private RecyclerView recyclerView;
    private ConstraintLayout aartiCL,kathasCL,mantraCL;
    private ArrayList<GaneshItem> mHomeList = new ArrayList<>();
    private HomeViewModel homeViewModel;
    private boolean isAdapterSet = false; // 👈 prevent resetting adapter
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (recyclerView == null) {
            recyclerView = view.findViewById(R.id.recyclerView);
            aartiCL = view.findViewById(R.id.aartiCL);
            mantraCL = view.findViewById(R.id.mantraCL);
            kathasCL = view.findViewById(R.id.kathasCL);
            mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest); // ✅ Load ad into XML AdView

            homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

            if (!homeViewModel.isDataLoaded()) {
                homeViewModel.loadData(resId -> getString(resId));
            }

            mHomeList = new ArrayList<>(homeViewModel.getItemList());

            if (!isAdapterSet) {
                setAdapter();
                isAdapterSet = true;
            }

            setUpclick();

            loadInterstitialAd();

        }
    }

    private void loadInterstitialAd() {
        InterstitialAdHelper.loadAd(requireContext());

    }

    void setUpclick(){
        aartiCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.aartiFragment);
            }
        });
        kathasCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.kathaFragment);
            }
        });
        mantraCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.mantraFragment);
            }
        });
    }

    private void setAdapter() {
        GaneshItemAdapter adapter = new GaneshItemAdapter(
                requireContext(),
                mHomeList,
                (item, position) -> {
                    handleItemClick(item);
                 });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    private void handleItemClick(GaneshItem item) {
        InterstitialAdHelper.showAdIfAvailable(requireActivity(), () -> {
            // This runs AFTER the ad is closed (or skipped)
            HomeFragmentDirections.ActionListToDetail action =
                    HomeFragmentDirections.actionListToDetail(item);
            NavHostFragment.findNavController(HomeFragment.this).navigate(action);

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
