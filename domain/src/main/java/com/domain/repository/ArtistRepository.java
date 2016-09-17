package com.domain.repository;

import com.domain.model.Artist;

import java.util.List;

public interface ArtistRepository {
    List<Artist> artists();
    Artist artist(long artistId);
    boolean clear();
}
