package com.skillnez.repository.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    boolean update(E e);
    List<E> findALl();
    boolean delete(K id);
    E save(E e);
    Optional<E> findById(K id);
}
