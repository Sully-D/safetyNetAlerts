package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class URI {

    GetList getList = new GetList();

    public List<String> getPersonCoverByFirestation(String stationNumber) {

        int major = 0;
        int minor = 0;

        Map<String, String> population = new HashMap<>();


        List<String> addressStation = getList.getAddressFirestationByNumber(stationNumber);
        List<Person> personCoverByFirestation = getList.getPersonByAddressStation(addressStation);
        List<Map<String, String>> listAdultAndChild = getList.getYear(personCoverByFirestation);

        for (Map<String, String> person : listAdultAndChild){
            int year = Integer.parseInt(person.get("year"));
            if (year > 18){
                major++;
            } else {
                minor++;
            }
        }

        List<String> listPersonCoverByFirestation = new ArrayList<>();
        String[] nbAdultAndMinor = {"nbAdult="+major, "nbMinor="+minor};
        listPersonCoverByFirestation.add(Arrays.toString(nbAdultAndMinor));
        listPersonCoverByFirestation.add(listAdultAndChild.toString());

        return listPersonCoverByFirestation;
    }

    public List<String> getChildAtAddress(String address){

        List<String> childAtAddress = new ArrayList<>();
        List<String> adultAtAddress = new ArrayList<>();
        List<String> personAtAddress = new ArrayList<>();
        Map<String, String> child = new HashMap<>();
        Map<String, String> adult = new HashMap<>();

        List<Person> listPersonAtAddress = getList.getPersonByAddress(address);
        List<Map <String, String>> listAdultAndChild = getList.getYear(listPersonAtAddress);

        for (Map <String, String> person : listAdultAndChild){
            int year = Integer.parseInt(person.get("year"));
            if (year <= 18){
                child.put("lastName", person.get("lastName"));
                child.put("firstName", person.get("firstName"));
                child.put("Child", person.get("Child"));
                childAtAddress.add(child.toString());
            } else {
                adult.put("lastName", person.get("lastName"));
                adult.put("firstName", person.get("firstName"));
                adult.put("Adult", person.get("Adult"));
                adultAtAddress.add(adult.toString());
            }
        }

        if (childAtAddress.isEmpty()) {
            return personAtAddress;
        }
        personAtAddress.add(childAtAddress.toString());
        personAtAddress.add(adultAtAddress.toString());

        return personAtAddress;
    }

    public List<String> phoneNumberByFirestation(String firestationNumber){

        List<String> addressStation = getList.getAddressFirestationByNumber(firestationNumber);
        List<Person> personCoverByFirestation = getList.getPersonByAddressStation(addressStation);
        List<String> tempListPhone = new ArrayList<>();
        List<String> listPhone = new ArrayList<>();

        for (Person person : personCoverByFirestation) {
            tempListPhone.add(person.getPhone());
        }

        for (String phone : tempListPhone) {
            if (!listPhone.contains(phone)){
                listPhone.add(phone);
            }
        }

        return listPhone;
    }

    public List<String> getPersonAndFirestationNumberByAddress(String address){

        Map<String, String> personAndFirestationNumber = new HashMap<>();
        List<String> personAndFirestationNumberByAddress = new ArrayList<>();

        List<Person> personLeaveInAddress = getList.getPersonByAddress(address);
        String numberFirestationByAddress = getList.getNumberFirestationByAddress(address);
        List<Map<String, String>> yearPerson = getList.getYear(personLeaveInAddress);

        for (Person person : personLeaveInAddress){
            for (Map<String, String> personYearOld : yearPerson){
                if (person.getFirstName().contains(personYearOld.get("firstName")) &&
                        person.getLastName().contains(personYearOld.get("lastName"))){

                    Map<String, String> medicalRecord = getList.getMedicalRecord(personYearOld.get("firstName"),
                            personYearOld.get("lastName"));
                    personAndFirestationNumber.put("numberFirestation", numberFirestationByAddress);
                    personAndFirestationNumber.put("lastName", person.getLastName());
                    personAndFirestationNumber.put("firstName", person.getFirstName());
                    personAndFirestationNumber.put("age", personYearOld.get("year"));
                    personAndFirestationNumber.put("medications", medicalRecord.get("medications"));
                    personAndFirestationNumber.put("allergies", medicalRecord.get("allergies"));
                    personAndFirestationNumber.put("phone", person.getPhone());

                    personAndFirestationNumberByAddress.add("Firestation:" + personAndFirestationNumber.get("numberFirestation") +
                            ", LastName:" + personAndFirestationNumber.get("lastName") +
                            ", FirstName:" + personAndFirestationNumber.get("firstName") +
                            ", Age:" + personAndFirestationNumber.get("age") +
                            ", Medications:" + personAndFirestationNumber.get("medications") +
                            ", Allergies:" + personAndFirestationNumber.get("allergies") +
                            ", Phone:" + personAndFirestationNumber.get("phone"));
                }
            }
        }

        return personAndFirestationNumberByAddress;
    }
}
