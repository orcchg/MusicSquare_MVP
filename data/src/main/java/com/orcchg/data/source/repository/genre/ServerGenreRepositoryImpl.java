package com.orcchg.data.source.repository.genre;

import com.domain.model.Genre;
import com.domain.model.TotalValue;
import com.domain.repository.IGenreRepository;
import com.orcchg.data.entity.GenreEntity;
import com.orcchg.data.entity.TotalValueEntity;
import com.orcchg.data.entity.mapper.GenreMapper;
import com.orcchg.data.entity.mapper.TotalValueMapper;
import com.orcchg.data.source.local.genre.GenreLocalSource;
import com.orcchg.data.source.remote.genre.GenreDataSource;
import com.orcchg.data.source.repository.RepoUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;

@Singleton
public class ServerGenreRepositoryImpl implements IGenreRepository {

    private final GenreDataSource cloudSource;
    private final GenreLocalSource localSource;
    private final GenreMapper genreMapper;
    private final TotalValueMapper totalValueMapper;

    private @RepoUtils.SourceType int source;

    @Inject
    ServerGenreRepositoryImpl(GenreDataSource cloudSource, GenreLocalSource localSource,
                              GenreMapper genreMapper, TotalValueMapper totalValueMapper) {
        this.cloudSource = cloudSource;
        this.localSource = localSource;
        this.genreMapper = genreMapper;
        this.totalValueMapper = totalValueMapper;
    }

    @DebugLog @Override
    public List<Genre> genres() {
        return processListOfEntities(getDataSource().genres());
    }

    @DebugLog @Override
    public Genre genre(String name) {
        GenreEntity genreEntity = getDataSource(name).genre(name);
        if (source == RepoUtils.SOURCE_REMOTE &&
            (checkCacheStaled() || !localSource.hasGenre(name))) {
            List<GenreEntity> genreEntities = new ArrayList<>();
            genreEntities.add(genreEntity);
            localSource.updateGenres(genreEntities);
        }
        return genreMapper.map(genreEntity);
    }

    @DebugLog @Override
    public boolean clear() {
        localSource.clear();
        return true;
    }

    @DebugLog @Override
    public TotalValue total() {
        // total items count is always fetched from remote cloud to be actual
        TotalValueEntity totalValueEntity = cloudSource.total();
        return totalValueMapper.map(totalValueEntity);
    }

    /* Internal */
    // --------------------------------------------------------------------------------------------
    @DebugLog
    private boolean checkCacheStaled() {
        return localSource.isEmpty() || localSource.isExpired();
    }

    @DebugLog
    private GenreDataSource getDataSource() {
        return getDataSource(null);
    }

    @DebugLog
    private GenreDataSource getDataSource(String name) {
        if (checkCacheStaled() || !localSource.hasGenre(name)) {
            source = RepoUtils.SOURCE_REMOTE;
            return cloudSource;
        } else {
            source = RepoUtils.SOURCE_LOCAL;
            return localSource;
        }
    }

    private List<Genre> processListOfEntities(List<GenreEntity> data) {
        if (source == RepoUtils.SOURCE_REMOTE) {
            localSource.updateGenres(data);
        }
        List<Genre> genres = new ArrayList<>();
        for (GenreEntity entity : data) {
            genres.add(genreMapper.map(entity));
        }
        return genres;
    }
}
