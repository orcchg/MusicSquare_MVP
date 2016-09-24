package com.domain.repository;

import com.domain.model.Artist;

import java.util.List;

public interface IArtistRepository {
    List<Artist> artists();
    List<Artist> artists(int limit, int offset);
    List<Artist> artists(String... genres);
    List<Artist> artists(int limit, int offset, String... genres);
    Artist artist(long artistId);
    boolean clear();
}
