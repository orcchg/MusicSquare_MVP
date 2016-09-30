package com.domain.model;

public class TotalValue {
    private final int value;

    TotalValue(Builder builder) {
        this.value = builder.value;
    }

    public static class Builder {
        final int value;

        public Builder(int value) {
            this.value = value;
        }

        public TotalValue build() {
            return new TotalValue(this);
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TotalValue{" +
                "value=" + value +
                '}';
    }
}
