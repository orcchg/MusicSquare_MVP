package com.domain.repository;

import com.domain.model.Genre;

import java.util.List;

public interface IGenresRepository {
    List<Genre> genres();
    boolean clear();
}
