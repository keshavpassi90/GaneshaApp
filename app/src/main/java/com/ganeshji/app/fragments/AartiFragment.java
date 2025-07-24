package com.ganeshji.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ganeshji.app.R;
import com.ganeshji.app.adapter.GaneshItemAdapter;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;
import com.ganeshji.app.utils.InterstitialAdHelper;
import com.ganeshji.app.view_model.AartiViewModel;
import com.ganeshji.app.view_model.HomeViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class AartiFragment  extends Fragment {
    private View rootView; // 👈 persistent root view
    private boolean isAdapterSet = false; // 👈 prevent resetting adapter
    private AdView mAdView;

    RelativeLayout backRL;
    RecyclerView aartiRecyclerRV;
    private ArrayList<GaneshItem> mHomeList = new ArrayList<>();
    private AartiViewModel aartiViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_aarti, container, false);
        }
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setUpIds(view);
        setUpClicks();

        aartiViewModel = new ViewModelProvider(requireActivity()).get(AartiViewModel.class);

        if (!aartiViewModel.isDataLoaded()) {
            aartiViewModel.loadData(resId -> getString(resId));
        }

        mHomeList = new ArrayList<>(aartiViewModel.getItemList());

        if (!isAdapterSet) {
            setAdapter();
            isAdapterSet = true;
        }
    }

    // setup ids
    void setUpIds(View view){

        backRL = view.findViewById(R.id.backRL);
        aartiRecyclerRV = view.findViewById(R.id.aartiRecyclerRV);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest); // ✅ Load ad into XML AdView
        InterstitialAdHelper.loadAd(requireContext());

    }

    // setup Clicks
    void setUpClicks(){

        backRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AartiFragment.this).popBackStack();
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

        aartiRecyclerRV.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        aartiRecyclerRV.setAdapter(adapter);
    }

    private void handleItemClick(GaneshItem item) {
        InterstitialAdHelper.showAdIfAvailable(requireActivity(), () -> {
            // This runs AFTER the ad is closed (or skipped)
            AartiFragmentDirections.ActionListToDetail action =
                    AartiFragmentDirections.actionListToDetail(item);
            NavHostFragment.findNavController(this).navigate(action);

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

