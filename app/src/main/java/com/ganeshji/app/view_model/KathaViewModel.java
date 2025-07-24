package com.ganeshji.app.view_model;

import androidx.lifecycle.ViewModel;

import com.ganeshji.app.R;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;

import java.util.ArrayList;
import java.util.List;

public class KathaViewModel extends ViewModel {
    private final ArrayList<GaneshItem> itemList = new ArrayList<>();

    public List<GaneshItem> getItemList() {
        return itemList;
    }

    public boolean isDataLoaded() {
        return !itemList.isEmpty();
    }

    public void loadData(HomeViewModel.ViewModelStringProvider provider) {
        if (isDataLoaded()) return;

        addItem("कथा - एकदंत गणेश", provider.getString(R.string.ek_dant_katha),
                R.drawable.ekdanta_ganesha, false, false, "katha", 0);


        addItem("कथा - गणेश जन्म", provider.getString(R.string.ganesh_janam_katha_full),
                R.drawable.ganesh_katha, false, false, "katha", 0);

        addItem("पृथ्वी परिक्रमा कथा", provider.getString(R.string.katha_prithvi_parikrama),
                R.drawable.prthvi_katha, false, false, "katha", 0);

        addItem("कथा - संकट चौथ", provider.getString(R.string.katha_sankat_chauth),
                R.drawable.sankath_chauth_katha, false, false, "katha", 0);

        addItem("कथा - गणेश जी और चंद्रमा", provider.getString(R.string.katha_ganesh_chandra),
                R.drawable.chandrama_ganesh, false, false, "katha", 0);


        addItem("कथा - कुबेर और गणेश जी", provider.getString(R.string.katha_kuber_aur_ganesh),
                R.drawable.lord_kuber_with_ganesh, false, false, "katha", 0);

       }

    private void addItem(String title, String desc, int iconId, boolean isAudio, boolean isFav, String type, int audioId) {
        itemList.add(new GaneshItem().setData(title, desc, isAudio, iconId, isFav, type, audioId));
    }

    public interface ViewModelStringProvider {
        String getString(int resId);
    }
}


