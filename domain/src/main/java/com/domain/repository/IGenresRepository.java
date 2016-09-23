package com.domain.repository;

import java.util.List;

public interface IGenresRepository {
    List<String> genres();
    boolean clear();
}
