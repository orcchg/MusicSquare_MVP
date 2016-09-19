package com.orcchg.data.entity.mapper;

import com.domain.model.Artist;
import com.domain.model.mapper.Mapper;
import com.orcchg.data.entity.ArtistEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ArtistMapper implements Mapper<ArtistEntity, Artist> {

    @Inject
    ArtistMapper() {
    }

    @Override
    public Artist map(ArtistEntity object) {
        return new Artist.Builder(object.getId(), object.getName())
                .setCoverSmall(object.getCoverSmall())
                .setCoverLarge(object.getCoverLarge())
                .setGenres(object.getGenres())
                .setTracksCount(object.getTracksCount())
                .setAlbumsCount(object.getAlbumsCount())
                .setDescription(object.getDescription())
                .setWebLink(object.getWebLink())
                .build();
    }

    @Override
    public List<Artist> map(List<ArtistEntity> list) {
        List<Artist> mapped = new ArrayList<>();
        for (ArtistEntity entity : list) {
            mapped.add(map(entity));
        }
        return mapped;
    }
}
