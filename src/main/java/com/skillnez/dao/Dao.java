package com.skillnez.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    E update(E e);

    List<E> findAll();

    boolean delete(K id);

    E save(E e);

    Optional<E> findById(K id);
}
