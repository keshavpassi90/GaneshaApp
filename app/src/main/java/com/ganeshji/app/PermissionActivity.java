package com.ganeshji.app;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private AppOpenAd appOpenAd;
    private boolean isDialogOpen = false;
    private boolean isLoadingAd = false;
    private static final String[] storage_permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String[] storage_permissions_33 = { Manifest.permission.POST_NOTIFICATIONS};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_permission);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // Configuration or initialization logic if needed
            }
        });

        // Setting up test device IDs
        List<String> testDevices = new ArrayList<>();
        testDevices.add(getResources().getString(R.string.testDevice));

        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);


        // Load the App Open Ad
        loadAppOpenAd();

        Button permissionBt =findViewById(R.id.permissionBt);

        permissionBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Code to execute when the button is clicked
                // For example, show a toast or change activity
                // Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
         if(!checkPermissions()){
             requestPermissions();
         }else{
             startMainActivity();
         }

            }
        } );
    }


    private void loadAppOpenAd() {
        if (isLoadingAd) return;

        isLoadingAd = true;
        AdRequest adRequest = new AdRequest.Builder().build();

        AppOpenAd.load(this, getResources().getString(R.string.app_open_id), adRequest,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        Log.e("ERROR", "App Open Ad failed to load: " + adError.getMessage());
                        isLoadingAd = false;
                    }
                });
    }

    // Handle the ad logic based on ad status
    private void handleAdLogic() {
        if (appOpenAd == null) {
            startMainActivity();
        } else {
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    appOpenAd = null; // Clear the ad reference
                    startMainActivity(); // Proceed to the main activity
                    loadAppOpenAd(); // Load another ad for the next time
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    Log.e("ERROR", "Ad failed to show: " + adError.getMessage());
                    appOpenAd = null; // Clear the ad reference
                    startMainActivity(); // Proceed to the main activity
                }
            });
            appOpenAd.show(PermissionActivity.this);
        }
    }

    private void startMainActivity() {

            Intent mainIntent = new Intent(PermissionActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();


    }

    private String[] getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return storage_permissions_33;
        } else {
            return storage_permissions;
        }
    }

    private boolean checkPermissions() {
        String[] permissions = getPermissions();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        String[] permissions = getPermissions();
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
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
                handleAdLogic();
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

}