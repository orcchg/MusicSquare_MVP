package com.orcchg.data.source.local.genre;

import com.orcchg.data.entity.GenreEntity;

import java.util.List;

public interface GenreRepository {

    boolean hasGenre(String name);
    void addGenre(GenreEntity genre);
    void addGenres(List<GenreEntity> genres);
    void updateGenres(List<GenreEntity> genres);
    void removeGenres(GenreSpecification specification);
    List<GenreEntity> queryGenres(GenreSpecification specification);
}
