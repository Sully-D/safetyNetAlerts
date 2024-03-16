package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.Person;
import org.springframework.stereotype.Repository;

@Repository
public interface EncapsulateModelsPrsFstMdrDAO<T> {
    T add(T toAdd);
    void update(T toSearch);
    void delete(T toDelete);
}
