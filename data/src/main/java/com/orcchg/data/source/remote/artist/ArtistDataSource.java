package com.orcchg.data.source.remote.artist;

import android.support.annotation.Nullable;

import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;

import java.util.List;

public interface ArtistDataSource {

    List<SmallArtistEntity> artists();

    @Nullable
    ArtistEntity artist(long artistId);
}
