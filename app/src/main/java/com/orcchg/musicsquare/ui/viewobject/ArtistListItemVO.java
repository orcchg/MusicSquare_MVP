package com.orcchg.musicsquare.ui.viewobject;

import android.support.annotation.NonNull;

public class ArtistListItemVO {
    private final long id;
    private final String name;
    private final String coverSmall;

    public ArtistListItemVO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.coverSmall = builder.coverSmall;
    }

    public static class Builder {
        private final long id;
        private final String name;
        private String coverSmall;

        public Builder(long id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        public Builder setCover(@NonNull String coverSmall) {
            this.coverSmall = coverSmall;
            return this;
        }

        public ArtistListItemVO build() {
            return new ArtistListItemVO(this);
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
}
