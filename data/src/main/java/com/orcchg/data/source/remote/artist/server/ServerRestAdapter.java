package com.orcchg.data.source.remote.artist.server;

import com.domain.model.Genre;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.TotalValueEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface ServerRestAdapter {

    String ENDPOINT = "http://194.190.63.108:9123/";

    @GET("/all")
    Call<List<SmallArtistEntity>> artists(@Query("limit") Integer limit,
          @Query("offset") Integer offset, @Query("genres") String genres);

    @GET("/genres")
    Call<List<Genre>> genres();

    @GET("/single")
    Call<ArtistEntity> artist(@Query("id") long artistId);

    @GET("/total")
    Call<TotalValueEntity> total(@Query("genres") String genres);
}
