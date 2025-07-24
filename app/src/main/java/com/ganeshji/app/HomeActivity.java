package com.ganeshji.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    // ── UI references ────────────────────────────────────────────────────────────
     BottomNavigationView bottomNav;
     RelativeLayout settings;
     ConstraintLayout toolbar;
     NavController        navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);           // your XML posted earlier

        // 1⃣ Grab the NavHostFragment defined in activity_home.xml
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // 2⃣ Get its NavController
        navController = navHostFragment.getNavController();

        // 3⃣ Hook BottomNavigationView to the controller
        bottomNav = findViewById(R.id.bottom_navigation);
        settings = findViewById(R.id.settings);
        toolbar = findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // (Optional) If you ever need manual tab change callbacks:
        bottomNav.setOnItemSelectedListener(item -> {
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            return handled;
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));

            }
        });

        // Listen for destination changes
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Assuming detail screen's id is R.id.detailFragment
            if (destination.getId() == R.id.detailWallpaperFragment || destination.getId() == R.id.homeDetailFragment|| destination.getId() == R.id.aartiFragment|| destination.getId() == R.id.kathaFragment|| destination.getId() == R.id.mantraFragment) {
                bottomNav.setVisibility(View.GONE); // Hide bottom nav on detail screen
                toolbar.setVisibility(View.GONE); // Hide bottom nav on detail screen
            } else {
                bottomNav.setVisibility(View.VISIBLE); // Show on other screens
                toolbar.setVisibility(View.VISIBLE); // Show on other screens
            }
        });
    }

    // ── Optional: handle Up button in the toolbar if you add one ────────────────
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}

