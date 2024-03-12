package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.AllInfoPerson;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class URI {

    GetList getList = new GetList();

    public List<String> getPersonCoverByFirestation(String stationNumber) {

        int major = 0;
        int minor = 0;

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

    public List<AllInfoPerson> getPersonAndFirestationNumberByAddress(String address){

        List<Person> personLeaveInAddress = getList.getPersonByAddress(address);
        List<Map<String, String>> yearPerson = getList.getYear(personLeaveInAddress);

        List<AllInfoPerson> allInfosPerson = getList.allInfosPerson(personLeaveInAddress, yearPerson);

        return allInfosPerson;
    }

    public List<AllInfoPerson> getHomeCoverByFirestation(List<String> stationsNumbers){

//        List<String> addressFirestations = new ArrayList<>();
//        List<AllInfoPerson> groupPersonByAddress = new ArrayList<>();
//
//        for (String number : stationsNumbers){
//            List<String> addressFirestationByNumber = getList.getAddressFirestationByNumber(number);
//            addressFirestations.addAll(addressFirestationByNumber);
//        }
        List<String> addressFirestations = getList.allFirestationsAddress(stationsNumbers);

        List<Person> personByAddress = getList.getPersonByAddressStation(addressFirestations);
        List<Map<String, String>> listAgesPersons = getList.getYear(personByAddress);

        List<AllInfoPerson> allInfosPerson = getList.allInfosPerson(personByAddress,listAgesPersons);

        List<AllInfoPerson> sortByAddress =  getList.sortByAddress(allInfosPerson, addressFirestations);

        return sortByAddress;
    }

    public String getpersonsInfos(String firstName, String lastName){

        List<String> stationsNumbers = List.of("1","2","3","4");
        List<String> addressFirestations = getList.allFirestationsAddress(stationsNumbers);
        List<Person> personByAddress = getList.getPersonByAddressStation(addressFirestations);
        List<Map<String, String>> listAgesPersons = getList.getYear(personByAddress);
        List<AllInfoPerson> allInfosPerson = getList.allInfosPerson(personByAddress,listAgesPersons);
        List<AllInfoPerson> personInfos = new ArrayList<>();
        List<String> formatePersonInfos = new ArrayList<>();

        for (AllInfoPerson person : allInfosPerson){
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)){
                personInfos.add(person);
            }
        }

        for (AllInfoPerson info : personInfos){
            formatePersonInfos.add(
                    "firstName:"+info.getFirstName() +
                            ", lastName:"+info.getLastName() +
                            ", address:"+info.getAddress() +
                            ", age:"+info.getAge() +
                            ", email:"+info.getEmail() +
                            ", medications:"+info.getMedications() +
                            ", allergies"+info.getAllergies()
            );
        }

        JsonToObject jsonToObject = new JsonToObject();
        String personinfosToJson = jsonToObject.writeListToJson(formatePersonInfos);

        return personinfosToJson;
    }

    public String getAllEmailByCity(String city){

        List<String> emails = getList.getEmailByCity(city);

        JsonToObject jsonToObject = new JsonToObject();
        String emailsByCity = jsonToObject.writeListToJson(emails);

        return emailsByCity;
    }

}
