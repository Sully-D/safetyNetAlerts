package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.Medicalrecord;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetList {

    private JsonToObject jsonToObject = new JsonToObject();
    private EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();

    public List<String> getAddressFirestationByNumber(String stationNumber) {

        List<Firestation> firestationList = readJsonData.getFirestationList();
        List<String> addressStation = new ArrayList<>();

        for (Firestation firestation : firestationList) {
            if (firestation.getStation().equals(stationNumber)){
                addressStation.add(firestation.getAddress());
            }
        }
        return addressStation;
    }

    public List<Person> getPersonByAddressStation(List<String> addressStation){

        List<Person> personList = readJsonData.getPersonList();
        List<Person> personCover = new ArrayList<>();

        for (Person person : personList) {
            if (addressStation != null) {
                if (addressStation.contains(person.getAddress())) {
                    personCover.add(person);
                }
            } else {
                System.out.println("ERROR : ADDRESS FIRESTATION EMPTY");
            }
        }
        return personCover;
    }


    public List<Map<String, String>> getAdultAndChild(List<Person> personList){

        List<Map<String, String>> population = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate currentDate = LocalDate.now();

        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();

        for (Person person : personList){
            for (Medicalrecord medicalrecord : medicalrecordList){
                if (medicalrecord.getFirstName().equals(person.getFirstName()) && medicalrecord.getLastName().equals(person.getLastName())){
                    LocalDate birthDate = LocalDate.parse(medicalrecord.getBirthdate(), formatter);
                    Period age = Period.between(birthDate, currentDate);
                    int years = age.getYears();
                    Map<String, String> entry = new HashMap<>();
                    if (years > 18) {
                        entry.put("Adult", String.valueOf(years));
                    } else {
                        entry.put("Child", String.valueOf(years));
                    }
                    entry.put("firstName", person.getFirstName());
                    entry.put("lastName", person.getLastName());
                    entry.put("address", person.getAddress());
                    entry.put("phone", person.getPhone());

                    population.add(entry);
                }
            }
        }
        return population;
    }

    public List<Person> getPersonByAddress(String address){

        List<Person> personList = readJsonData.getPersonList();
        List<Person> persons = new ArrayList<>();

        for (Person person : personList) {
            if (address != null) {
                if (person.getAddress().contains(address)) {
                    persons.add(person);
                }
            } else {
                System.out.println("ERROR : ADDRESS EMPTY");
            }
        }
        return persons;
    }
}
