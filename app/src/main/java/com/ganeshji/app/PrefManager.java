package com.ganeshji.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class for Shared Preference
 */
public class PrefManager {

    Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    public void saveFontSize(Float fontSize,int fontKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FontDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("fontSize", fontSize);
        editor.putInt("fontKey", fontKey);
        editor.commit();
    }

    public Float getFontSize() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FontDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("fontSize", 24.0f);
    }
    public Integer getFontKey() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FontDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("fontKey", 1);
    }
}
