package com.ganeshji.app.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import com.ganeshji.app.R;
import com.ganeshji.app.adapter.ImageGridAdapter;
import com.ganeshji.app.retrofit.NetworkUtils;
import com.ganeshji.app.retrofit.RetrofitClient;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshaModel;
import com.ganeshji.app.retrofit.wallpaper_model.PhotosItem;
import com.ganeshji.app.utils.AppProgress;
import com.ganeshji.app.utils.DownloadImageTask;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WallPaperFragment extends Fragment {
    private static final int REQUEST_WRITE_PERMISSION = 101;
    private String fileName ="";
    private String selectedImageURl ="";
    private AppProgress progress;
    private ProgressBar loader;
    private RecyclerView recyclerView;
    private ImageGridAdapter adapter;
    private List<PhotosItem> imageUrls;
    private boolean isLoading = false;
    private int currentPage = 1;
    private final int visibleThreshold = 2;
    private boolean isPaginationWork = false;
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment_home layout
        return inflater.inflate(R.layout.fragment_wallpaper, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpIds(view);
        fetchGaneshImage();
    }

    // setup ids
    void setUpIds(View view){
        progress = new AppProgress(requireContext());
        // 1. Find RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        loader = view.findViewById(R.id.loader);
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest); // ✅ Load ad into XML AdView

    }

    // fetch API to get the wallpaper
    private void fetchGaneshImage() {
        if (!NetworkUtils.isInternetAvailable(getActivity())) {
            showNoInternetDialog();
            return;
        }
        if(isLoading == false){
            progress.show();
        }

        RetrofitClient.getApi()
                .searchPhotos("ganesh", currentPage, 10)
                .enqueue(new Callback<GaneshaModel>() {
                    @Override
                    public void onResponse(Call<GaneshaModel> call, Response<GaneshaModel> res) {
                        if(isLoading == false){
                            progress.dismiss(); // ✅ Hide loader
                        }

                        if (res.isSuccessful() && res.body() != null ) {
                            GaneshaModel model = res.body();
                            if(model.getNextPage()==null){
                                isPaginationWork =true;
                            }

                            if(isLoading == false){
                                imageUrls = model.getPhotos();
                                setUpAdapter();
                            }else{
                                loader .setVisibility(View.GONE);
                                imageUrls.addAll(model.getPhotos());
                                adapter.notifyDataSetChanged();
                                isLoading=false;

                            }


                        } else {
                            toast("Empty or error: " + res.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<GaneshaModel> call, Throwable throwable) {
                        if(isLoading == false){
                            progress.dismiss(); // ✅ Hide loader
                        }
                        toast("Failed: " + throwable.getMessage());
                    }


                });
    }

    private void toast(String m) {
        Toast.makeText(getActivity(), "Error: " + m, Toast.LENGTH_SHORT).show();

    }

    // set up adapter
    void setUpAdapter(){
// 2. Set layout manager for 2-column grid
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        // 4. Initialize Adapter with click listener
        adapter = new ImageGridAdapter(getActivity(), imageUrls, new ImageGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String imageUrl, int position,boolean isDownload) {
                if(isDownload==true){
// If you use AndroidX Lifecycle and Coroutines support in Java (or run on background thread yourself):
                    Calendar calendar = Calendar.getInstance();
                     fileName ="Ganesha"+calendar.getTimeInMillis()+".png";
                     selectedImageURl=imageUrl;
                    checkPermissionAndDownload();
                }else{
                    // Inside your Fragment method (like onViewCreated or click listener)
                    NavDirections action = WallPaperFragmentDirections.actionWallpaperFragmentToDetailWallpaperFragment(imageUrl);
                    NavHostFragment.findNavController(WallPaperFragment.this).navigate(action);
                }

            }
        });

        // 5. Set adapter to RecyclerView
        recyclerView.setAdapter(adapter);


        // 6. pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isPaginationWork == false) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager == null) return;

                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItemPosition + visibleThreshold)) {
                        // Load more
                        isLoading = true;
                        loader .setVisibility(View.VISIBLE);
                    loadMoreImages();

                    }
                }
            }
        });
    }

    private void loadMoreImages() {
        // Simulate loading delay (use real API in production)
        new Handler().postDelayed(() -> {
            currentPage++;
            fetchGaneshImage();

        }, 2000);
    }

    // show alert
    private void showNoInternetDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("No Internet")
                .setMessage("Please check your connection and try again.")
                .setPositiveButton("Retry", (dialog, which) -> fetchGaneshImage())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkPermissionAndDownload() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            } else {
                startDownload();
            }
        } else {
            startDownload();
        }
    }

    private void startDownload() {
        new DownloadImageTask(requireContext(), fileName).execute(selectedImageURl);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload();
            } else {
                showSettingsDialog();
            }
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Required")
                .setMessage("Storage permission is required to save images. Please enable it in settings.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
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
