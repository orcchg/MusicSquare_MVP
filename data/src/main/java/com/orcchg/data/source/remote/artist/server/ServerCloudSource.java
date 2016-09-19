package com.orcchg.data.source.remote.artist.server;

import android.support.annotation.Nullable;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.exception.NetworkException;
import com.orcchg.data.source.remote.artist.ArtistDataSource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import timber.log.Timber;

public class ServerCloudSource implements ArtistDataSource {

    private final ServerRestAdapter restAdapter;

    @Inject
    ServerCloudSource(Retrofit retrofit) {
        this.restAdapter = retrofit.create(ServerRestAdapter.class);
    }

    @Override
    public List<SmallArtistEntity> artists() {
        try {
            Timber.i("Requesting artists from Server cloud...");
            return this.restAdapter.getArtists().execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
            throw new NetworkException();
        }
    }

    @Nullable
    @Override
    public ArtistEntity artist(long artistId) {
        try {
            Timber.d("Requesting artist from cloud...");
            return this.restAdapter.getArtist(artistId).execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
        }
        return null;
    }
}