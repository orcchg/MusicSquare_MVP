package com.orcchg.data.entity.util;

import android.text.TextUtils;

import com.orcchg.data.entity.GenreEntity;

import java.util.Arrays;
import java.util.List;

public class GenreUtils {
    private static final String DELIMITER = ";";

    public static String genresToString(GenreEntity genre) {
        return TextUtils.join(DELIMITER, genre.getGenres());
    }

    public static List<String> stringToGenres(String genres) {
        return Arrays.asList(genres.split(DELIMITER));
    }
}
