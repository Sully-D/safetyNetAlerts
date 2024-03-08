package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        listPersonCoverByFirestation.add("Adult : " + major);
        listPersonCoverByFirestation.add("Minor : " + minor);
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

        URI uri = new URI();
        List<String> listPersonByFirestation = uri.getPersonCoverByFirestation(firestationNumber);
        List<String> listPhone = new ArrayList<>();

//        listPersonByFirestation.forEach(phone -> {
//            listPhone.add(phone.)
//        });


        return null;
    }
}
