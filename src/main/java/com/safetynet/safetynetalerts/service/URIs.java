package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.AllInfoPerson;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class URIs {

    private static final Logger logger = LoggerFactory.getLogger(URIs.class);

//    JsonToObject jsonToObject = new JsonToObject();
//    GetList getList = new GetList(jsonToObject);
    @Autowired
    JsonToObject jsonToObject;
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

        logger.info("Start of getPersonsCoverByFirestation for station number {}", stationNumber);

        int major = 0; // Count of adults
        int minor = 0; // Count of minors

        try {
            // Retrieves addresses associated with the given fire station number
            logger.debug("Retrieve addresses associated with station number {}", stationNumber);
            List<String> listFirestationsAddress = getList.getAddressFirestationByNumber(stationNumber);

            // Retrieves persons living at the obtained addresses
            logger.debug("Recovery of people living at addresses obtained for station number {}", stationNumber);
            List<Person> listPersonsCoverByFirestation = getList.getPersonByAddressStation(listFirestationsAddress);

            // Calculates ages of persons covered by the fire station
            logger.debug("Calculating the ages of people covered by station number {}", stationNumber);
            List<Map<String, String>> listPersonsAges = getList.getAge(listPersonsCoverByFirestation);

            // Categorizing as major or minor based on age
            for (Map<String, String> person : listPersonsAges) {
                int age = Integer.parseInt(person.get("age"));
                if (age > 18) {
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

            logger.info("Operation completed for station number {}. Number of adults: {}, Number of minors: {}", stationNumber, major, minor);
            return listPersonsCoverByFirestationWithAges;

        } catch (Exception e) {
            logger.error("Error when retrieving persons covered by fire station number " + stationNumber, e);
            return Collections.emptyList(); // return void list if error
        }
    }

    /**
     * Retrieves a list of children living at a specified address. If no children are found, returns null.
     * Each child's last name, first name, and type ("Child") are included in the list.
     *
     * @param address The address to search for children at.
     * @return A List of Strings detailing each child found at the address or null if no children are found.
     */
    public List<String> getChildrenAtAddress(String address) {

        logger.info("Search for children at address: {}", address);
        List<String> listPersonsAtAddress = new ArrayList<>();

        try {
            // Retrieve all persons living at the specified address
            logger.debug("Recover people at address: {}", address);
            List<Person> listPersonsByAddress = getList.getPersonByAddress(address);
            // Calculate ages for persons at the address
            logger.debug("Calculating the ages of people at address: {}", address);
            List<Map<String, String>> listPersonsWithAges = getList.getAge(listPersonsByAddress);

            // Separating children from adults
            for (Map<String, String> person : listPersonsWithAges) {
                Map<String, String> personDetails = new HashMap<>();
                personDetails.put("lastName", person.get("lastName"));
                personDetails.put("firstName", person.get("firstName"));
                personDetails.put("age", person.get("age"));

                int age = Integer.parseInt(person.get("age"));
                if (age <= 18) {
                    personDetails.put("type", "Child");
                    logger.debug("Foundling: {} {}", person.get("firstName"), person.get("lastName"));
                } else {
                    personDetails.put("type", "Adult");
                }
                listPersonsAtAddress.add(personDetails.toString());
            }

            // Check if there are children in the list
            boolean hasChildren = listPersonsAtAddress.stream().anyMatch(person -> person.contains("Child"));
            if (!hasChildren) {
                logger.info("No children found at address: {}", address);
                return Collections.emptyList();
            }

            logger.info("Foundlings at address: {}. Total: {}", address, listPersonsAtAddress.size());
            return listPersonsAtAddress;

        } catch (Exception e) {
            logger.error("Error retrieving children from address " + address, e);
            return Collections.emptyList(); // Retourne une liste vide en cas d'erreur
        }
    }

    /**
     * Retrieves a list of phone numbers for residents covered by a specified fire station.
     *
     * @param firestationNumber The fire station number to query for.
     * @return A list of unique phone numbers of residents covered by the specified fire station.
     */
    public List<String> getPhonesNumbersByFirestation(String firestationNumber){
        logger.info("Starting to retrieve phone numbers for firestation number: {}", firestationNumber);

        // Retrieves addresses associated with the given fire station number
        logger.debug("Retrieving addresses associated with firestation number: {}", firestationNumber);
        List<String> listFirestationsAddress = getList.getAddressFirestationByNumber(firestationNumber);

        if (listFirestationsAddress.isEmpty()) {
            logger.warn("No addresses found for firestation number: {}", firestationNumber);
            return Collections.emptyList(); // Return an empty list to avoid null pointer exceptions downstream
        }

        // Retrieves persons living at the obtained addresses
        logger.debug("Retrieving persons living at the obtained addresses");
        List<Person> listPersonsCoverByFirestation = getList.getPersonByAddressStation(listFirestationsAddress);

        if (listPersonsCoverByFirestation.isEmpty()) {
            logger.warn("No persons found living at the addresses associated with firestation number: {}", firestationNumber);
            return Collections.emptyList();
        }

        List<String> listPhonesTemp = new ArrayList<>();
        // Collecting phone numbers
        logger.debug("Collecting phone numbers for the addresses");
        for (Person person : listPersonsCoverByFirestation) {
            listPhonesTemp.add(person.getPhone());
        }

        // Removing duplicate phone numbers
        logger.debug("Removing duplicate phone numbers from the list");
        List<String> listPhones = new ArrayList<>(new HashSet<>(listPhonesTemp));

        logger.info("Successfully retrieved phone numbers for firestation number: {}. Total unique phone numbers: {}", firestationNumber, listPhones.size());
        return listPhones;
    }

    /**
     * Retrieves detailed information about persons and the fire station number covering their address.
     *
     * @param address The address to query for persons and their fire station number.
     * @return A list of {@link AllInfoPerson} containing detailed information about each person at the address and their associated fire station number.
     */
    public List<String> getPersonsAndFirestationNumberByAddress(String address){
        logger.info("Retrieving detailed information for residents and their fire station number at address: {}", address);

        // Retrieve all persons living at the specified address
        logger.debug("Retrieving all persons living at the specified address: {}", address);
        List<Person> listPersonsByAddress = getList.getPersonByAddress(address);

        if (listPersonsByAddress.isEmpty()) {
            logger.warn("No persons found at the specified address: {}", address);
            return Collections.emptyList(); // Return an empty list to signify no data was found
        }

        // Calculate ages for persons at the address
        logger.debug("Calculating ages for persons at the address: {}", address);
        List<Map<String, String>> listPersonsWithAges = getList.getAge(listPersonsByAddress);

        // Combining personal info with age information
        logger.debug("Combining personal information with age data");
        List<AllInfoPerson> listAllInfoPersons = getList.allInfosPerson(listPersonsByAddress, listPersonsWithAges);

        // Formatting the information of the filtered persons
        List<String> listFormatInfoPersons = listAllInfoPersons.stream()
                .map(info -> String.format(
                        "firestation:%s, firstName:%s, lastName:%s, phone:%s, age:%s, medications:%s, allergies:%s",
                        info.getFirestation(), info.getFirstName(), info.getLastName(), info.getPhone(), info.getAge(),
                        info.getMedications(), info.getAllergies()))
                .collect(Collectors.toList());

        logger.info("Successfully retrieved detailed information for {} residents at address: {}", listAllInfoPersons.size(), address);
        return listFormatInfoPersons;
    }

    /**
     * Retrieves detailed information about persons covered by a list of fire station numbers, sorted by their address.
     *
     * @param stationsNumbers A list of fire station numbers to query for.
     * @return A sorted list of {@link AllInfoPerson} by address for the specified fire station numbers.
     */
    public List<String> getAddressCoverByFirestation(List<String> stationsNumbers){

        logger.info("Retrieving detailed information for persons covered by fire station numbers: {}", stationsNumbers);

        // Retrieve addresses covered by the specified list of fire station numbers
        logger.debug("Retrieving addresses covered by fire stations: {}", stationsNumbers);
        List<String> listFirestationAddress = getList.allFirestationsAddress(stationsNumbers);

        if (listFirestationAddress.isEmpty()) {
            logger.warn("No addresses found for fire station numbers: {}", stationsNumbers);
            return Collections.emptyList(); // Return an empty list to avoid proceeding with no data
        }

        // Retrieve persons living at the obtained addresses
        logger.debug("Retrieving persons living at the obtained addresses");
        List<Person> listPersonsByAddressStation = getList.getPersonByAddressStation(listFirestationAddress);

        if (listPersonsByAddressStation.isEmpty()) {
            logger.warn("No persons found living at the addresses covered by the specified fire stations");
            return Collections.emptyList();
        }

        // Calculate ages for persons covered by the fire stations
        logger.debug("Calculating ages for persons covered by the fire stations");
        List<Map<String, String>> listAgesPersons = getList.getAge(listPersonsByAddressStation);

        // Combining personal info with age and address information
        logger.debug("Combining personal information with age and address data");
        List<AllInfoPerson> listAllInfoPersons = getList.allInfosPerson(listPersonsByAddressStation, listAgesPersons);

        // Sorting the combined information by address
        logger.debug("Sorting the combined information by address");
        List<AllInfoPerson> listAllInfoPersonsSortByAddress = getList.sortByAddress(listAllInfoPersons, listFirestationAddress);

        // Formatting the information of the filtered persons
        List<String> listFormatInfoPersons = listAllInfoPersons.stream()
                .map(info -> String.format(
                        "address:%s, firstName:%s, lastName:%s, phone:%s, age:%s, medications:%s, allergies:%s",
                        info.getAddress(), info.getFirstName(), info.getLastName(), info.getPhone(), info.getAge(),
                        info.getMedications(), info.getAllergies()))
                .collect(Collectors.toList());

        logger.info("Successfully retrieved and sorted detailed information for persons covered by fire stations. Total persons: {}", listAllInfoPersonsSortByAddress.size());
        return listFormatInfoPersons;
    }

    /**
     * Retrieves formatted information about a person identified by first and last name, across all addresses.
     *
     * @param firstName The first name of the person to query for.
     * @param lastName  The last name of the person to query for.
     * @return A string representing a JSON containing detailed information about the person, including personal details and medical information.
     */
    public List<String> getPersonsInfo(String firstName, String lastName){

        logger.info("Retrieving information for person: {} {}", firstName, lastName);

        // Retrieve all fire station numbers and their associated addresses
        logger.debug("Retrieving all fire station numbers and their associated addresses");
        List<String> listFirestationsNumbers = getList.getFirestationNumber();
        List<String> listFirestationsAddress = getList.allFirestationsAddress(listFirestationsNumbers);

        // Retrieve persons living at the obtained addresses
        logger.debug("Retrieving persons living at the obtained addresses");
        List<Person> listPersonsByAddress = getList.getPersonByAddressStation(listFirestationsAddress);

        // Calculate ages for the retrieved persons
        logger.debug("Calculating ages for the retrieved persons");
        List<Map<String, String>> listAgesPersons = getList.getAge(listPersonsByAddress);

        // Combining personal info with age information
        logger.debug("Combining personal info with age information");
        List<AllInfoPerson> listAllInfoPersons = getList.allInfosPerson(listPersonsByAddress, listAgesPersons);

        // Filtering for the specific person
        logger.debug("Filtering information for the specific person: {} {}", firstName, lastName);
        List<AllInfoPerson> listPersonsInfo = listAllInfoPersons.stream()
                .filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName))
                .toList();

        if (listPersonsInfo.isEmpty()) {
            logger.warn("No information found for person: {} {}", firstName, lastName);
            return Collections.emptyList();
        }

        // Formatting the information of the filtered persons
        List<String> listFormatInfoPersons = listPersonsInfo.stream()
                .map(info -> String.format(
                        "firstName:%s, lastName:%s, address:%s, age:%s, email:%s, medications:%s, allergies:%s",
                        info.getFirstName(), info.getLastName(), info.getAddress(), info.getAge(),
                        info.getEmail(), info.getMedications(), info.getAllergies()))
                .collect(Collectors.toList());

        logger.info("Successfully retrieved information for person: {} {}", firstName, lastName);
        return listFormatInfoPersons;
    }

    /**
     * Retrieves all emails of residents within a specified city.
     *
     * @param city The city to query for resident emails.
     * @return A string representing a JSON containing all the emails of residents within the specified city.
     */
    public List<String> getAllEmailsByCity(String city){

        logger.info("Retrieving all emails for residents in city: {}", city);

        // Retrieve emails for residents in the specified city
        List<String> listEmails = getList.getEmailByCity(city);

        if (listEmails.isEmpty()) {
            logger.warn("No emails found for residents in city: {}", city);
            return Collections.emptyList();
        }

        logger.info("Successfully retrieved and converted emails for city: {}. Total emails: {}", city, listEmails.size());
        return listEmails;
    }
}
