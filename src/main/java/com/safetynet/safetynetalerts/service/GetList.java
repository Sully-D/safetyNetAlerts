package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.*;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

//@Data
@Service
public class GetList {

    private static final Logger logger = LoggerFactory.getLogger(GetList.class);

    private JsonToObject jsonToObject;
    private EncapsulateModelsPrsFstMdr readJsonData;

    @Autowired
    public GetList(JsonToObject jsonToObject) {
        this.jsonToObject = jsonToObject;
    }

    @PostConstruct
    public void init() {
        try {
            this.readJsonData = this.jsonToObject.readJsonData();
        } catch (Exception e) {
            logger.error("Failed to initialize GetList due to an error in readJsonData", e);
        }
    }


    /**
     * Retrieves a list of addresses served by a specific fire station number.
     *
     * @param stationNumber The station number to search for within the list of firestations.
     * @return A list of addresses served by the specified fire station number.
     */
    public List<String> getAddressFirestationByNumber(String stationNumber) {

        logger.info("Retrieving addresses served by fire station number: {}", stationNumber);

        List<Firestation> listFirestations = readJsonData.getFirestationList();
        if (listFirestations.isEmpty()) {
            logger.warn("The firestation list is empty.");
            return new ArrayList<>();
        }

        List<String> listAddressStations = new ArrayList<>();
        for (Firestation firestation : listFirestations) {
            if (Objects.equals(firestation.getStation(), stationNumber)) {
                listAddressStations.add(firestation.getAddress());
            }
        }

        if (listAddressStations.isEmpty()) {
            logger.warn("No addresses found for fire station number: {}", stationNumber);
        } else {
            logger.info("Found addresses for fire station number: {}. Total addresses: {}", stationNumber, listAddressStations.size());
        }

        return listAddressStations;
    }

    /**
     * Retrieves a list of persons covered by the specified fire station addresses.
     *
     * @param addressStation A list of addresses to filter persons by their coverage.
     * @return A list of persons who live at the specified addresses.
     */
    public List<Person> getPersonByAddressStation(List<String> addressStation) {

        logger.info("Retrieving persons covered by fire station addresses. Number of addresses to check: {}", addressStation != null ? addressStation.size() : 0);

        if (addressStation == null || addressStation.isEmpty()) {
            logger.warn("The list of fire station addresses is null or empty.");
            return new ArrayList<>();
        }

        List<Person> listPersons = readJsonData.getPersonList();
        if (listPersons.isEmpty()) {
            logger.warn("The person list is empty.");
            return new ArrayList<>();
        }

        List<Person> listPersonsCoverByStation = new ArrayList<>();
        for (Person person : listPersons) {
            if (addressStation.contains(person.getAddress())) {
                listPersonsCoverByStation.add(person);
            }
        }

        logger.info("Found {} persons living at the specified fire station addresses.", listPersonsCoverByStation.size());
        return listPersonsCoverByStation;
    }

    /**
     * Calculates and retrieves ages of the given list of persons.
     *
     * @param listPersons A list of persons to calculate ages for.
     * @return A list of maps, each containing personal information along with the calculated age.
     */
    public List<Map<String, String>> getAge(List<Person> listPersons) {

        logger.info("Calculating ages for a list of persons. Number of persons: {}", listPersons.size());

        if (listPersons.isEmpty()) {
            logger.warn("The list of persons is empty. No ages will be calculated.");
            return new ArrayList<>();
        }

        List<Map<String, String>> listPersonsAges = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate currentDate = LocalDate.now();
        List<Medicalrecord> listMedicalRecords = readJsonData.getMedicalrecordList();

        if (listMedicalRecords.isEmpty()) {
            logger.warn("The list of medical records is empty. Cannot calculate ages without birthdates.");
            return new ArrayList<>();
        }

        for (Person person : listPersons) {
            boolean foundRecord = false;
            for (Medicalrecord medicalrecord : listMedicalRecords) {
                if (medicalrecord.getFirstName().equals(person.getFirstName()) && medicalrecord.getLastName().equals(person.getLastName())) {
                    LocalDate birthDate = LocalDate.parse(medicalrecord.getBirthdate(), formatter);
                    Period age = Period.between(birthDate, currentDate);
                    Map<String, String> entry = new HashMap<>();
                    entry.put("firstName", person.getFirstName());
                    entry.put("lastName", person.getLastName());
                    entry.put("address", person.getAddress());
                    entry.put("phone", person.getPhone());
                    entry.put("age", String.valueOf(age.getYears()));
                    listPersonsAges.add(entry);
                    foundRecord = true;
                    break;
                }
            }
            if (!foundRecord) {
                logger.debug("No medical record found for person: {} {}", person.getFirstName(), person.getLastName());
            }
        }

        logger.info("Ages calculated for persons. Number of persons with calculated ages: {}", listPersonsAges.size());
        return listPersonsAges;
    }

    /**
     * Retrieves a list of persons who live at the specified address.
     *
     * @param address The address to filter persons by.
     * @return A list of persons living at the specified address.
     */
    public List<Person> getPersonByAddress(String address) {

        logger.info("Retrieving persons living at address: {}", address);

        if (address == null || address.isEmpty()) {
            logger.warn("Provided address is null or empty. Returning an empty list.");
            return new ArrayList<>();
        }

        List<Person> listPersons = readJsonData.getPersonList();
        if (listPersons.isEmpty()) {
            logger.warn("The person list is empty. No data to filter by address.");
            return new ArrayList<>();
        }

        List<Person> listPersonsByAddress = new ArrayList<>();
        for (Person person : listPersons) {
            if (person.getAddress().contains(address)) {
                listPersonsByAddress.add(person);
            }
        }

        if (listPersonsByAddress.isEmpty()) {
            logger.info("No persons found living at the specified address: {}", address);
        } else {
            logger.info("Found {} persons living at the specified address: {}", listPersonsByAddress.size(), address);
        }

        return listPersonsByAddress;
    }

    /**
     * Retrieves the fire station number based on the specified address.
     *
     * @param address The address to look up the fire station number for.
     * @return The fire station number serving the specified address, or null if not found.
     */
    public String getNumberFirestationByAddress(String address) {

        logger.info("Looking up fire station number for address: {}", address);

        if (address == null || address.isEmpty()) {
            logger.warn("Provided address is null or empty. Cannot perform lookup.");
            return null;
        }

        List<Firestation> listFirestations = readJsonData.getFirestationList();
        if (listFirestations.isEmpty()) {
            logger.warn("The firestation list is empty. No data available for lookup.");
            return null;
        }

        for (Firestation firestation : listFirestations) {
            if (firestation.getAddress().equals(address)) {
                logger.info("Found fire station number {} for address: {}", firestation.getStation(), address);
                return firestation.getStation();
            }
        }

        logger.warn("No fire station number found for address: {}", address);
        return null;
    }

    /**
     * Compiles comprehensive information for persons at the given addresses, including fire station coverage.
     *
     * @param listPersonsByAddress The list of persons by address.
     * @param listPersonsAges       The list of persons along with their ages.
     * @return A list of all information for each person, including fire station coverage.
     */
    public List<AllInfoPerson> allInfosPerson(List<Person> listPersonsByAddress, List<Map<String, String>> listPersonsAges) {
        logger.info("Compiling all info for persons based on address and age information. Persons count: {}", listPersonsByAddress.size());

        if (listPersonsByAddress.isEmpty() || listPersonsAges.isEmpty()) {
            logger.warn("One of the input lists (persons by address or persons ages) is empty. Returning an empty list.");
            return new ArrayList<>();
        }

        List<AllInfoPerson> listPersonsAndFirestationsNumberByAddress = new ArrayList<>();

        for (Person person : listPersonsByAddress) {
            for (Map<String, String> personAge : listPersonsAges) {
                if (person.getFirstName().equals(personAge.get("firstName")) &&
                        person.getLastName().equals(personAge.get("lastName"))) {
                    Map<String, String> mapMedicalRecords = getMedicalRecord(personAge.get("firstName"),
                            personAge.get("lastName"));
                    String numberFirestationByAddress = getNumberFirestationByAddress(person.getAddress());

                    AllInfoPerson allInfoPerson = new AllInfoPerson(numberFirestationByAddress,
                            person.getAddress(),
                            person.getLastName(),
                            person.getFirstName(),
                            personAge.get("age"),
                            mapMedicalRecords.get("medications"),
                            mapMedicalRecords.get("allergies"),
                            person.getEmail(),
                            person.getPhone()
                    );

                    listPersonsAndFirestationsNumberByAddress.add(allInfoPerson);

                    logger.debug("Added all info for person: {} {}", person.getFirstName(), person.getLastName());
                }
            }
        }

        logger.info("All info compiled for {} persons.", listPersonsAndFirestationsNumberByAddress.size());
        return listPersonsAndFirestationsNumberByAddress;
    }

    /**
     * Retrieves medical record information for a person based on their first and last name.
     *
     * @param firstName The first name of the person.
     * @param lastName  The last name of the person.
     * @return A map containing the medical record information for the person.
     */
    public Map<String, String> getMedicalRecord(String firstName, String lastName) {

        logger.info("Retrieving medical record for person: {} {}", firstName, lastName);

        List<Medicalrecord> listMedicalRecords = readJsonData.getMedicalrecordList();
        if (listMedicalRecords.isEmpty()) {
            logger.warn("The medical records list is empty. Cannot retrieve medical record for: {} {}", firstName, lastName);
            return new HashMap<>();
        }

        for (Medicalrecord medicalrecord : listMedicalRecords) {
            if (medicalrecord.getFirstName().equals(firstName) && medicalrecord.getLastName().equals(lastName)) {
                Map<String, String> mapInfoMedicalRecords = new HashMap<>();
                mapInfoMedicalRecords.put("firstName", medicalrecord.getFirstName());
                mapInfoMedicalRecords.put("lastName", medicalrecord.getLastName());
                mapInfoMedicalRecords.put("medications", String.join(", ", medicalrecord.getMedications()));
                mapInfoMedicalRecords.put("allergies", String.join(", ", medicalrecord.getAllergies()));

                logger.info("Found medical record for: {} {}", firstName, lastName);
                return mapInfoMedicalRecords;
            }
        }

        logger.warn("No medical record found for: {} {}", firstName, lastName);
        return new HashMap<>();
    }

    /**
     * Sorts a list of comprehensive information about persons by their address, according to a list of addresses.
     *
     * @param listInfoPersons        The list of persons with comprehensive information.
     * @param listFirestationAddress The list of addresses to sort by.
     * @return A sorted list of comprehensive information about persons.
     */
    public List<AllInfoPerson> sortByAddress(List<AllInfoPerson> listInfoPersons, List<String> listFirestationAddress) {

        logger.info("Starting to sort persons by address. Number of persons: {}; Number of addresses: {}", listInfoPersons.size(), listFirestationAddress.size());

        if (listInfoPersons.isEmpty() || listFirestationAddress.isEmpty()) {
            logger.warn("One or both of the input lists are empty. Returning an empty list.");
            return new ArrayList<>();
        }

        List<AllInfoPerson> listAllInfoPersons = new ArrayList<>();

        for (String address : listFirestationAddress) {
            for (AllInfoPerson person : listInfoPersons) {
                if (person.getAddress().equals(address) &&
                        !listAllInfoPersons.contains(person)) {
                    listAllInfoPersons.add(person);
                    logger.debug("Added person: {} {} to the sorted list.", person.getFirstName(), person.getLastName());
                }
            }
        }

        logger.info("Completed sorting persons by address. Total sorted persons: {}", listAllInfoPersons.size());
        return listAllInfoPersons;
    }

    /**
     * Compiles a list of addresses covered by the specified list of fire station numbers.
     *
     * @param listFirestationsNumbers The list of fire station numbers to retrieve addresses for.
     * @return A list of addresses covered by the specified fire station numbers.
     */
    public List<String> allFirestationsAddress(List<String> listFirestationsNumbers) {

        logger.info("Compiling addresses for fire station numbers. Total numbers: {}", listFirestationsNumbers.size());

        if (listFirestationsNumbers.isEmpty()) {
            logger.warn("The list of fire station numbers is empty. Returning an empty list of addresses.");
            return new ArrayList<>();
        }

        List<String> listFirestationsAddress = new ArrayList<>();

        for (String number : listFirestationsNumbers) {
            List<String> listFirestationsAddressByNumber = getAddressFirestationByNumber(number);
            if (!listFirestationsAddressByNumber.isEmpty()) {
                logger.debug("Adding addresses for fire station number: {}", number);
                listFirestationsAddress.addAll(listFirestationsAddressByNumber);
            } else {
                logger.debug("No addresses found for fire station number: {}", number);
            }
        }

        logger.info("Completed compiling addresses. Total addresses compiled: {}", listFirestationsAddress.size());
        return listFirestationsAddress;
    }

    /**
     * Retrieves a list of email addresses for persons residing in the specified city.
     *
     * @param city The city to filter persons by.
     * @return A list of email addresses for persons in the specified city.
     */
    public List<String> getEmailByCity(String city) {

        logger.info("Retrieving email addresses for persons residing in city: {}", city);

        if (city == null || city.isEmpty()) {
            logger.warn("The specified city is null or empty. Cannot retrieve emails.");
            return new ArrayList<>();
        }

        List<Person> listPersons = readJsonData.getPersonList();
        if (listPersons.isEmpty()) {
            logger.warn("The list of persons is empty. No emails can be retrieved.");
            return new ArrayList<>();
        }

        List<String> listEmails = new ArrayList<>();

        for (Person person : listPersons) {
            if (person.getCity().equalsIgnoreCase(city)) {
                listEmails.add(person.getEmail());
            }
        }

        if (listEmails.isEmpty()) {
            logger.info("No email addresses found for persons residing in city: {}", city);
        } else {
            logger.info("Retrieved {} email addresses for persons residing in city: {}", listEmails.size(), city);
        }

        return listEmails;
    }

    /**
     * Retrieves a list of all unique fire station numbers.
     *
     * @return A list of unique fire station numbers.
     */
    public List<String> getFirestationNumber() {

        logger.info("Retrieving list of all unique fire station numbers.");

        List<Firestation> listFirestations = readJsonData.getFirestationList();
        if (listFirestations.isEmpty()) {
            logger.warn("The list of firestations is empty. No fire station numbers can be retrieved.");
            return new ArrayList<>();
        }

        Set<String> uniqueNumbers = new HashSet<>();
        for (Firestation firestation : listFirestations) {
            uniqueNumbers.add(firestation.getStation());
        }

        List<String> listNumbers = new ArrayList<>(uniqueNumbers);
        logger.info("Successfully retrieved unique fire station numbers. Total unique numbers: {}", listNumbers.size());

        return listNumbers;
    }
}
