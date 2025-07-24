package com.ganeshji.app.fragments;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ganeshji.app.R;
import com.ganeshji.app.adapter.FavouriteAdapter;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;
import com.ganeshji.app.utils.InterstitialAdHelper;
import com.ganeshji.app.utils.PrefsHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
ConstraintLayout emptyCL;
RecyclerView favoritesRV;
    ArrayList<GaneshItem> mFavoriteList =new ArrayList<>();
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpIds(view);
        mFavoriteList.clear();
        mFavoriteList = PrefsHelper.loadFavorites(requireContext());

        // check favourite list empty or not
        if(mFavoriteList.isEmpty()){
            emptyCL.setVisibility(View.VISIBLE);
            favoritesRV.setVisibility(View.GONE);
        }else {
            emptyCL.setVisibility(View.GONE);
            favoritesRV.setVisibility(View.VISIBLE);
            // set up adapter
            setAdapter();
        }
    }

    // setUp ids
    void setUpIds(View view){
        emptyCL=view.findViewById(R.id.emptyCL);
        favoritesRV=view.findViewById(R.id.favoritesRV);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest); // ✅ Load ad into XML AdView
        InterstitialAdHelper.loadAd(requireContext());

    }

    // set up Adapter
    private void setAdapter() {
        FavouriteAdapter adapter = new FavouriteAdapter(
                requireContext(),
                mFavoriteList,
                (item, position) -> {
                    handleItemClick(item);
                    });

        favoritesRV.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        favoritesRV.setAdapter(adapter);
    }


    private void handleItemClick(GaneshItem item) {
        InterstitialAdHelper.showAdIfAvailable(requireActivity(), () -> {
            // This runs AFTER the ad is closed (or skipped)
            FavoritesFragmentDirections.ActionListToDetail action =
                    FavoritesFragmentDirections.actionListToDetail(item);
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
