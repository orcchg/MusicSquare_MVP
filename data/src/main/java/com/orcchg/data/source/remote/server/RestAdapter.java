package com.orcchg.data.source.remote.server;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestAdapter {

    String ENDPOINT = "http://194.190.63.108:9123/";

    @GET("/all")
    Call<List<SmallArtistEntity>> getArtists();

    @GET("/single")
    Call<ArtistEntity> getArtist(@Query("id") long artistId);
}
