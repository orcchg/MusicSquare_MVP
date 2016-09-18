package com.orcchg.data.entity.mapper;

import com.domain.model.mapper.Mapper;
import com.orcchg.data.entity.ArtistEntity;
import com.orcchg.data.entity.SmallArtistEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ArtistEntitySlicer implements Mapper<ArtistEntity, SmallArtistEntity> {

    @Inject
    public ArtistEntitySlicer() {
    }

    @Override
    public SmallArtistEntity map(ArtistEntity object) {
        return new SmallArtistEntity.Builder(object.getId(), object.getName())
                .setCover(object.getCoverSmall())
                .build();
    }

    @Override
    public List<SmallArtistEntity> map(List<ArtistEntity> list) {
        List<SmallArtistEntity> mapped = new ArrayList<>();
        for (ArtistEntity entity : list) {
            mapped.add(map(entity));
        }
        return mapped;
    }
}
