package com.alma.alma_backend.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Base generic service to centralize simple CRUD operations across services.
 * @param <T>  entity type managed by the service
 * @param <ID> identifier type of the entity
 */
public abstract class BaseService<T, ID> {

    private final JpaRepository<T, ID> repository;

    protected BaseService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    protected JpaRepository<T, ID> getRepository() {
        return repository;
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}
