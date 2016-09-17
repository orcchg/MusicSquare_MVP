package com.orcchg.musicsquare.ui.viewobject.mapper;

import com.domain.model.Artist;
import com.domain.model.mapper.Mapper;
import com.orcchg.musicsquare.ui.viewobject.ArtistDetailsVO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ArtistDetailsMapper implements Mapper<Artist, ArtistDetailsVO> {

    @Inject
    public ArtistDetailsMapper() {
    }

    @Override
    public ArtistDetailsVO map(Artist object) {
        return new ArtistDetailsVO.Builder(object.getId(), object.getName())
                .setCoverLarge(object.getCoverLarge())
                .setGenres(object.getGenres())
                .setTracksCount(object.getTracksCount())
                .setAlbumsCount(object.getAlbumsCount())
                .setDescription(object.getDescription())
                .setWebLink(object.getWebLink())
                .build();
    }

    @Override
    public List<ArtistDetailsVO> map(List<Artist> list) {
        List<ArtistDetailsVO> mapped = new ArrayList<>();
        for (Artist artist : list) {
            mapped.add(map(artist));
        }
        return mapped;
    }
}
