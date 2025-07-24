package com.ganeshji.app;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class SettingActivity extends AppCompatActivity {
RelativeLayout backRL;

    TextView mResult, versionName;
    LinearLayout fontItem, Item2, Item3,Item5, Item6, Item7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        setUpIds();
        setUpClicks();
    }

    // set up view ids
    void setUpIds(){
        backRL=findViewById(R.id.backRL);
        Item2 = findViewById (R.id.list_item2);
        Item3 = findViewById (R.id.list_item3);
        Item5 = findViewById (R.id.list_item5);
        Item6 = findViewById (R.id.list_item6);
        Item7 = findViewById (R.id.list_item7);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(pInfo!=null){
            versionName = findViewById(R.id.textView9);
            versionName.setText(pInfo.versionName);
        }

    }

    // set up clicks
    void setUpClicks(){
        backRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Item5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMarket();
            }
        });
        Item7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback();            }
        });
        Item6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingActivity.this, Webview.class);
                i.putExtra(Webview.PRIVACY_URL, getString(R.string.privacy_url));
                startActivity(i);
            }
        });
     Item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent();
                sharingIntent.setAction(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                sharingIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: "+ Uri.parse("http://play.google.com/store/apps/details?id=com.ganeshji.app" ));

                startActivity(Intent.createChooser(sharingIntent, "Share app via"));            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));

            showToast("unable to find market app");
        }
    }

    private void feedback() {
        Intent Email = new Intent(Intent.ACTION_SENDTO);
        //Email.setType("message/rfc822");
        //Email.setData(Uri.parse("mailto:"+getResources().getString(R.string.supportEmail)));
        //Email.putExtra(Intent.EXTRA_EMAIL, );
        Email.setData(Uri.parse("mailto:"));
        Email.putExtra(Intent.EXTRA_EMAIL  , new String[] { getResources().getString(R.string.supportEmail) });
        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "");
        startActivity(Intent.createChooser(Email, "Send Feedback:"));
    }

}