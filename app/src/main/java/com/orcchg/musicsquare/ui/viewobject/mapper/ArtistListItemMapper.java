package com.orcchg.musicsquare.ui.viewobject.mapper;

import com.domain.model.Artist;
import com.domain.model.mapper.Mapper;
import com.orcchg.musicsquare.ui.viewobject.ArtistListItemVO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ArtistListItemMapper implements Mapper<Artist, ArtistListItemVO> {

    @Inject
    public ArtistListItemMapper() {
    }

    @Override
    public ArtistListItemVO map(Artist object) {
        return new ArtistListItemVO.Builder(object.getId(), object.getName())
                .setCover(object.getCoverSmall())
                .build();
    }

    @Override
    public List<ArtistListItemVO> map(List<Artist> list) {
        List<ArtistListItemVO> mapped = new ArrayList<>();
        for (Artist artist : list) {
            mapped.add(map(artist));
        }
        return mapped;
    }
}
