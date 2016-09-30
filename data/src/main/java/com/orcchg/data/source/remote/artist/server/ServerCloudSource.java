package com.orcchg.data.source.remote.artist.server;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.domain.model.Genre;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.exception.NetworkException;
import com.orcchg.data.source.remote.artist.ArtistDataSource;
import com.orcchg.data.source.remote.artist.GenresDataSource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Retrofit;
import timber.log.Timber;

@Singleton
public class ServerCloudSource implements ArtistDataSource, GenresDataSource {

    private final ServerRestAdapter restAdapter;

    @Inject
    ServerCloudSource(Retrofit.Builder retrofit) {
        restAdapter = retrofit.baseUrl(ServerRestAdapter.ENDPOINT).build()
            .create(ServerRestAdapter.class);
    }

    @Override
    public List<SmallArtistEntity> artists() {
        return artists(-1, 0);
    }

    @Override
    public List<SmallArtistEntity> artists(int limit, int offset) {
        return artists(limit, offset, null);
    }

    @Override
    public List<SmallArtistEntity> artists(@Nullable List<String> genres) {
        return artists(-1, 0, genres);
    }

    @Override
    public List<SmallArtistEntity> artists(int limit, int offset, @Nullable List<String> genres) {
        try {
            Integer Limit = limit == -1 ? null : limit;
            Integer Offset = offset == 0 ? null : offset;
            String genresQuery = genres == null || genres.isEmpty() ? null : TextUtils.join(",", genres);
            Timber.i("Requesting artists from Server cloud...");
            return restAdapter.artists(Limit, Offset, genresQuery).execute().body();
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
            return restAdapter.artist(artistId).execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
            throw new NetworkException();
        }
    }

    @Override
    public List<Genre> genres() {
        try {
            Timber.d("Requesting genres from cloud...");
            return restAdapter.genres().execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
            throw new NetworkException();
        }
    }

    @Override
    public TotalValueEntity total() {
        return total(null);
    }

    @Override
    public TotalValueEntity total(@Nullable List<String> genres) {
        try {
            Timber.d("Requesting total artists count from cloud...");
            String genresQuery = genres == null || genres.isEmpty() ? null : TextUtils.join(",", genres);
            return restAdapter.total(genresQuery).execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
            throw new NetworkException();
        }
    }
}
