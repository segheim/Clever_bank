package org.example.clever_bank.dao;

import java.util.List;
import java.util.Optional;

public interface DaoBase<T> {

    Optional<T> create(T entity);

    Optional<T> read(Long id);

    List<T> readAll();

    Optional<T> update(T entity);

    boolean delete(Long id);

}
