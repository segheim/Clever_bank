package org.example.clever_bank.dao;

import java.util.List;
import java.util.Optional;

/**
 * Common interface dao layer with methods create, read, readAll, update, delete
 *
 * @param <T> the type of parameter
 * @version 1.0
 */
public interface DaoBase<T> {

    /**
     * Creat entity in database
     *
     * @param entity - entity
     * @return Optional<T>
     */
    Optional<T> create(T entity);

    /**
     * Search T by id in database
     *
     * @param id - id
     * @return Optional<T>
     */
    Optional<T> read(Long id);


    /**
     * Search all in database
     *
     * @return List<T>
     */
    List<T> readAll();

    /**
     *  Update entity in database
     * @param entity - entity
     * @return Optional<T>
     */
    Optional<T> update(T entity);

    /**
     * Remove T from database
     *
     * @param id - id
     * @return boolean
     */
    boolean delete(Long id);

}
