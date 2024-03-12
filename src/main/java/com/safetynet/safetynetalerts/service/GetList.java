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
import java.util.*;

@Service
public class GetList {

    private static JsonToObject jsonToObject = new JsonToObject();
    private static EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();

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


    public List<Map <String, String>> getYear(List<Person> personList){

        List<Map <String, String>> population = new ArrayList<>();

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
                    entry.put("firstName", person.getFirstName());
                    entry.put("lastName", person.getLastName());
                    entry.put("address", person.getAddress());
                    entry.put("phone", person.getPhone());
                    entry.put("year", String.valueOf(years));

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

    public static String getNumberFirestationByAddress(String address){
        List<Firestation> firestationList = readJsonData.getFirestationList();

        for (Firestation firestation : firestationList) {
            if (firestation.getAddress().equals(address)){
                return firestation.getStation();
            }
        }
        return null;
    }

    public List<String> allInfosPerson (List <Person>  personByAddress, List<Map<String, String>> yearPerson){

        Map<String, String> personAndFirestationNumber = new HashMap<>();
        List<String>  personAndFirestationNumberByAddress = new ArrayList<>();

        for (Person person : personByAddress){
            for (Map<String, String> personYearOld : yearPerson){
                if (person.getFirstName().contains(personYearOld.get("firstName")) &&
                        person.getLastName().contains(personYearOld.get("lastName"))){

                    Map<String, String> medicalRecord = GetList.getMedicalRecord(personYearOld.get("firstName"),
                            personYearOld.get("lastName"));
                    String numberFirestationByAddress = GetList.getNumberFirestationByAddress(person.getAddress());
                    personAndFirestationNumber.put("numberFirestation", numberFirestationByAddress);
                    personAndFirestationNumber.put("address", person.getAddress());
                    personAndFirestationNumber.put("lastName", person.getLastName());
                    personAndFirestationNumber.put("firstName", person.getFirstName());
                    personAndFirestationNumber.put("age", personYearOld.get("year"));
                    personAndFirestationNumber.put("medications", medicalRecord.get("medications"));
                    personAndFirestationNumber.put("allergies", medicalRecord.get("allergies"));
                    personAndFirestationNumber.put("phone", person.getPhone());

                    personAndFirestationNumberByAddress.add(
                        "firestation:" + personAndFirestationNumber.get("numberFirestation") +
                        ", address:" + personAndFirestationNumber.get("address") +
                        ", lastName:" + personAndFirestationNumber.get("lastName") +
                        ", firstName:" + personAndFirestationNumber.get("firstName") +
                        ", age:" + personAndFirestationNumber.get("age") +
                        ", medications:" + personAndFirestationNumber.get("medications") +
                        ", allergies:" + personAndFirestationNumber.get("allergies") +
                        ", phone:" + personAndFirestationNumber.get("phone")
                    );
                }
            }
        }
        return personAndFirestationNumberByAddress;
    }

    public static Map<String, String> getMedicalRecord(String firstName, String lastName){
        List<Medicalrecord> medicalrecordList = readJsonData.getMedicalrecordList();
        Map<String, String> infoMedicalRecord = new HashMap<>();


        for (Medicalrecord medicalrecord : medicalrecordList){
            if (medicalrecord.getLastName().equals(lastName) && medicalrecord.getFirstName().equals(firstName)){
                String fName = medicalrecord.getFirstName();
                String lName = medicalrecord.getLastName();
                String medications = medicalrecord.getMedications().toString();
                String allergies = medicalrecord.getAllergies().toString();

                infoMedicalRecord.put("firstName", fName);
                infoMedicalRecord.put("lastName", lName);
                infoMedicalRecord.put("medications", medications);
                infoMedicalRecord.put("allergies", allergies);
            }
        }
        return infoMedicalRecord;
    }
}
