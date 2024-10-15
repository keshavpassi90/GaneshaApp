package com.ganeshji.app;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TabSeven extends Fragment {
    View view;
    String[] path_7;

    TextView titleText, descriptionText;
    public TabSeven(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_seven,container,false);
        path_7 = getResources().getStringArray(R.array.path_7);
        descriptionText = view.findViewById(R.id.descriptionTextView);
        titleText = view.findViewById(R.id.titleTextView);
        StringBuilder builder = new StringBuilder();
        for (String details : path_7) {
            builder.append(details + "\n");
        }
        descriptionText.setText(builder.toString());
        Context context = getActivity();
        descriptionText.setTextSize(new PrefManager(context).getFontSize());
        titleText.setText(R.string.tab_view7);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = getActivity();
        descriptionText.setTextSize(new PrefManager(context).getFontSize());
    }
}
