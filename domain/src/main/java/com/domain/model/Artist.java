package com.domain.model;

import java.util.List;

public class Artist {
    private final long id;
    private final String name;
    private String coverSmall;
    private String coverLarge;
    private List<String> genres;
    private int tracksCount;
    private int albumsCount;
    private String description;
    private String webLink;

    public Artist(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.coverSmall = builder.coverSmall;
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
        private String coverSmall;
        private String coverLarge;
        private List<String> genres;
        private int tracksCount;
        private int albumsCount;
        private String description;
        private String webLink;

        public Builder(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder setCoverSmall(String coverSmall) {
            this.coverSmall = coverSmall;
            return this;
        }

        public Builder setCoverLarge(String coverLarge) {
            this.coverLarge = coverLarge;
            return this;
        }

        public Builder setGenres(List<String> genres) {
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

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setWebLink(String webLink) {
            this.webLink = webLink;
            return this;
        }

        public Artist build() {
            return new Artist(this);
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCoverSmall() {
        return coverSmall;
    }

    public void setCoverSmall(String coverSmall) {
        this.coverSmall = coverSmall;
    }

    public String getCoverLarge() {
        return coverLarge;
    }

    public void setCoverLarge(String coverLarge) {
        this.coverLarge = coverLarge;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getTracksCount() {
        return tracksCount;
    }

    public void setTracksCount(int tracksCount) {
        this.tracksCount = tracksCount;
    }

    public int getAlbumsCount() {
        return albumsCount;
    }

    public void setAlbumsCount(int albumsCount) {
        this.albumsCount = albumsCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverSmall='" + coverSmall + '\'' +
                ", coverLarge='" + coverLarge + '\'' +
                ", genres=" + genres +
                ", tracksCount=" + tracksCount +
                ", albumsCount=" + albumsCount +
                ", description='" + description + '\'' +
                ", webLink='" + webLink + '\'' +
                '}';
    }
}
