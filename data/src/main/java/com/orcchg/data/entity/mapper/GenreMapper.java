package com.orcchg.data.entity.mapper;

import com.domain.model.Genre;
import com.domain.model.mapper.Mapper;
import com.orcchg.data.entity.GenreEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GenreMapper implements Mapper<GenreEntity, Genre> {

    @Inject
    GenreMapper() {
    }

    @Override
    public Genre map(GenreEntity object) {
        return new Genre.Builder(object.getName())
                .setGenres(object.getGenres())
                .build();
    }

    @Override
    public List<Genre> map(List<GenreEntity> list) {
        List<Genre> mapped = new ArrayList<>();
        for (GenreEntity entity : list) {
            mapped.add(map(entity));
        }
        return mapped;
    }
}
