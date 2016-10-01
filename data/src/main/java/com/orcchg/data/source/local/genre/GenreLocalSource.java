package com.orcchg.data.source.local.genre;

import com.orcchg.data.source.local.base.ICache;
import com.orcchg.data.source.remote.genre.GenreDataSource;

public interface GenreLocalSource extends GenreDataSource, GenreRepository, ICache {
}
