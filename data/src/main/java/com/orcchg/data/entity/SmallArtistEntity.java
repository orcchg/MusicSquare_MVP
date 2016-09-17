package com.orcchg.data.entity;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class SmallArtistEntity {
    @SerializedName("id") private final long id;
    @SerializedName("name") private final String name;
    @SerializedName("cover") private String cover;

    SmallArtistEntity(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.cover = builder.cover;
    }

    public static class Builder {
        final long id;
        final String name;
        String cover;

        public Builder(long id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        public Builder setCover(@NonNull String cover) {
            this.cover = cover;
            return this;
        }

        public SmallArtistEntity build() {
            return new SmallArtistEntity(this);
        }
    }

    /* Getters */
    // --------------------------------------------------------------------------------------------
    public long getId() { return id; }
    public String getName() { return name; }
    public String getCover() { return cover; }

    @Override
    public String toString() {
        return "SmallArtistEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
