package com.orcchg.data.source.local.artist;

import com.orcchg.data.source.local.base.ICache;
import com.orcchg.data.source.remote.artist.ArtistDataSource;

public interface ArtistLocalSource extends ArtistDataSource, ArtistRepository, ICache {
}
