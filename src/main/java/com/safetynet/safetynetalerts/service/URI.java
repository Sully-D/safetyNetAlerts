package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.AllInfoPerson;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class that offers functionalities to query information related to fire stations and the people they cover.
 * It provides methods to retrieve lists of people by fire station, children at an address, phone numbers by fire station,
 * detailed person and fire station information by address, and more.
 */
@Service
public class URI {

    //GetList getList = new GetList();
    @Autowired
    GetList getList;

    /**
     * Retrieves a list containing the number of adults and minors covered by a specific fire station, along with
     * a detailed list of all people covered by the fire station.
     *
     * @param stationNumber The station number to query for.
     * @return A List of Strings with the first element being a summary count of adults and minors, and the second
     * element being detailed age information of all individuals covered.
     */
    public List<String> getPersonsCoverByFirestation(String stationNumber) {
        int major = 0; // Count of adults
        int minor = 0; // Count of minors

        // Retrieves addresses associated with the given fire station number
        List<String> listFirestationsAddress = getList.getAddressFirestationByNumber(stationNumber);
        // Retrieves persons living at the obtained addresses
        List<Person> listPersonsCoverByFirestation = getList.getPersonByAddressStation(listFirestationsAddress);
        // Calculates ages of persons covered by the fire station
        List<Map<String, String>> listPersonsAges = getList.getAge(listPersonsCoverByFirestation);

        // Categorizing as major or minor based on age
        for (Map<String, String> person : listPersonsAges){
            int year = Integer.parseInt(person.get("year"));
            if (year > 18){
                major++;
            } else {
                minor++;
            }
        }

        // Preparing the output list
        List<String> listPersonsCoverByFirestationWithAges = new ArrayList<>();
        String[] nbAdultAndMinor = {"nbAdult=" + major, "nbMinor=" + minor};
        listPersonsCoverByFirestationWithAges.add(Arrays.toString(nbAdultAndMinor));
        listPersonsCoverByFirestationWithAges.add(listPersonsAges.toString());

        return listPersonsCoverByFirestationWithAges;
    }

    /**
     * Retrieves a list of children living at a specified address. If no children are found, returns null.
     * Each child's last name, first name, and type ("Child") are included in the list.
     *
     * @param address The address to search for children at.
     * @return A List of Strings detailing each child found at the address or null if no children are found.
     */
    public List<String> getChildrenAtAddress(String address) {
        List<String> listPersonsAtAddress = new ArrayList<>();
        // Retrieve all persons living at the specified address
        List<Person> listPersonsByAddress = getList.getPersonByAddress(address);
        // Calculate ages for persons at the address
        List<Map<String, String>> listPersonsWithAges = getList.getAge(listPersonsByAddress);

        // Separating children from adults
        for (Map<String, String> person : listPersonsWithAges) {
            Map<String, String> personDetails = new HashMap<>();
            personDetails.put("lastName", person.get("lastName"));
            personDetails.put("firstName", person.get("firstName"));

            int year = Integer.parseInt(person.get("year"));
            if (year <= 18) {
                personDetails.put("type", "Child");
            } else {
                personDetails.put("type", "Adult");
            }
            listPersonsAtAddress.add(personDetails.toString());
        }

        // Check if there are children in the list to return it; otherwise, return null
        return listPersonsAtAddress.stream().anyMatch(person -> person.contains("Child")) ? listPersonsAtAddress : null;
    }

    /**
     * Retrieves a list of phone numbers for residents covered by a specified fire station.
     *
     * @param firestationNumber The fire station number to query for.
     * @return A list of unique phone numbers of residents covered by the specified fire station.
     */
    public List<String> getPhonesNumbersByFirestation(String firestationNumber){
        // Retrieves addresses associated with the given fire station number
        List<String> listFirestationsAddress = getList.getAddressFirestationByNumber(firestationNumber);
        // Retrieves persons living at the obtained addresses
        List<Person> listPersonsCoverByFirestation = getList.getPersonByAddressStation(listFirestationsAddress);

        List<String> listPhonesTemp = new ArrayList<>();
        // Collecting phone numbers
        for (Person person : listPersonsCoverByFirestation) {
            listPhonesTemp.add(person.getPhone());
        }

        // Removing duplicate phone numbers
        List<String> listPhones = new ArrayList<>(new HashSet<>(listPhonesTemp));

        return listPhones;
    }

    /**
     * Retrieves detailed information about persons and the fire station number covering their address.
     *
     * @param address The address to query for persons and their fire station number.
     * @return A list of {@link AllInfoPerson} containing detailed information about each person at the address and their associated fire station number.
     */
    public List<AllInfoPerson> getPersonsAndFirestationNumberByAddress(String address){
        // Retrieve all persons living at the specified address
        List<Person> listPersonsByAddress = getList.getPersonByAddress(address);
        // Calculate ages for persons at the address
        List<Map<String, String>> listPersonsWithAges = getList.getAge(listPersonsByAddress);
        // Combining personal info with age information
        List<AllInfoPerson> listAllInfoPersons = getList.allInfosPerson(listPersonsByAddress, listPersonsWithAges);

        return listAllInfoPersons;
    }

    /**
     * Retrieves detailed information about persons covered by a list of fire station numbers, sorted by their address.
     *
     * @param stationsNumbers A list of fire station numbers to query for.
     * @return A sorted list of {@link AllInfoPerson} by address for the specified fire station numbers.
     */
    public List<AllInfoPerson> getAddressCoverByFirestation(List<String> stationsNumbers){
        // Retrieve addresses covered by the specified list of fire station numbers
        List<String> listFirestationAddress = getList.allFirestationsAddress(stationsNumbers);
        // Retrieve persons living at the obtained addresses
        List<Person> listPersonsByAddressStation = getList.getPersonByAddressStation(listFirestationAddress);
        // Calculate ages for persons covered by the fire stations
        List<Map<String, String>> listAgesPersons = getList.getAge(listPersonsByAddressStation);
        // Combining personal info with age and address information
        List<AllInfoPerson> listAllInfoPersons = getList.allInfosPerson(listPersonsByAddressStation, listAgesPersons);
        // Sorting the combined information by address
        List<AllInfoPerson> listAllInfoPersonsSortByAddress = getList.sortByAddress(listAllInfoPersons, listFirestationAddress);

        return listAllInfoPersonsSortByAddress;
    }

    /**
     * Retrieves formatted information about a person identified by first and last name, across all addresses.
     *
     * @param firstName The first name of the person to query for.
     * @param lastName The last name of the person to query for.
     * @return A string representing a JSON containing detailed information about the person, including personal details and medical information.
     */
    public String getPersonsInfo(String firstName, String lastName){
        // Retrieve all fire station numbers and their associated addresses
        List<String> listFirestationsNumbers = getList.getFirestationNumber();
        List<String> listFirestationsAddress = getList.allFirestationsAddress(listFirestationsNumbers);
        // Retrieve persons living at the obtained addresses
        List<Person> listPersonsByAddress = getList.getPersonByAddressStation(listFirestationsAddress);
        // Calculate ages for the retrieved persons
        List<Map<String, String>> listAgesPersons = getList.getAge(listPersonsByAddress);
        // Combining personal info with age information
        List<AllInfoPerson> listAllInfoPersons = getList.allInfosPerson(listPersonsByAddress, listAgesPersons);

        // Filtering for the specific person
        List<AllInfoPerson> listPersonsInfos = listAllInfoPersons.stream()
                .filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName))
                .collect(Collectors.toList());

        // Formatting the information of the filtered persons
        List<String> listFormateInfoPersons = listPersonsInfos.stream()
                .map(info -> String.format(
                        "firstName:%s, lastName:%s, address:%s, age:%s, email:%s, medications:%s, allergies%s",
                        info.getFirstName(), info.getLastName(), info.getAddress(), info.getAge(),
                        info.getEmail(), info.getMedications(), info.getAllergies()))
                .collect(Collectors.toList());

        // Converting the formatted information to JSON
        JsonToObject jsonToObject = new JsonToObject();
        String formateInfosPersonsToJson = jsonToObject.writeListToJson(listFormateInfoPersons);

        return formateInfosPersonsToJson;
    }

    /**
     * Retrieves all emails of residents within a specified city.
     *
     * @param city The city to query for resident emails.
     * @return A string representing a JSON containing all the emails of residents within the specified city.
     */
    public String getAllEmailsByCity(String city){
        // Retrieve emails for residents in the specified city
        List<String> listEmails = getList.getEmailByCity(city);
        // Converting the list of emails to JSON
        JsonToObject jsonToObject = new JsonToObject();
        String emailsByCity = jsonToObject.writeListToJson(listEmails);

        return emailsByCity;
    }
}
