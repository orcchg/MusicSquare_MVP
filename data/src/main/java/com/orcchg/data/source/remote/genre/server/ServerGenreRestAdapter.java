package com.orcchg.data.source.remote.genre.server;

import com.orcchg.data.entity.GenreEntity;
import com.orcchg.data.entity.TotalValueEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface ServerGenreRestAdapter {

    String ENDPOINT = "http://194.190.63.108:9123/";

    @GET("/genres")
    Call<List<GenreEntity>> genres();

    @GET("/genre")
    Call<GenreEntity> genre(@Query("name") String name);

    @GET("/total_genres")
    Call<TotalValueEntity> total();
}
