package com.orcchg.data.source.remote.artist.server;

import com.domain.model.Genre;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface ServerRestAdapter {

    String ENDPOINT = "http://194.190.63.108:9123/";

    @GET("/all")
    Call<List<SmallArtistEntity>> getArtists(@Query("limit") Integer limit,
        @Query("offset") Integer offset, @Query("genres") String genres);

    @GET("/genres")
    Call<List<Genre>> getGenres();

    @GET("/single")
    Call<ArtistEntity> getArtist(@Query("id") long artistId);
}
