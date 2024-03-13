package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Person;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link EncapsulateModelsPrsFstMdrDAO} for {@link Person} entities.
 * Handles adding, updating, and deleting person records in the application's JSON data storage.
 */
@Data
@Repository
public class ImplEncapsulateModelsPrsFstMdrDAOPerson implements EncapsulateModelsPrsFstMdrDAO<Person> {

    /**
     * Adds a new person record to the JSON data store.
     *
     * @param person The {@link Person} object to add.
     * @return The added {@link Person} object.
     */
    @Override
    public Person add(Person person) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Person> personList = readJsonData.getPersonList();
        personList.add(person);
        readJsonData.setPersonList(personList);
        jsonToObject.saveJsonData(readJsonData);
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
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
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

            System.out.println("UPDATE COMPLETE !");

            readJsonData.setPersonList(personList);
            jsonToObject.saveJsonData(readJsonData);
        }, () -> {
            System.out.println("PERSON NOT FOUND");
        });
    }

    /**
     * Deletes a person record from the JSON data store.
     *
     * @param personToDelete The {@link Person} object to delete.
     */
    @Override
    public void delete(Person personToDelete) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Person> personList = readJsonData.getPersonList();

        personList.removeIf(person -> person.equals(personToDelete));

        readJsonData.setPersonList(personList);
        jsonToObject.saveJsonData(readJsonData);
    }

}
