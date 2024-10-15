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

public class TabFour extends Fragment {
    View view;
    String[] path_4;

    TextView titleText, descriptionText;
    public TabFour(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_four,container,false);
        path_4 = getResources().getStringArray(R.array.path_4);
        descriptionText = view.findViewById(R.id.descriptionTextView);
        titleText = view.findViewById(R.id.titleTextView);
        StringBuilder builder = new StringBuilder();
        for (String details : path_4) {
            builder.append(details + "\n");
        }
        descriptionText.setText(builder.toString());
        Context context = getActivity();
        descriptionText.setTextSize(new PrefManager(context).getFontSize());
        titleText.setText(R.string.tab_view4);
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
