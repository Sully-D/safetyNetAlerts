package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.*;
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

    public static List<String> getAddressFirestationByNumber(String stationNumber) {

        List<Firestation> listFirestations = readJsonData.getFirestationList();
        List<String> listAddressStations = new ArrayList<>();

        for (Firestation firestation : listFirestations) {
            if (firestation.getStation().equals(stationNumber)){
                listAddressStations.add(firestation.getAddress());
            }
        }
        return listAddressStations;
    }

    public List<Person> getPersonByAddressStation(List<String> addressStation){

        List<Person> listPersons = readJsonData.getPersonList();
        List<Person> listPersonsCoverByStation = new ArrayList<>();

        for (Person person : listPersons) {
            if (addressStation != null) {
                if (addressStation.contains(person.getAddress())) {
                    listPersonsCoverByStation.add(person);
                }
            } else {
                System.out.println("ERROR : ADDRESS FIRESTATION EMPTY");
            }
        }
        return listPersonsCoverByStation;
    }


    public List<Map <String, String>> getAge(List<Person> listPersons){

        List<Map <String, String>> listPersonsAges = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate currentDate = LocalDate.now();

        List<Medicalrecord> listMedicalRecords = readJsonData.getMedicalrecordList();

        for (Person person : listPersons){
            for (Medicalrecord medicalrecord : listMedicalRecords){
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

                    listPersonsAges.add(entry);
                }
            }
        }
        return listPersonsAges;
    }

    public List<Person> getPersonByAddress(String address){

        List<Person> listPersons = readJsonData.getPersonList();
        List<Person> listPersonsByAddress = new ArrayList<>();

        for (Person person : listPersons) {
            if (address != null) {
                if (person.getAddress().contains(address)) {
                    listPersonsByAddress.add(person);
                }
            } else {
                System.out.println("ERROR : ADDRESS EMPTY");
            }
        }
        return listPersonsByAddress;
    }

    public static String getNumberFirestationByAddress(String address){
        List<Firestation> listFirestations = readJsonData.getFirestationList();

        for (Firestation firestation : listFirestations) {
            if (firestation.getAddress().equals(address)){
                return firestation.getStation();
            }
        }
        return null;
    }

    public List<AllInfoPerson> allInfosPerson (List <Person>  listPersonsByAddress, List<Map<String, String>> listPeronsAges){

        List<AllInfoPerson>  listPersonsAndFirestationsNumberByAddress = new ArrayList<>();

        for (Person person : listPersonsByAddress){
            for (Map<String, String> personAge : listPeronsAges){

                AllInfoPerson allInfoPerson = new AllInfoPerson();

                if (person.getFirstName().contains(personAge.get("firstName")) &&
                        person.getLastName().contains(personAge.get("lastName"))){

                    Map<String, String> mapMedicalRecords = GetList.getMedicalRecord(personAge.get("firstName"),
                            personAge.get("lastName"));
                    String numberFirestationByAddress = GetList.getNumberFirestationByAddress(person.getAddress());

                    allInfoPerson.setFirestation(numberFirestationByAddress);
                    allInfoPerson.setAddress(person.getAddress());
                    allInfoPerson.setLastName(person.getLastName());
                    allInfoPerson.setFirstName(person.getFirstName());
                    allInfoPerson.setAge(personAge.get("year"));
                    allInfoPerson.setMedications(mapMedicalRecords.get("medications"));
                    allInfoPerson.setAllergies(mapMedicalRecords.get("allergies"));
                    allInfoPerson.setEmail(person.getEmail());
                    allInfoPerson.setPhone(person.getPhone());

                    listPersonsAndFirestationsNumberByAddress.add(allInfoPerson);
                }
            }
        }

        return listPersonsAndFirestationsNumberByAddress;
    }

    public static Map<String, String> getMedicalRecord(String firstName, String lastName){
        List<Medicalrecord> listMedicalRecords = readJsonData.getMedicalrecordList();
        Map<String, String> mapInfoMedicalRecords = new HashMap<>();


        for (Medicalrecord medicalrecord : listMedicalRecords){
            if (medicalrecord.getLastName().equals(lastName) && medicalrecord.getFirstName().equals(firstName)){

                String fName = medicalrecord.getFirstName();
                String lName = medicalrecord.getLastName();
                String medications = medicalrecord.getMedications().toString();
                String allergies = medicalrecord.getAllergies().toString();

                mapInfoMedicalRecords.put("firstName", fName);
                mapInfoMedicalRecords.put("lastName", lName);
                mapInfoMedicalRecords.put("medications", medications);
                mapInfoMedicalRecords.put("allergies", allergies);
            }
        }
        return mapInfoMedicalRecords;
    }

    public List<AllInfoPerson> sortByAddress(List<AllInfoPerson> listInfoPersons, List<String> listFirestationAddress){

        List<AllInfoPerson> listAllInfoPersons = new ArrayList<>();

        for (String address : listFirestationAddress){
            for (AllInfoPerson person : listInfoPersons){
                if (!listAllInfoPersons.contains(person.getFirstName()) &&
                        !listAllInfoPersons.contains(person.getLastName())){
                    if (person.getAddress().equals(address)){
                        listAllInfoPersons.add(person);
                    }
                }
            }
        }
        return listAllInfoPersons;
    }

    public List<String> allFirestationsAddress (List<String> listFirestationsNumbers){

        List<String> listFirestationsAddress = new ArrayList<>();

        for (String number : listFirestationsNumbers){

            List<String> listFirestationsAddressByNumber = GetList.getAddressFirestationByNumber(number);

            listFirestationsAddress.addAll(listFirestationsAddressByNumber);
        }

        return listFirestationsAddress;
    }

    public List<String> getEmailByCity (String city){

        List<Person> listPerons = readJsonData.getPersonList();
        List<String> listEmails = new ArrayList<>();

        for (Person person : listPerons){
            if (person.getCity().equals(city)){
                listEmails.add(person.getEmail());
            }
        }

        return listEmails;
    }

    public List<String> getFirestationNumber(){

        List<Firestation> listFirestationsNumbers = readJsonData.getFirestationList();
        List<String> listNumbers = new ArrayList<>();

        for (Firestation firestation : listFirestationsNumbers){
            if (!listNumbers.contains(firestation.getStation())){
                listNumbers.add(firestation.getStation());
            }
        }
        return listNumbers;
    }
}
