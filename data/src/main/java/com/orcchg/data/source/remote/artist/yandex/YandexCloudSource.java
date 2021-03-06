package com.orcchg.data.source.remote.artist.yandex;

import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.util.LongSparseArray;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.entity.mapper.ArtistEntitySlicer;
import com.orcchg.data.source.remote.artist.ArtistDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Retrofit;
import timber.log.Timber;

@Singleton
@TargetApi(16)
public class YandexCloudSource implements ArtistDataSource {

    private final YandexRestAdapter restAdapter;

    private final LongSparseArray<ArtistEntity> artists;

    @Inject
    YandexCloudSource(Retrofit.Builder retrofit) {
        restAdapter = retrofit.baseUrl(YandexRestAdapter.ENDPOINT).build()
            .create(YandexRestAdapter.class);
        artists = new LongSparseArray<>();
    }

    @Override
    public List<SmallArtistEntity> artists() {
        try {
            Timber.d("Requesting artists from Yandex cloud...");
            List<ArtistEntity> models = restAdapter.getArtists("artists.json").execute().body();
            List<SmallArtistEntity> smallModels = new ArrayList<>(models.size());
            ArtistEntitySlicer mapper = new ArtistEntitySlicer();
            for (ArtistEntity model : models) {
                artists.put(model.getId(), model);
                smallModels.add(mapper.map(model));
            }
            return smallModels;
        } catch (IOException e) {
            Timber.e("Network error: %s", e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<SmallArtistEntity> artists(int limit, int offset) {
        Timber.w("Query parameters not supported !");
        return artists();
    }

    @Override
    public List<SmallArtistEntity> artists(@Nullable List<String> genres) {
        Timber.w("Query parameters not supported !");
        return artists();
    }

    @Override
    public List<SmallArtistEntity> artists(int limit, int offset, @Nullable List<String> genres) {
        Timber.w("Query parameters not supported !");
        return artists();
    }

    @Override
    public ArtistEntity artist(long artistId) {
        return artists.get(artistId);
    }

    @Override
    public TotalValueEntity total() {
        return new TotalValueEntity.Builder(317).build();
    }

    @Override
    public TotalValueEntity total(@Nullable List<String> genres) {
        return new TotalValueEntity.Builder(317).build();
    }
}
