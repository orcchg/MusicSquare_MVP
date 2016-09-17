package com.orcchg.data.entity;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Model of artist item in the data response.
 */
public class ArtistEntity {
    public static final String COVER_LARGE = "big";
    public static final String COVER_SMALL = "small";

    @SerializedName("id") private final long id;
    @SerializedName("name") private final String name;
    @SerializedName("genres") private final List<String> genres;
    @SerializedName("tracks") private final int tracksCount;
    @SerializedName("albums") private final int albumsCount;
    @SerializedName("link") private final String webLink;
    @SerializedName("description") private final String description;
    @SerializedName("cover") private final Map<String, String> covers;

    ArtistEntity(Builder builder) {
        id = builder.id;
        name = builder.name;
        genres = builder.genres;
        tracksCount = builder.tracksCount;
        albumsCount = builder.albumsCount;
        webLink = builder.webLink;
        description = builder.description;
        covers = builder.covers;
    }

    public static class Builder {
        final long id;
        final String name;
        List<String> genres;
        int tracksCount;
        int albumsCount;
        String webLink;
        String description;
        Map<String, String> covers;

        public Builder(long id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        public Builder setGenres(@NonNull List<String> genres) {
            this.genres = genres;
            return this;
        }

        public Builder setTracksCount(int count) {
            tracksCount = count;
            return this;
        }

        public Builder setAlbumsCount(int count) {
            albumsCount = count;
            return this;
        }

        public Builder setWebLink(@NonNull String link) {
            webLink = link;
            return this;
        }

        public Builder setDescription(@NonNull String description) {
            this.description = description;
            return this;
        }

        public Builder setCovers(@NonNull Map<String, String> covers) {
            this.covers = covers;
            return this;
        }

        public Builder setCovers(@NonNull String large, @NonNull String small) {
            this.covers = new ArrayMap<>();
            this.covers.put(COVER_LARGE, large);
            this.covers.put(COVER_SMALL, small);
            return this;
        }

        public ArtistEntity build() {
            return new ArtistEntity(this);
        }
    }

    /* Getters */
    // --------------------------------------------------------------------------------------------
    public long getId() { return id; }
    public String getName() { return name; }
    public List<String> getGenres() { return genres; }
    public int getTracksCount() { return tracksCount; }
    public int getAlbumsCount() { return albumsCount; }
    public String getWebLink() { return webLink; }
    public String getDescription() { return description; }
    public Map<String, String> getCovers() { return covers; }
    public String getCoverLarge() { return covers.get(COVER_LARGE); }
    public String getCoverSmall() { return covers.get(COVER_SMALL); }

    /* Setters */
    // --------------------------------------------------------------------------------------------
    /* XXX: just left here for convenience
    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setGenres(List<String> genres) { this.genres = genres; }
    public void setTracksCount(int tracksCount) { this.tracksCount = tracksCount; }
    public void setAlbumsCount(int albumsCount) { this.albumsCount = albumsCount; }
    public void setWebLink(String webLink) { this.webLink = webLink; }
    public void setDescription(String description) { this.description = description; }
    public void setCovers(Map<String, String> covers) { this.covers = covers; }
    */

    @Override
    public String toString() {
        return "ArtistEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genres=" + genres +
                ", tracksCount=" + tracksCount +
                ", albumsCount=" + albumsCount +
                ", webLink='" + webLink + '\'' +
                ", description='" + description + '\'' +
                ", covers=" + covers +
                '}';
    }
}
