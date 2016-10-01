package com.domain.repository;

import com.domain.model.Artist;
import com.domain.model.TotalValue;
import com.sun.istack.internal.Nullable;

import java.util.List;

public interface IArtistRepository {
    List<Artist> artists();
    List<Artist> artists(int limit, int offset);
    List<Artist> artists(@Nullable List<String> genres);
    List<Artist> artists(int limit, int offset, @Nullable List<String> genres);
    Artist artist(long artistId);
    boolean clear();
    TotalValue total();
}
