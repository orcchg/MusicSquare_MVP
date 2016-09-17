package com.orcchg.data.source.remote.yandex;

import com.orcchg.data.entity.ArtistEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import timber.log.Timber;

public class YandexCloudSource /*implements DataSource*/ {

    private final RestAdapter restAdapter;

    @Inject
    public YandexCloudSource(Retrofit retrofit) {
        this.restAdapter = retrofit.create(RestAdapter.class);
    }

    //@Override
    public List<ArtistEntity> artists() {
        try {
            Timber.i("Requesting artists from Yandex cloud...");
            return this.restAdapter.getArtists("artists.json").execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
        }
        return new ArrayList<>();
    }

    //@Override
    public ArtistEntity artist(long artistId) {
        return null;
    }
}
