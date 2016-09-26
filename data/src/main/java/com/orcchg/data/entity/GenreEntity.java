package com.orcchg.data.entity;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenreEntity {
    @SerializedName("name") private final String name;
    @SerializedName("genres") private final List<String> genres;

    GenreEntity(Builder builder) {
        name = builder.name;
        genres = builder.genres;
    }

    public static class Builder {
        final String name;
        List<String> genres;

        public Builder(@NonNull String name) {
            this.name = name;
        }

        public Builder setGenres(@NonNull List<String> genres) {
            this.genres = genres;
            return this;
        }

        public GenreEntity build() {
            return new GenreEntity(this);
        }
    }

    /* Getters */
    // --------------------------------------------------------------------------------------------
    public String getName() { return name; }
    public List<String> getGenres() { return genres; }

    @Override
    public String toString() {
        return "GenreEntity{" +
                "name='" + name + '\'' +
                ", genres=" + genres +
                '}';
    }
}
