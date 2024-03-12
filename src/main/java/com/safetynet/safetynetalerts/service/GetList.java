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

    /**
     * Retrieves a list of addresses served by a specific fire station number.
     *
     * @param stationNumber The station number to search for within the list of firestations.
     * @return A list of addresses served by the specified fire station number.
     */
    public static List<String> getAddressFirestationByNumber(String stationNumber) {
        List<Firestation> listFirestations = readJsonData.getFirestationList();
        List<String> listAddressStations = new ArrayList<>();

        for (Firestation firestation : listFirestations) {
            if (firestation.getStation().equals(stationNumber)) {
                listAddressStations.add(firestation.getAddress());
            }
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
        List<Person> listPersons = readJsonData.getPersonList();
        List<Person> listPersonsCoverByStation = new ArrayList<>();

        for (Person person : listPersons) {
            if (addressStation != null && addressStation.contains(person.getAddress())) {
                listPersonsCoverByStation.add(person);
            }
        }
        return listPersonsCoverByStation;
    }

    /**
     * Calculates and retrieves ages of the given list of persons.
     *
     * @param listPersons A list of persons to calculate ages for.
     * @return A list of maps, each containing personal information along with the calculated age.
     */
    public List<Map<String, String>> getAge(List<Person> listPersons) {
        List<Map<String, String>> listPersonsAges = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate currentDate = LocalDate.now();
        List<Medicalrecord> listMedicalRecords = readJsonData.getMedicalrecordList();

        for (Person person : listPersons) {
            for (Medicalrecord medicalrecord : listMedicalRecords) {
                if (medicalrecord.getFirstName().equals(person.getFirstName()) && medicalrecord.getLastName().equals(person.getLastName())) {
                    LocalDate birthDate = LocalDate.parse(medicalrecord.getBirthdate(), formatter);
                    Period age = Period.between(birthDate, currentDate);
                    Map<String, String> entry = new HashMap<>();
                    entry.put("firstName", person.getFirstName());
                    entry.put("lastName", person.getLastName());
                    entry.put("address", person.getAddress());
                    entry.put("phone", person.getPhone());
                    entry.put("year", String.valueOf(age.getYears()));
                    listPersonsAges.add(entry);
                }
            }
        }
        return listPersonsAges;
    }

    /**
     * Retrieves a list of persons who live at the specified address.
     *
     * @param address The address to filter persons by.
     * @return A list of persons living at the specified address.
     */
    public List<Person> getPersonByAddress(String address) {
        List<Person> listPersons = readJsonData.getPersonList();
        List<Person> listPersonsByAddress = new ArrayList<>();

        for (Person person : listPersons) {
            if (address != null && person.getAddress().contains(address)) {
                listPersonsByAddress.add(person);
            }
        }
        return listPersonsByAddress;
    }

    /**
     * Retrieves the fire station number based on the specified address.
     *
     * @param address The address to look up the fire station number for.
     * @return The fire station number serving the specified address, or null if not found.
     */
    public static String getNumberFirestationByAddress(String address) {
        List<Firestation> listFirestations = readJsonData.getFirestationList();

        for (Firestation firestation : listFirestations) {
            if (firestation.getAddress().equals(address)) {
                return firestation.getStation();
            }
        }
        return null;
    }

    /**
     * Compiles comprehensive information for persons at the given addresses, including fire station coverage.
     *
     * @param listPersonsByAddress The list of persons by address.
     * @param listPeronsAges       The list of persons along with their ages.
     * @return A list of all information for each person, including fire station coverage.
     */
    public List<AllInfoPerson> allInfosPerson(List<Person> listPersonsByAddress, List<Map<String, String>> listPeronsAges) {
        List<AllInfoPerson> listPersonsAndFirestationsNumberByAddress = new ArrayList<>();

        for (Person person : listPersonsByAddress) {
            for (Map<String, String> personAge : listPeronsAges) {
                if (person.getFirstName().equals(personAge.get("firstName")) &&
                        person.getLastName().equals(personAge.get("lastName"))) {
                    Map<String, String> mapMedicalRecords = GetList.getMedicalRecord(personAge.get("firstName"),
                            personAge.get("lastName"));
                    String numberFirestationByAddress = GetList.getNumberFirestationByAddress(person.getAddress());

                    AllInfoPerson allInfoPerson = new AllInfoPerson();
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

    /**
     * Retrieves medical record information for a person based on their first and last name.
     *
     * @param firstName The first name of the person.
     * @param lastName  The last name of the person.
     * @return A map containing the medical record information for the person.
     */
    public static Map<String, String> getMedicalRecord(String firstName, String lastName) {
        List<Medicalrecord> listMedicalRecords = readJsonData.getMedicalrecordList();
        Map<String, String> mapInfoMedicalRecords = new HashMap<>();

        for (Medicalrecord medicalrecord : listMedicalRecords) {
            if (medicalrecord.getFirstName().equals(firstName) && medicalrecord.getLastName().equals(lastName)) {
                mapInfoMedicalRecords.put("firstName", medicalrecord.getFirstName());
                mapInfoMedicalRecords.put("lastName", medicalrecord.getLastName());
                mapInfoMedicalRecords.put("medications", medicalrecord.getMedications().toString());
                mapInfoMedicalRecords.put("allergies", medicalrecord.getAllergies().toString());
            }
        }
        return mapInfoMedicalRecords;
    }

    /**
     * Sorts a list of comprehensive information about persons by their address, according to a list of addresses.
     *
     * @param listInfoPersons        The list of persons with comprehensive information.
     * @param listFirestationAddress The list of addresses to sort by.
     * @return A sorted list of comprehensive information about persons.
     */
    public List<AllInfoPerson> sortByAddress(List<AllInfoPerson> listInfoPersons, List<String> listFirestationAddress) {
        List<AllInfoPerson> listAllInfoPersons = new ArrayList<>();

        for (String address : listFirestationAddress) {
            for (AllInfoPerson person : listInfoPersons) {
                if (person.getAddress().equals(address) &&
                        !listAllInfoPersons.contains(person)) {
                    listAllInfoPersons.add(person);
                }
            }
        }
        return listAllInfoPersons;
    }

    /**
     * Compiles a list of addresses covered by the specified list of fire station numbers.
     *
     * @param listFirestationsNumbers The list of fire station numbers to retrieve addresses for.
     * @return A list of addresses covered by the specified fire station numbers.
     */
    public List<String> allFirestationsAddress(List<String> listFirestationsNumbers) {
        List<String> listFirestationsAddress = new ArrayList<>();

        for (String number : listFirestationsNumbers) {
            List<String> listFirestationsAddressByNumber = GetList.getAddressFirestationByNumber(number);
            listFirestationsAddress.addAll(listFirestationsAddressByNumber);
        }

        return listFirestationsAddress;
    }

    /**
     * Retrieves a list of email addresses for persons residing in the specified city.
     *
     * @param city The city to filter persons by.
     * @return A list of email addresses for persons in the specified city.
     */
    public List<String> getEmailByCity(String city) {
        List<Person> listPersons = readJsonData.getPersonList();
        List<String> listEmails = new ArrayList<>();

        for (Person person : listPersons) {
            if (person.getCity().equals(city)) {
                listEmails.add(person.getEmail());
            }
        }

        return listEmails;
    }

    /**
     * Retrieves a list of all unique fire station numbers.
     *
     * @return A list of unique fire station numbers.
     */
    public List<String> getFirestationNumber() {
        List<Firestation> listFirestations = readJsonData.getFirestationList();
        List<String> listNumbers = new ArrayList<>();

        for (Firestation firestation : listFirestations) {
            if (!listNumbers.contains(firestation.getStation())) {
                listNumbers.add(firestation.getStation());
            }
        }
        return listNumbers;
    }
}
