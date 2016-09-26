package com.domain.model;

import java.util.List;

public class Genre {
    private final String name;
    private List<String> genres;

    public Genre(Builder builder) {
        this.name = builder.name;
        this.genres = builder.genres;
    }

    public static class Builder {
        final String name;
        List<String> genres;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setGenres(List<String> genres) {
            this.genres = genres;
            return this;
        }

        public Genre build() {
            return new Genre(this);
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getGenres() {
        return genres;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                ", genres=" + genres +
                '}';
    }
}
