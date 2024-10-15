package com.ganeshji.app;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabOne extends Fragment {
    View view;
    String[] path_1;


    TextView titleText, descriptionText;
    public TabOne(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_one,container,false);
        path_1 = getResources().getStringArray(R.array.path_1);
        descriptionText = view.findViewById(R.id.descriptionTextView);
        titleText = view.findViewById(R.id.titleTextView);
        StringBuilder builder = new StringBuilder();
        for (String details : path_1) {
            builder.append(details + "\n");
        }
        descriptionText.setText(builder.toString());
        Context context = getActivity();
        descriptionText.setTextSize(new PrefManager(context).getFontSize());
        titleText.setText("गजानन श्री गणराय");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = getActivity();
        descriptionText.setTextSize(new PrefManager(context).getFontSize());
    }


    protected void increaseFont()
    {
        descriptionText.setTextSize(0, descriptionText.getTextSize() + 2.0f);
    }
    protected void decreaseFont()
    {
        descriptionText.setTextSize(0, descriptionText.getTextSize() - 2.0f);
    }
}
