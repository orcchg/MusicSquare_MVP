package com.orcchg.data.entity.util;

import android.text.TextUtils;

import com.orcchg.data.entity.ArtistEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistUtils {
    private static final String DELIMITER = ";";
    private static final String MAP_DELIMITER = "=";

    /**
     * Convert complex data into string representation and vice-versa.
     */
    public static String genresToString(ArtistEntity musician) {
        return TextUtils.join(DELIMITER, musician.getGenres());
    }

    public static String coversToString(ArtistEntity musician) {
        String delimiter = "";
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<String, String> entry : musician.getCovers().entrySet()) {
            builder.append(delimiter).append(entry.getKey()).append(MAP_DELIMITER).append(entry.getValue());
            delimiter = DELIMITER;
        }
        return builder.toString();
    }

    public static List<String> stringToGenres(String genres) {
        return Arrays.asList(genres.split(DELIMITER));
    }

    public static Map<String, String> stringToCovers(String covers) {
        String[] tokens = covers.split(DELIMITER);
        Map<String, String> map = new HashMap<>();
        for (String token : tokens) {
            String[] pair = token.split(MAP_DELIMITER);
            map.put(pair[0], pair[1]);
        }
        return map;
    }
}
