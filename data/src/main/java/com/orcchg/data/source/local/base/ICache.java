package com.orcchg.data.source.local.base;

public interface ICache {
    boolean isEmpty();
    boolean isExpired();
    void clear();
    int totalItems();
}
