package com.safetynet.safetynetalerts.repository.implement;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.EncapsulateModelsPrsFstMdrDAO;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the EncapsulateModelsPrsFstMdrDAO for Firestation entities,
 * providing CRUD operations on firestation data within the system's JSON storage.
 */
@Repository
public class ImplEncapsulateModelsPrsFstMdrDAOFirestation implements EncapsulateModelsPrsFstMdrDAO<Firestation> {

    private JsonToObject jsonToObject;
    private EncapsulateModelsPrsFstMdr readJsonData;

    @Autowired
    public ImplEncapsulateModelsPrsFstMdrDAOFirestation(JsonToObject jsonToObject) {
        this.jsonToObject = jsonToObject;
    }
     @PostConstruct
        public void init() {
            try {
                this.readJsonData = this.jsonToObject.readJsonData();
            } catch (Exception e) {
                logger.error("Failed to initialize ImplEncapsulateModelsPrsFstMdrDAOFirestation due to an error in readJsonData", e);
            }
        }


    private static final Logger logger = LoggerFactory.getLogger(ImplEncapsulateModelsPrsFstMdrDAOFirestation.class);

    /**
     * Adds a new firestation record to the JSON data store.
     *
     * @param firestation The Firestation object to add.
     * @return The added Firestation object.
     */
    @Override
    public Firestation add(Firestation firestation) {

        if (firestation == null) {
            logger.warn("Attempted to add a null Firestation object to the data store.");
            return null;
        }

        logger.info("Adding a new firestation record: Station number {}, Address {}", firestation.getStation(), firestation.getAddress());

        if (readJsonData == null) {
            logger.error("Failed to read the current JSON data store.");
            return null;
        }

        List<Firestation> firestationList = readJsonData.getFirestationList();
        firestationList.add(firestation);
        readJsonData.setFirestationList(firestationList);

        boolean saveResult = jsonToObject.saveJsonData(readJsonData);
        if (!saveResult) {
            logger.error("Failed to save the updated JSON data store after adding firestation: Station number {}, Address {}", firestation.getStation(), firestation.getAddress());
            return null;
        }

        logger.info("Successfully added and saved new firestation record: Station number {}, Address {}", firestation.getStation(), firestation.getAddress());
        return firestation;
    }

    /**
     * Updates an existing firestation record in the JSON data store based on its address.
     *
     * @param firestationUpdate The Firestation object with updated information.
     */
    @Override
    public void update(Firestation firestationUpdate) {

        if (firestationUpdate == null || firestationUpdate.getAddress() == null || firestationUpdate.getStation() == null) {
            logger.warn("Attempted to update a Firestation with incomplete information.");
            return;
        }

        logger.info("Updating firestation record for address: {}", firestationUpdate.getAddress());

        List<Firestation> firestationList = readJsonData.getFirestationList();

        Optional<Firestation> findFirestation = firestationList.stream()
                .filter(p -> p.getAddress().equals(firestationUpdate.getAddress()))
                .findFirst();

        findFirestation.ifPresentOrElse(firestation -> {
            firestation.setStation(firestationUpdate.getStation());
            logger.info("Update complete for firestation at address: {}", firestationUpdate.getAddress());
            readJsonData.setFirestationList(firestationList);
            boolean saveResult = jsonToObject.saveJsonData(readJsonData);
            if (!saveResult) {
                logger.error("Failed to save the updated JSON data store after updating firestation: {}", firestationUpdate.getAddress());
            }
        }, () -> {
            logger.warn("Firestation not found for address: {}", firestationUpdate.getAddress());
        });
    }

    /**
     * Deletes a firestation record from the JSON data store.
     *
     * @param firestationToDelete The Firestation object to delete.
     * @return
     */
    @Override
    public boolean delete(Firestation firestationToDelete) {

        if (firestationToDelete == null) {
            logger.warn("Attempted to delete a null Firestation object.");
            return false;
        }

        logger.info("Deleting firestation record: Station number {}, Address {}", firestationToDelete.getStation(), firestationToDelete.getAddress());

        List<Firestation> firestationList = readJsonData.getFirestationList();

        boolean removalResult = firestationList.removeIf(firestation -> firestation.equals(firestationToDelete));

        if (removalResult) {
            logger.info("Successfully deleted firestation record: Station number {}, Address {}", firestationToDelete.getStation(), firestationToDelete.getAddress());
            readJsonData.setFirestationList(firestationList);
            boolean saveResult = jsonToObject.saveJsonData(readJsonData);
            if (!saveResult) {
                logger.error("Failed to save the updated JSON data store after deleting the firestation: {}", firestationToDelete.getAddress());
            }
        } else {
            logger.warn("Firestation record to delete was not found: Station number {}, Address {}", firestationToDelete.getStation(), firestationToDelete.getAddress());
        }
        return removalResult;
    }
}
