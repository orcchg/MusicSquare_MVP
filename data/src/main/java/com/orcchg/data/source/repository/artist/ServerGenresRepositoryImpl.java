package com.orcchg.data.source.repository.artist;

import com.domain.model.Genre;
import com.domain.repository.IGenresRepository;
import com.orcchg.data.source.remote.artist.GenresDataSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ServerGenresRepositoryImpl implements IGenresRepository {

    private final GenresDataSource cloudSource;

    @Inject
    ServerGenresRepositoryImpl(GenresDataSource genresDataSource) {
        this.cloudSource = genresDataSource;
        // TODO: inject local cache
    }

    @Override
    public List<Genre> genres() {
        return this.cloudSource.genres();
    }

    @Override
    public boolean clear() {
        return true;
    }
}
