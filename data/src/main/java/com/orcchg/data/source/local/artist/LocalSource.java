package com.orcchg.data.source.local.artist;

import com.orcchg.data.source.remote.artist.DataSource;
import com.orcchg.data.source.local.ICache;

public interface LocalSource extends DatabaseRepository, DataSource, ICache {
}
