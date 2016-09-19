package com.domain.repository;

import com.domain.model.Artist;

import java.util.List;

public interface IArtistRepository {
    List<Artist> artists();
    Artist artist(long artistId);
    boolean clear();
}
