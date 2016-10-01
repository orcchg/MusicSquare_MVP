package com.domain.repository;

import com.domain.model.Artist;
import com.domain.model.TotalValue;

import java.util.List;

public interface IArtistRepository {
    List<Artist> artists();
    List<Artist> artists(int limit, int offset);
    List<Artist> artists(List<String> genres);
    List<Artist> artists(int limit, int offset, List<String> genres);
    Artist artist(long artistId);
    boolean clear();
    TotalValue total();
}
