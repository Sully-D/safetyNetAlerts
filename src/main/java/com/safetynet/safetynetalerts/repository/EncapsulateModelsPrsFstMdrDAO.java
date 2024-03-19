package com.safetynet.safetynetalerts.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface EncapsulateModelsPrsFstMdrDAO<T> {
    T add(T toAdd);
    void update(T toSearch);
    boolean delete(T toDelete);
}
