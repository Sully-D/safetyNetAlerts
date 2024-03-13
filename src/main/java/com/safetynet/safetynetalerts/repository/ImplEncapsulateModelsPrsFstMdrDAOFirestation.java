package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Firestation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the EncapsulateModelsPrsFstMdrDAO for Firestation entities,
 * providing CRUD operations on firestation data within the system's JSON storage.
 */
@Repository
public class ImplEncapsulateModelsPrsFstMdrDAOFirestation implements EncapsulateModelsPrsFstMdrDAO<Firestation> {

    /**
     * Adds a new firestation record to the JSON data store.
     *
     * @param firestation The Firestation object to add.
     * @return The added Firestation object.
     */
    @Override
    public Firestation add(Firestation firestation) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Firestation> firestationList = readJsonData.getFirestationList();
        firestationList.add(firestation);
        readJsonData.setFirestationList(firestationList);
        jsonToObject.saveJsonData(readJsonData);
        return firestation;
    }

    /**
     * Updates an existing firestation record in the JSON data store based on its address.
     *
     * @param firestationUpdate The Firestation object with updated information.
     */
    @Override
    public void update(Firestation firestationUpdate) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Firestation> firestationList = readJsonData.getFirestationList();

        Optional<Firestation> findFirestation = firestationList.stream()
                .filter(p -> p.getAddress().equals(firestationUpdate.getAddress()))
                .findFirst();
        findFirestation.ifPresentOrElse(firestation -> {
            firestation.setStation(firestationUpdate.getStation());
            System.out.println("UPDATE COMPLETE !");
            readJsonData.setFirestationList(firestationList);
            jsonToObject.saveJsonData(readJsonData);
        }, () -> {
            System.out.println("FIRESTATION NOT FOUND");
        });
    }

    /**
     * Deletes a firestation record from the JSON data store.
     *
     * @param firestationToDelete The Firestation object to delete.
     */
    @Override
    public void delete(Firestation firestationToDelete) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Firestation> firestationList = readJsonData.getFirestationList();

        firestationList.removeIf(firestation -> firestation.equals(firestationToDelete));
        readJsonData.setFirestationList(firestationList);
        jsonToObject.saveJsonData(readJsonData);
    }
}
