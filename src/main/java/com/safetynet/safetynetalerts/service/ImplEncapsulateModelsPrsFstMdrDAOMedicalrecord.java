package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Medicalrecord;
import com.safetynet.safetynetalerts.repository.JsonToObject;

import java.util.List;
import java.util.Optional;

public class ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord implements EncapsulateModelsPrsFstMdrDAO<Medicalrecord> {

    @Override
    public void add(Medicalrecord medicalrecord) {
        JsonToObject jsonToObject = new JsonToObject();

        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();

        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();
        medicalrecordList.add(medicalrecord);
        readJsonData.setMedicalrecordList(medicalrecordList);

        jsonToObject.saveJsonData(readJsonData);
    }

    @Override
    public void update(Medicalrecord medicalrecordUpdate) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();

        String firstName = medicalrecordUpdate.getFirstName();
        String lastName = medicalrecordUpdate.getLastName();

        Optional<Medicalrecord> findPerson = medicalrecordList.stream()
                .filter(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName))
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

    @Override
    public void delete(Medicalrecord medicalrecordToDelete) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();

        medicalrecordList.remove(medicalrecordToDelete);

        readJsonData.setMedicalrecordList(medicalrecordList);
        jsonToObject.saveJsonData(readJsonData);
    }
}
