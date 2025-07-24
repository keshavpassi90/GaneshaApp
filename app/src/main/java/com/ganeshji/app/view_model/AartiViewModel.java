package com.ganeshji.app.view_model;

import androidx.lifecycle.ViewModel;

import com.ganeshji.app.R;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;

import java.util.ArrayList;
import java.util.List;

public class AartiViewModel extends ViewModel {
    private final ArrayList<GaneshItem> itemList = new ArrayList<>();

    public List<GaneshItem> getItemList() {
        return itemList;
    }

    public boolean isDataLoaded() {
        return !itemList.isEmpty();
    }

    public void loadData(HomeViewModel.ViewModelStringProvider provider) {
        if (isDataLoaded()) return;

        addItem("आरती - जय गणेश देवा", provider.getString(R.string.jai_ganesh_deva_aarti),
                R.drawable.ganesh_aarti_one, true, false, "aarti", R.raw.jai_ganesh_deva_aarti);


        addItem("आरती - गणेश जी की", provider.getString(R.string.aarti_jai_ganeshji_ki),
                R.drawable.aarti_ganesh_ji_ki, true, false, "aarti", R.raw.aarti_ganesh_ji_ki);


        addItem("आरती - सुखकर्ता दुखहर्ता", provider.getString(R.string.aarti_sukhkarta_dukhharta),
                R.drawable.sukh_karta_icon, true, false, "aarti", R.raw.aarti_sukhkarta_dukhharta);

        addItem("शेंदूर लाल चढ़ायो आरती", provider.getString(R.string.aarti_shendur_lal_chadhayo),
                R.drawable.shindur_lal_icon, true, false, "aarti", R.raw.shendur_lal);

      }

    private void addItem(String title, String desc, int iconId, boolean isAudio, boolean isFav, String type, int audioId) {
        itemList.add(new GaneshItem().setData(title, desc, isAudio, iconId, isFav, type, audioId));
    }

    public interface ViewModelStringProvider {
        String getString(int resId);
    }
}

