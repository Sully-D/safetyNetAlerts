package com.safetynet.safetynetalerts.repository.implement;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.EncapsulateModelsPrsFstMdrDAO;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link EncapsulateModelsPrsFstMdrDAO} for {@link Person} entities.
 * Handles adding, updating, and deleting person records in the application's JSON data storage.
 */
//@Data
@Repository
public class ImplEncapsulateModelsPrsFstMdrDAOPerson implements EncapsulateModelsPrsFstMdrDAO<Person> {

    private JsonToObject jsonToObject;
    private EncapsulateModelsPrsFstMdr readJsonData;

    @Autowired
    public ImplEncapsulateModelsPrsFstMdrDAOPerson(JsonToObject jsonToObject) {
        this.jsonToObject = jsonToObject;
    }
    @PostConstruct
    public void init() {
        try {
            this.readJsonData = this.jsonToObject.readJsonData();
        } catch (Exception e) {
            logger.error("Failed to initialize ImplEncapsulateModelsPrsFstMdrDAOPerson due to an error in readJsonData", e);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ImplEncapsulateModelsPrsFstMdrDAOPerson.class);

    /**
     * Adds a new person record to the JSON data store.
     *
     * @param person The {@link Person} object to add.
     * @return The added {@link Person} object.
     */
    @Override
    public Person add(Person person) {

        if (person == null) {
            logger.warn("Attempted to add a null Person object to the data store.");
            return null;
        }

        logger.info("Adding a new Person record to the data store: {} {}", person.getFirstName(), person.getLastName());

        List<Person> personList = readJsonData.getPersonList();

        // Check if the person already exists
        boolean exists = personList.stream()
                .anyMatch(p -> p.getFirstName().equals(person.getFirstName()) && p.getLastName().equals(person.getLastName()));
        if (exists) {
            logger.warn("A Person record already exists for: {} {}. Aborting add.", person.getFirstName(), person.getLastName());
            return null;
        }

        personList.add(person);
        readJsonData.setPersonList(personList);

        boolean saveResult = jsonToObject.saveJsonData(readJsonData);
        if (!saveResult) {
            logger.error("Failed to save the updated JSON data store after adding new Person for: {} {}", person.getFirstName(), person.getLastName());
            return null;
        }

        logger.info("Successfully added and saved new Person record for: {} {}", person.getFirstName(), person.getLastName());
        return person;
    }

    /**
     * Updates an existing person record in the JSON data store.
     * The update is based on a person's first and last name as identifiers.
     *
     * @param personUpdate The {@link Person} object containing the updated information.
     */
    @Override
    public void update(Person personUpdate) {

        if (personUpdate == null) {
            logger.warn("Attempted to update a Person with a null object.");
            return;
        }

        logger.info("Attempting to update Person record for: {} {}", personUpdate.getFirstName(), personUpdate.getLastName());

        List<Person> personList = readJsonData.getPersonList();

        Optional<Person> findPerson = personList.stream()
                .filter(p -> p.getFirstName().equals(personUpdate.getFirstName()) && p.getLastName().equals(personUpdate.getLastName()))
                .findFirst();

        findPerson.ifPresentOrElse(person -> {
            person.setAddress(personUpdate.getAddress());
            person.setCity(personUpdate.getCity());
            person.setZip(personUpdate.getZip());
            person.setPhone(personUpdate.getPhone());
            person.setEmail(personUpdate.getEmail());

            logger.info("Update complete for Person: {} {}", personUpdate.getFirstName(), personUpdate.getLastName());

            readJsonData.setPersonList(personList);
            boolean saveResult = jsonToObject.saveJsonData(readJsonData);
            if (!saveResult) {
                logger.error("Failed to save the updated JSON data store after updating Person for: {} {}", personUpdate.getFirstName(), personUpdate.getLastName());
            }
        }, () -> {
            logger.warn("Person not found for update: {} {}", personUpdate.getFirstName(), personUpdate.getLastName());
        });
    }

    /**
     * Deletes a person record from the JSON data store.
     *
     * @param personToDelete The {@link Person} object to delete.
     * @return
     */
    @Override
    public boolean delete(Person personToDelete) {

        if (personToDelete == null) {
            logger.warn("Attempted to delete a null Person object.");
            return false;
        }

        logger.info("Attempting to delete Person record: {} {}", personToDelete.getFirstName(), personToDelete.getLastName());

        List<Person> personList = readJsonData.getPersonList();

        boolean removalSuccess = personList.removeIf(person -> person.equals(personToDelete));

        if (removalSuccess) {
            logger.info("Successfully deleted Person record: {} {}", personToDelete.getFirstName(), personToDelete.getLastName());
            readJsonData.setPersonList(personList);
            boolean saveResult = jsonToObject.saveJsonData(readJsonData);
            if (!saveResult) {
                logger.error("Failed to save the updated JSON data store after deleting Person: {} {}", personToDelete.getFirstName(), personToDelete.getLastName());
            }
        } else {
            logger.warn("Person record to delete was not found: {} {}", personToDelete.getFirstName(), personToDelete.getLastName());
        }
        return removalSuccess;
    }

}
