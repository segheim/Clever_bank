package org.example.clever_bank.service;

import java.util.List;

public interface Service<T> {

    boolean add(T t);
    T findById(Long id);
    List<T> findAll();
    boolean update(T t);
    boolean remove(Long id);


}
