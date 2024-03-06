package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.Person;

public interface EncapsulateModelsPrsFstMdrDAO<T> {
    void add(T toAdd);
    void update(T toSearch);
    void delete(T toDelete);
}
