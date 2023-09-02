package org.example.clever_bank.service;

import org.example.clever_bank.exception.ValidationException;

import java.util.List;

/**
 * Base interface service
 *
 * @param <T> - type
 */
public interface Service<T> {

    /**
     * Create entity
     *
     * @param t - entity
     * @return entity
     * @throws ValidationException
     */
    T add(T t) throws ValidationException;

    /**
     * Search entity by id
     *
     * @param id - id
     * @return entity
     */
    T findById(Long id);

    /**
     * Search all entities
     * @return List<T>
     */
    List<T> findAll();

    /**
     * Update t
     * @param t - entity
     * @return entity
     * @throws ValidationException
     */
    T update(T t) throws ValidationException;

    /**
     * Remove entity by id
     * @param id - id
     * @return boolean
     */
    boolean remove(Long id);


}
