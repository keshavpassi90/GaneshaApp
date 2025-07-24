package com.ganeshji.app.view_model;

import androidx.lifecycle.ViewModel;

import com.ganeshji.app.R;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;

import java.util.ArrayList;
import java.util.List;

public class MantraViewModel extends ViewModel {
    private final ArrayList<GaneshItem> itemList = new ArrayList<>();

    public List<GaneshItem> getItemList() {
        return itemList;
    }

    public boolean isDataLoaded() {
        return !itemList.isEmpty();
    }

    public void loadData(HomeViewModel.ViewModelStringProvider provider) {
        if (isDataLoaded()) return;

        addItem("गणपति मूल मंत्र", provider.getString(R.string.mantra_om_gam),
                R.drawable.om_ganpati_namah, true, false, "mantra", R.raw.om_ganpatye_namah);


        addItem("मंत्र - वक्रतुण्ड महाकाय", provider.getString(R.string.mantra_vakratunda_mahakaya),
                R.drawable.vakratun_mahakaye_icon, true, false, "aarti", R.raw.vakratund_mahakaye);

        addItem("मंत्र - एकदन्ताय", provider.getString(R.string.mantra_ekadantaya),
                R.drawable.ik_antaya_di_mahi_icon, true, false, "mantra", R.raw.ek_dantaya_di_mahi);

        addItem("मंत्र - गणेश गायत्री मंत्र", provider.getString(R.string.mantra_ganesh_gayatri),
                R.drawable.ganesh_mantra_icon_new, true, false, "mantra", R.raw.ganesh_gyatri_mantra);
    }

    private void addItem(String title, String desc, int iconId, boolean isAudio, boolean isFav, String type, int audioId) {
        itemList.add(new GaneshItem().setData(title, desc, isAudio, iconId, isFav, type, audioId));
    }

    public interface ViewModelStringProvider {
        String getString(int resId);
    }
}


