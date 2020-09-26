package com.deloitte.service.impl;

import com.deloitte.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Slf4j
@Component
public class HashMapStore implements StoreService {

    protected  static Map<String, Object> store = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        System.out.println("-- Initialized --");
    }

    @Override
    public <T> void store(String key, T value){
        store.put(key, value);
    }

    @Override
    public Object retrieve(String key){
        return store.get(key);
    }

    @Override
    public Callable<Object> retrieveFun(final String key){
        return() -> retrieve(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T retrieve(String key, Class<T> clazz){
        return (T) store.get(key);
    }

    @Override
    public <T> Callable<T> retrieveFun(String key, Class<T> clazz){
        return() -> {
            System.out.println("");
            return retrieve(key, clazz);
        };
    }

    @Override
    public Set<Entry> retrieveAll(){
        return store.keySet().stream()
                .map(key -> Entry.builder().key(key).value(retrieve(key)).build())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> keySet(){
        return store.keySet();
    }

    @Override
    public Set<Entry> retrieveWithKeyPrefix(final String prefix){
        return store.keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .map(key -> Entry.builder().key(key).value(retrieve(key)).build())
                .collect(Collectors.toSet());
    }
}
