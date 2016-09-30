package com.orcchg.data.entity;

import com.google.gson.annotations.SerializedName;

public class TotalValueEntity {
    @SerializedName("value") private final int value;

    public TotalValueEntity() {
        this.value = 0;
    }

    TotalValueEntity(Builder builder) {
        this.value = builder.value;
    }

    public static class Builder {
        final int value;

        public Builder(int value) {
            this.value = value;
        }

        public TotalValueEntity build() {
            return new TotalValueEntity(this);
        }
    }

    /* Getters */
    // --------------------------------------------------------------------------------------------
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TotalValueEntity{" +
                "value=" + value +
                '}';
    }
}
