package com.orcchg.data.source.remote.yandex;

import com.orcchg.data.entity.ArtistEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Node class for API requests.
 */
public interface RestAdapter {

    String ENDPOINT = "http://download.cdn.yandex.net/";

    @GET("/mobilization-2016/{path}")
    Call<List<ArtistEntity>> getArtists(@Path("path") String path);
}
