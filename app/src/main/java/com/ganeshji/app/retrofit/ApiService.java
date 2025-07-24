package com.ganeshji.app.retrofit;


import com.ganeshji.app.retrofit.wallpaper_model.GaneshaModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("v1/search")
    Call<GaneshaModel> searchPhotos(
            @Query("query") String query,
            @Query("page") int page,
            @Query("per_page") int perPage
    );
}