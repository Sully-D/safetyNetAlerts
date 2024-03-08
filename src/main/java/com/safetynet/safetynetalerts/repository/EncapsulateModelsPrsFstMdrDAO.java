package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.Person;

public interface EncapsulateModelsPrsFstMdrDAO<T> {
    T add(T toAdd);
    void update(T toSearch);
    void delete(T toDelete);
}
