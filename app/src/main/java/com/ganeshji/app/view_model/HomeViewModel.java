package com.ganeshji.app.view_model;


import androidx.lifecycle.ViewModel;

import com.ganeshji.app.R;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final ArrayList<GaneshItem> itemList = new ArrayList<>();

    public List<GaneshItem> getItemList() {
        return itemList;
    }

    public boolean isDataLoaded() {
        return !itemList.isEmpty();
    }

    public void loadData(ViewModelStringProvider provider) {
        if (isDataLoaded()) return;

        addItem("आरती - जय गणेश देवा", provider.getString(R.string.jai_ganesh_deva_aarti),
                R.drawable.ganesh_aarti_one, true, false, "aarti", R.raw.jai_ganesh_deva_aarti);

        addItem("गणपति मूल मंत्र", provider.getString(R.string.mantra_om_gam),
                R.drawable.om_ganpati_namah, true, false, "mantra", R.raw.om_ganpatye_namah);

        addItem("कथा - एकदंत गणेश", provider.getString(R.string.ek_dant_katha),
                R.drawable.ekdanta_ganesha, false, false, "katha", 0);

        addItem("आरती - गणेश जी की", provider.getString(R.string.aarti_jai_ganeshji_ki),
                R.drawable.aarti_ganesh_ji_ki, true, false, "aarti", R.raw.aarti_ganesh_ji_ki);

        addItem("मंत्र - वक्रतुण्ड महाकाय", provider.getString(R.string.mantra_vakratunda_mahakaya),
                R.drawable.vakratun_mahakaye_icon, true, false, "aarti", R.raw.vakratund_mahakaye);

        addItem("आरती - सुखकर्ता दुखहर्ता", provider.getString(R.string.aarti_sukhkarta_dukhharta),
                R.drawable.sukh_karta_icon, true, false, "aarti", R.raw.aarti_sukhkarta_dukhharta);

        addItem("कथा - गणेश जन्म", provider.getString(R.string.ganesh_janam_katha_full),
                R.drawable.ganesh_katha, false, false, "katha", 0);

        addItem("पृथ्वी परिक्रमा कथा", provider.getString(R.string.katha_prithvi_parikrama),
                R.drawable.prthvi_katha, false, false, "katha", 0);

        addItem("कथा - संकट चौथ", provider.getString(R.string.katha_sankat_chauth),
                R.drawable.sankath_chauth_katha, false, false, "katha", 0);

        addItem("कथा - गणेश जी और चंद्रमा", provider.getString(R.string.katha_ganesh_chandra),
                R.drawable.chandrama_ganesh, false, false, "katha", 0);

        addItem("शेंदूर लाल चढ़ायो आरती", provider.getString(R.string.aarti_shendur_lal_chadhayo),
                R.drawable.shindur_lal_icon, true, false, "aarti", R.raw.shendur_lal);

        addItem("कथा - कुबेर और गणेश जी", provider.getString(R.string.katha_kuber_aur_ganesh),
                R.drawable.lord_kuber_with_ganesh, false, false, "katha", 0);

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

