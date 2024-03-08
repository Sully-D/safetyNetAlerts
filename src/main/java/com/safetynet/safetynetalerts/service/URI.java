package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class URI {

    public List<String> getPersonCoverByFirestation(String stationNumber) {

        int major = 0;
        int minor = 0;

        Map<String, String> population = new HashMap<>();

        GetList getList = new GetList();
        List<String> addressStation = getList.getAddressFirestationByNumber(stationNumber);
        List<Person> personCoverByFirestation = getList.getPersonByAddressStation(addressStation);
        List<Map<String, String>> listAdultAndChild = getList.getAdultAndChild(personCoverByFirestation);

        for (Map<String, String> person : listAdultAndChild){
            if (person.containsKey("Adult")){
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

        GetList getList = new GetList();
        List<Person> listPersonAtAddress = getList.getPersonByAddress(address);
        List<Map<String, String>> listAdultAndChild = getList.getAdultAndChild(listPersonAtAddress);

        listAdultAndChild.forEach(map -> {
            Map<String, String> child = new HashMap<>();
            Map<String, String> adult = new HashMap<>();
            if (map.containsKey("Child")) {
                child.put("lastName", map.get("lastName"));
                child.put("firstName", map.get("firstName"));
                child.put("Child", map.get("Child"));
                childAtAddress.add(child.toString());
            } else if (map.containsKey("Adult")) {
                adult.put("lastName", map.get("lastName"));
                adult.put("firstName", map.get("firstName"));
                adult.put("Adult", map.get("Adult"));
                adultAtAddress.add(adult.toString());
            }
        });

        if (childAtAddress.isEmpty()) {
            return personAtAddress;
        }
        personAtAddress.add(childAtAddress.toString());
        personAtAddress.add(adultAtAddress.toString());

        return personAtAddress;
    }

    public List<String> phoneNumberByFirestation(String firestationNumber){

        GetList getList = new GetList();
        List<String> addressStation = getList.getAddressFirestationByNumber(firestationNumber);
        List<Person> personCoverByFirestation = getList.getPersonByAddressStation(addressStation);
        List<String> listPhone = new ArrayList<>();

        for (Person person : personCoverByFirestation) {
            listPhone.add(person.getPhone());
        }

        return listPhone;
    }
}
