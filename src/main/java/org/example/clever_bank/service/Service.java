package org.example.clever_bank.service;

import org.example.clever_bank.exception.ValidationException;

import java.util.List;

public interface Service<T> {

    T add(T t) throws ValidationException;
    T findById(Long id);
    List<T> findAll();
    T update(T t) throws ValidationException;
    boolean remove(Long id);


}
