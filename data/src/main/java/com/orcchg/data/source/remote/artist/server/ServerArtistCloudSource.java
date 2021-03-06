package com.orcchg.data.source.remote.artist.server;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.exception.NetworkException;
import com.orcchg.data.source.remote.artist.ArtistDataSource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import retrofit2.Retrofit;
import timber.log.Timber;

@Singleton
public class ServerArtistCloudSource implements ArtistDataSource {

    private final ServerArtistRestAdapter restAdapter;

    @Inject
    ServerArtistCloudSource(Retrofit.Builder retrofit) {
        restAdapter = retrofit.baseUrl(ServerArtistRestAdapter.ENDPOINT).build()
            .create(ServerArtistRestAdapter.class);
    }

    @DebugLog @Override
    public List<SmallArtistEntity> artists() {
        return artists(-1, 0);
    }

    @DebugLog @Override
    public List<SmallArtistEntity> artists(int limit, int offset) {
        return artists(limit, offset, null);
    }

    @DebugLog @Override
    public List<SmallArtistEntity> artists(@Nullable List<String> genres) {
        return artists(-1, 0, genres);
    }

    @DebugLog @Override
    public List<SmallArtistEntity> artists(int limit, int offset, @Nullable List<String> genres) {
        try {
            Integer Limit = limit == -1 ? null : limit;
            Integer Offset = offset == 0 ? null : offset;
            String genresQuery = genres == null || genres.isEmpty() ? null : TextUtils.join(",", genres);
            Timber.d("Requesting artists from Server cloud...");
            return restAdapter.artists(Limit, Offset, genresQuery).execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
            throw new NetworkException();
        }
    }

    @DebugLog @Nullable @Override
    public ArtistEntity artist(long artistId) {
        try {
            Timber.d("Requesting artist from cloud...");
            return restAdapter.artist(artistId).execute().body();
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
            throw new NetworkException();
        }
    }

    @DebugLog @Override
    public TotalValueEntity total() {
        return total(null);
    }

    @DebugLog @Override
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
