package com.ganeshji.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefsHelper {
    private static final String PREF_NAME = "ganesh_prefs";
    private static final String KEY_FAV_LIST = "favorite_list";

    public static void saveFavorites(Context context, List<GaneshItem> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(KEY_FAV_LIST, json);
        editor.apply();
    }

    public static ArrayList<GaneshItem> loadFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FAV_LIST, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<ArrayList<GaneshItem>>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}
