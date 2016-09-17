package com.orcchg.musicsquare.ui.viewobject;

import android.support.annotation.NonNull;

import java.util.List;

public class ArtistDetailsVO {
    private final long id;
    private final String name;
    private final String coverLarge;
    private final List<String> genres;
    private final int tracksCount;
    private final int albumsCount;
    private final String description;
    private final String webLink;

    public ArtistDetailsVO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.coverLarge = builder.coverLarge;
        this.genres = builder.genres;
        this.tracksCount = builder.tracksCount;
        this.albumsCount = builder.albumsCount;
        this.description = builder.description;
        this.webLink = builder.webLink;
    }

    public static class Builder {
        private final long id;
        private final String name;
        private String coverLarge;
        private List<String> genres;
        private int tracksCount;
        private int albumsCount;
        private String description;
        private String webLink;

        public Builder(long id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        public Builder setCoverLarge(@NonNull String coverLarge) {
            this.coverLarge = coverLarge;
            return this;
        }

        public Builder setGenres(@NonNull List<String> genres) {
            this.genres = genres;
            return this;
        }

        public Builder setTracksCount(int count) {
            this.tracksCount = count;
            return this;
        }

        public Builder setAlbumsCount(int count) {
            this.albumsCount = count;
            return this;
        }

        public Builder setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        public Builder setWebLink(@NonNull String webLink) {
            this.webLink = webLink;
            return this;
        }

        public ArtistDetailsVO build() {
            return new ArtistDetailsVO(this);
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCoverLarge() {
        return coverLarge;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getTracksCount() {
        return tracksCount;
    }

    public int getAlbumsCount() {
        return albumsCount;
    }

    public String getDescription() {
        return description;
    }

    public String getWebLink() {
        return webLink;
    }
}
