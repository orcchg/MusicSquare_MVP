package com.domain.repository;

import com.domain.model.Genre;
import com.domain.model.TotalValue;

import java.util.List;

public interface IGenreRepository {
    List<Genre> genres();
    Genre genre(String name);
    boolean clear();
    TotalValue total();
}
