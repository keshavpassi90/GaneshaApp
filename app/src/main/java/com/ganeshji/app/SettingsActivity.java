package com.ganeshji.app;


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    String[] listItems;
    Float[] fontValues = new Float[4];
    TextView mResult, versionName;
    LinearLayout fontItem, Item2, Item3,Item5, Item6, Item7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar_id);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fontValues[0]=18f;
        fontValues[1]=24f;
        fontValues[2]=30f;
        fontValues[3]=36f;

        listItems = getResources().getStringArray(R.array.fontsizes);
        mResult = findViewById(R.id.textView2);
        mResult.setText(listItems[new PrefManager(this).getFontKey()]);
        fontItem = findViewById (R.id.list_item);
        Item2 = findViewById (R.id.list_item2);
        Item3 = findViewById (R.id.list_item3);
        Item5 = findViewById (R.id.list_item5);
        Item6 = findViewById (R.id.list_item6);
        Item7 = findViewById (R.id.list_item7);
        fontItem.setOnClickListener(onButtonClick());
        Item2.setOnClickListener(onButtonClick());
        Item3.setOnClickListener(onButtonClick());
        Item5.setOnClickListener(onButtonClick());
        Item6.setOnClickListener(onButtonClick());
        Item7.setOnClickListener(onButtonClick());

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

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.list_item:
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
                        mBuilder.setTitle("Font Size");
                        Integer fontKey = new PrefManager(SettingsActivity.this).getFontKey();
                        mBuilder.setSingleChoiceItems(listItems, fontKey , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mResult.setText(listItems[i]);
                                dialogInterface.dismiss();
                                new PrefManager(SettingsActivity.this).saveFontSize(fontValues[i],i);
                            }
                        });
                        mBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        /*// Set the alert dialog yes button click listener
                        mBuilder.setPositiveButton("Yes", dialogClickListener);

                        // Set the alert dialog no button click listener
                        mBuilder.setNegativeButton("No",dialogClickListener);*/
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();

                        break;
                    case R.id.list_item2:
                        Intent sharingIntent = new Intent();
                        sharingIntent.setAction(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");

                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                        sharingIntent.putExtra(Intent.EXTRA_TEXT,
                                "Hey check out my app at: "+ Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));

                        startActivity(Intent.createChooser(sharingIntent, "Share app via"));
                        break;

                    case R.id.list_item3:
                    case R.id.list_item5:{
                        launchMarket();
                        break;
                    }
                    case R.id.list_item6:{
                        Intent i = new Intent(SettingsActivity.this, Webview.class);
                        i.putExtra(Webview.PRIVACY_URL, getString(R.string.privacy_url));
                        startActivity(i);
                        break;
                    }
                    case R.id.list_item7:
                        feedback();
                        break;
                }

            }
        };
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

