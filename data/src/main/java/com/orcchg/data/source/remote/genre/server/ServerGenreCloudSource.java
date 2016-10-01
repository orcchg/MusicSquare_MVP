package com.orcchg.data.source.remote.genre.server;

import android.support.annotation.Nullable;

import com.orcchg.data.entity.GenreEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.exception.NetworkException;
import com.orcchg.data.source.remote.genre.GenreDataSource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import timber.log.Timber;

public class ServerGenreCloudSource implements GenreDataSource {

    private final ServerGenreRestAdapter restAdapter;

    @Inject
    ServerGenreCloudSource(Retrofit.Builder retrofit) {
        restAdapter = retrofit.baseUrl(ServerGenreRestAdapter.ENDPOINT).build()
                .create(ServerGenreRestAdapter.class);
    }

    @Override
    public List<GenreEntity> genres() {
        try {
            Timber.d("Requesting genres from cloud...");
            return restAdapter.genres().execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
            throw new NetworkException();
        }
    }

    @Nullable
    @Override
    public GenreEntity genre(String name) {
        try {
            Timber.d("Requesting genre from cloud...");
            return restAdapter.genre(name).execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
            throw new NetworkException();
        }
    }

    @Override
    public TotalValueEntity total() {
//        try {
//            Timber.d("Requesting total genres count from cloud...");
//            return restAdapter.total().execute().body();
//        } catch (IOException e) {
//            Timber.e("Network error: %s", e);
//            throw new NetworkException();
//        }
        // TODO: impl server method
        return new TotalValueEntity(24);
    }
}
