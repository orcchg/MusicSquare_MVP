package com.orcchg.data.source.local;

public interface ICache {
    boolean isEmpty();
    boolean isExpired();
    void clear();
}
