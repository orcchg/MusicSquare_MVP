package com.orcchg.data.source.remote.genre;

import android.support.annotation.Nullable;

import com.orcchg.data.entity.GenreEntity;
import com.orcchg.data.entity.TotalValueEntity;

import java.util.List;

public interface GenreDataSource {

    List<GenreEntity> genres();

    @Nullable
    GenreEntity genre(String name);

    TotalValueEntity total();
}
