package com.deloitte.service;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;

public interface StoreService {

    <T> void store (String key, T value);

    Object retrieve(String key);

    Callable<Object> retrieveFun(final String key);

    <T> T retrieve(String key, Class<T> clazz);

    <T> Callable<T> retrieveFun (final String key, Class<T> clazz);

    Set<Entry> retrieveAll();

    Set<String> keySet();

    Set<Entry> retrieveWithKeyPrefix(String prefix);

    @Data
    @Builder
    public static class Entry<String, T> implements Serializable {
        private final String key;
        private final T value;
    }
}
