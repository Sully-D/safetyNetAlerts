package com.safetynet.safetynetalerts.repository.implement;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Medicalrecord;
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
 * Repository class that implements CRUD operations for {@link Medicalrecord} entities
 * within the application's JSON storage. This class provides methods to add, update,
 * and delete medical records.
 */
@Repository
public class ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord implements EncapsulateModelsPrsFstMdrDAO<Medicalrecord> {

    private JsonToObject jsonToObject;
    private EncapsulateModelsPrsFstMdr readJsonData;

    @Autowired
    public ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord(JsonToObject jsonToObject) {
        this.jsonToObject = jsonToObject;
    }
    @PostConstruct
    public void init() {
        try {
            this.readJsonData = this.jsonToObject.readJsonData();
        } catch (Exception e) {
            logger.error("Failed to initialize ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord due to an error in readJsonData", e);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord.class);

    /**
     * Adds a new {@link Medicalrecord} to the JSON data store.
     *
     * @param medicalrecord The {@link Medicalrecord} object to add.
     * @return The added {@link Medicalrecord} object.
     */
    @Override
    public Medicalrecord add(Medicalrecord medicalrecord) {

        if (medicalrecord == null) {
            logger.warn("Attempted to add a null Medicalrecord object.");
            return null;
        }

        logger.info("Adding a new Medicalrecord to the data store: {} {}", medicalrecord.getFirstName(), medicalrecord.getLastName());

        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();

        // Check for existing records before adding
        boolean exists = medicalrecordList.stream()
                .anyMatch(m -> m.getFirstName().equals(medicalrecord.getFirstName()) && m.getLastName().equals(medicalrecord.getLastName()));
        if (exists) {
            logger.warn("A Medicalrecord already exists for: {} {}. Aborting add.", medicalrecord.getFirstName(), medicalrecord.getLastName());
            return null;
        }

        medicalrecordList.add(medicalrecord);
        readJsonData.setMedicalrecordList(medicalrecordList);
        boolean saveResult = jsonToObject.saveJsonData(readJsonData);

        if (!saveResult) {
            logger.error("Failed to save the updated JSON data store after adding new Medicalrecord for: {} {}", medicalrecord.getFirstName(), medicalrecord.getLastName());
            return null;
        }

        logger.info("Successfully added and saved new Medicalrecord for: {} {}", medicalrecord.getFirstName(), medicalrecord.getLastName());
        return medicalrecord;
    }

    /**
     * Updates an existing {@link Medicalrecord} in the JSON data store based on the person's first and last name.
     *
     * @param medicalrecordUpdate The {@link Medicalrecord} object containing the updated information.
     */
    @Override
    public void update(Medicalrecord medicalrecordUpdate) {
        if (medicalrecordUpdate == null) {
            logger.warn("Attempted to update a Medicalrecord with a null object.");
            return;
        }

        logger.info("Updating Medicalrecord for: {} {}", medicalrecordUpdate.getFirstName(), medicalrecordUpdate.getLastName());

        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();

        Optional<Medicalrecord> findPerson = medicalrecordList.stream()
                .filter(p -> p.getFirstName().equals(medicalrecordUpdate.getFirstName()) && p.getLastName().equals(medicalrecordUpdate.getLastName()))
                .findFirst();

        findPerson.ifPresentOrElse(person -> {
            person.setMedications(medicalrecordUpdate.getMedications());
            person.setAllergies(medicalrecordUpdate.getAllergies());
            logger.info("Update complete for Medicalrecord of: {} {}", medicalrecordUpdate.getFirstName(), medicalrecordUpdate.getLastName());
            readJsonData.setMedicalrecordList(medicalrecordList);
            boolean saveResult = jsonToObject.saveJsonData(readJsonData);
            if (!saveResult) {
                logger.error("Failed to save the updated JSON data store after updating Medicalrecord for: {} {}", medicalrecordUpdate.getFirstName(), medicalrecordUpdate.getLastName());
            }
        }, () -> {
            logger.warn("Person not found for updating Medicalrecord: {} {}", medicalrecordUpdate.getFirstName(), medicalrecordUpdate.getLastName());
        });
    }

    /**
     * Deletes a {@link Medicalrecord} from the JSON data store.
     *
     * @param medicalrecordToDelete The {@link Medicalrecord} object to delete.
     * @return
     */
    @Override
    public boolean delete(Medicalrecord medicalrecordToDelete) {

        if (medicalrecordToDelete == null) {
            logger.warn("Attempted to delete a null Medicalrecord object.");
            return false;
        }

        logger.info("Attempting to delete Medicalrecord for: {} {}", medicalrecordToDelete.getFirstName(), medicalrecordToDelete.getLastName());

        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();

        boolean removalSuccess = medicalrecordList.removeIf(person -> person.equals(medicalrecordToDelete));

        if (removalSuccess) {
            logger.info("Successfully deleted Medicalrecord for: {} {}", medicalrecordToDelete.getFirstName(), medicalrecordToDelete.getLastName());
            readJsonData.setMedicalrecordList(medicalrecordList);
            boolean saveResult = jsonToObject.saveJsonData(readJsonData);
            if (!saveResult) {
                logger.error("Failed to save the updated JSON data store after deleting Medicalrecord for: {} {}", medicalrecordToDelete.getFirstName(), medicalrecordToDelete.getLastName());
            }
        } else {
            logger.warn("Medicalrecord to delete was not found for: {} {}", medicalrecordToDelete.getFirstName(), medicalrecordToDelete.getLastName());
        }
        return removalSuccess;
    }
}
