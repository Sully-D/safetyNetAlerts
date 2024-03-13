package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Medicalrecord;
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

    /**
     * Adds a new {@link Medicalrecord} to the JSON data store.
     *
     * @param medicalrecord The {@link Medicalrecord} object to add.
     * @return The added {@link Medicalrecord} object.
     */
    @Override
    public Medicalrecord add(Medicalrecord medicalrecord) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();
        medicalrecordList.add(medicalrecord);
        readJsonData.setMedicalrecordList(medicalrecordList);
        jsonToObject.saveJsonData(readJsonData);
        return medicalrecord;
    }

    /**
     * Updates an existing {@link Medicalrecord} in the JSON data store based on the person's first and last name.
     *
     * @param medicalrecordUpdate The {@link Medicalrecord} object containing the updated information.
     */
    @Override
    public void update(Medicalrecord medicalrecordUpdate) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();

        Optional<Medicalrecord> findPerson = medicalrecordList.stream()
                .filter(p -> p.getFirstName().equals(medicalrecordUpdate.getFirstName()) && p.getLastName().equals(medicalrecordUpdate.getLastName()))
                .findFirst();
        findPerson.ifPresentOrElse(person -> {
            person.setMedications(medicalrecordUpdate.getMedications());
            person.setAllergies(medicalrecordUpdate.getAllergies());
            System.out.println("UPDATE COMPLETE !");
            readJsonData.setMedicalrecordList(medicalrecordList);
            jsonToObject.saveJsonData(readJsonData);
        }, () -> {
            System.out.println("PERSON NOT FOUND");
        });
    }

    /**
     * Deletes a {@link Medicalrecord} from the JSON data store.
     *
     * @param medicalrecordToDelete The {@link Medicalrecord} object to delete.
     */
    @Override
    public void delete(Medicalrecord medicalrecordToDelete) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();

        medicalrecordList.removeIf(person -> person.equals(medicalrecordToDelete));
        readJsonData.setMedicalrecordList(medicalrecordList);
        jsonToObject.saveJsonData(readJsonData);
    }
}
