package com.safetynet.safetynetalerts;

import com.safetynet.safetynetalerts.model.*;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import com.safetynet.safetynetalerts.service.GetList;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetListTest {

    @Mock
    private JsonToObject jsonToObject;

    @InjectMocks
    private GetList getList;

    @Mock
    private EncapsulateModelsPrsFstMdr readJsonData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jsonToObject.readJsonData()).thenReturn(readJsonData);
        getList.init();
    }


    @Test
    void init_whenReadJsonDataThrowsException_shouldHandleException() {
        // GIVEN
        doThrow(new RuntimeException("Failed to read JSON data")).when(jsonToObject).readJsonData();

        // WHEN & THEN
        getList.init();
    }

    @Test
    void getAddressFirestationByNumber_returnsCorrectAddresses() {
        // GIVEN
        Firestation station1 = new Firestation("Address 1", "1");
        Firestation station2 = new Firestation("Address 2", "2");
        List<Firestation> mockedList = Arrays.asList(station1, station2);

        when(readJsonData.getFirestationList()).thenReturn(mockedList);

        // WHEN
        List<String> addresses = getList.getAddressFirestationByNumber("1");

        // THEN
        assertEquals(1, addresses.size());
    }

    @Test
    void whenListFirestationsIsEmpty_thenReturnsEmptyList() {
        // GIVEN
        List<Firestation> mockData = Arrays.asList();
        when(readJsonData.getFirestationList()).thenReturn(mockData);

        // WHEN
        List<String> result = getList.getAddressFirestationByNumber("1");

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonByAddressStation_returnsCorrectStations(){

        // GIVEN
        List<String> addressStation = Arrays.asList("Address 1", "Address 3");

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 2", "City", "Zip", "Phone", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "Phone", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        // WHEN
        when(readJsonData.getPersonList()).thenReturn(listPersons);
        List<Person> result = getList.getPersonByAddressStation(addressStation);

        // THEN
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(person -> person.getFirstName().equals("John")));
        assertTrue(result.stream().anyMatch(person -> person.getFirstName().equals("Jim")));
    }

    @Test
    void getPersonByAddressStation_whenAddressStationIsNull_shouldReturnEmptyList() {
        List<String> addressStation = null;

        List<Person> result = getList.getPersonByAddressStation(addressStation);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonByAddressStation_whenAddressStationIsEmpty_shouldReturnEmptyList() {
        List<String> addressStation = Collections.emptyList();

        List<Person> result = getList.getPersonByAddressStation(addressStation);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonByAddressStation_whenPersonListIsEmpty_shouldReturnEmptyList() {
        List<String> addressStation = Arrays.asList("Address 1");
        when(readJsonData.getPersonList()).thenReturn(Collections.emptyList());

        List<Person> result = getList.getPersonByAddressStation(addressStation);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonByAddressStation_whenNoPersonMatchesAddress_shouldReturnEmptyList() {
        List<String> addressStation = Arrays.asList("Non-existing address");
        List<Person> mockPersonList = Arrays.asList(
                new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email"),
                new Person("Jane", "Doe", "Address 2", "City", "Zip", "Phone", "Email")
        );
        when(readJsonData.getPersonList()).thenReturn(mockPersonList);

        List<Person> result = getList.getPersonByAddressStation(addressStation);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAge_returnListPersonsWithAges(){

        // GIVEN
        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 2", "City", "Zip", "Phone", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "Phone", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        Medicalrecord medicalrecord1 = new Medicalrecord("John", "Doe", "03/06/1989",
                Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"),
                Arrays.asList("")
        );
        Medicalrecord medicalrecord2 = new Medicalrecord("Jane", "Doe", "03/06/1992",
                Arrays.asList(),
                Arrays.asList("peanut")
        );
        Medicalrecord medicalrecord3 = new Medicalrecord("Jim", "Test", "03/06/2010",
                Arrays.asList(),
                Arrays.asList("")
        );
        List<Medicalrecord> listMedicalrecords = Arrays.asList(medicalrecord1, medicalrecord2, medicalrecord3);

        // WHEN
        when(readJsonData.getMedicalrecordList()).thenReturn(listMedicalrecords);
        List<Map<String, String>> result = getList.getAge(listPersons);

        // THEN
        Map<String, String> firstPersonAge = result.get(0);
        assertEquals("John", firstPersonAge.get("firstName"));
        assertEquals("Doe", firstPersonAge.get("lastName"));
        assertEquals("35", firstPersonAge.get("age"));
    }

    @Test
    void getAge_whenListPersonsIsEmpty_shouldReturnEmptyList() {
        List<Person> listPersons = Collections.emptyList();

        List<Map<String, String>> result = getList.getAge(listPersons);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAge_whenListMedicalRecordsIsEmpty_shouldReturnEmptyList() {
        List<Person> listPersons = Arrays.asList(new Person("John", "Doe", "Address 1",
                "Test", "12345", "555-1234", "john.doe@example.com"));
        when(readJsonData.getMedicalrecordList()).thenReturn(Collections.emptyList());

        List<Map<String, String>> result = getList.getAge(listPersons);

        assertTrue(result.isEmpty());
    }


    @Test
    void getPersonByAddress_returnListPersons(){

        // GIVEN
        String address = "Address 1";

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 2", "City", "Zip", "Phone", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "Phone", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        // WHEN
        when(readJsonData.getPersonList()).thenReturn(listPersons);
        List<Person> result = getList.getPersonByAddress(address);

        // THEN
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(person -> person.getFirstName().equals("John")));
    }

    @Test
    void getPersonByAddress_whenAddressIsEmptyOrNull_shouldReturnEmptyList() {
        // Test with null address
        List<Person> resultWithNullAddress = getList.getPersonByAddress(null);
        assertTrue(resultWithNullAddress.isEmpty());

        // Test with empty address
        List<Person> resultWithEmptyAddress = getList.getPersonByAddress("");
        assertTrue(resultWithEmptyAddress.isEmpty());
    }

    @Test
    void getPersonByAddress_whenPersonListIsEmpty_shouldReturnEmptyList() {
        when(readJsonData.getPersonList()).thenReturn(Collections.emptyList());

        List<Person> result = getList.getPersonByAddress("Address 1");

        assertTrue(result.isEmpty());
    }

    @Test
    void getNumberFirestationByAddress_returnNumberStation(){

        // GIVEN
        String address = "Address 1";

        Firestation firestation1 = new Firestation("Address 1", "1");
        Firestation firestation2 = new Firestation("Address 2", "2");
        Firestation firestation3 = new Firestation("Address 3", "3");
        List<Firestation> listFirestations = Arrays.asList(firestation1, firestation2, firestation3);

        // WHEN
        when(readJsonData.getFirestationList()).thenReturn(listFirestations);
        String result = getList.getNumberFirestationByAddress(address);

        // THEN
        assertEquals("1", result);
    }

    @Test
    void getNumberFirestationByAddress_whenAddressIsEmptyOrNull_returnsNullAndLogsWarning() {
        assertNull(getList.getNumberFirestationByAddress(null));

        assertNull(getList.getNumberFirestationByAddress(""));
    }

    @Test
    void getNumberFirestationByAddress_whenFirestationListIsEmpty_returnsNullAndLogsWarning() {
        when(readJsonData.getFirestationList()).thenReturn(Collections.emptyList());

        assertNull(getList.getNumberFirestationByAddress("Address 1"));
    }

    @Test
    void getNumberFirestationByAddress_whenNoFirestationFoundForAddress_logsWarningAndReturnsNull() {
        List<Firestation> mockedList = Collections.singletonList(new Firestation("Address 2", "2"));
        when(readJsonData.getFirestationList()).thenReturn(mockedList);

        assertNull(getList.getNumberFirestationByAddress("Address"));
    }

    @Test
    void allInfosPerson_returnListWithAllInfos(){

        // GIVEN
        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 2", "City", "Zip", "Phone", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "Phone", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        Map<String, String> entry = new HashMap<>();
        entry.put("firstName", person1.getFirstName());
        entry.put("lastName", person1.getLastName());
        entry.put("address", person1.getAddress());
        entry.put("phone", person1.getPhone());
        entry.put("age", "35");
        List<Map<String, String>> listMapPerson = new ArrayList<>();
        listMapPerson.add(entry);

        // WHEN
        List<AllInfoPerson> result = getList.allInfosPerson(listPersons, listMapPerson);

        // Then
        assertFalse(result.isEmpty(), "The result should not be empty.");
        AllInfoPerson expectedPerson = result.get(0);
        assertEquals("John", expectedPerson.getFirstName(), "First name should match.");
        assertEquals("Doe", expectedPerson.getLastName(), "Last name should match.");
    }

    @Test
    void allInfosPerson_whenInputListsAreEmpty_returnsEmptyListAndLogsWarning() {
        List<Person> emptyPersonsList = Collections.emptyList();
        List<Map<String, String>> emptyAgesList = Collections.emptyList();

        List<AllInfoPerson> result = getList.allInfosPerson(emptyPersonsList, emptyAgesList);

        assertTrue(result.isEmpty());
    }

    @Test
    void allInfosPerson_whenNoMatchBetweenLists_returnsEmptyList() {
        List<Person> personsList = Collections.singletonList(new Person("John", "Doe",
                "Address 1", "Test", "12345", "555-1234", "john.doe@example.com"));
        Map<String, String> ageMap = new HashMap<>();
        ageMap.put("firstName", "Jane");
        ageMap.put("lastName", "Doe");
        ageMap.put("age", "30");
        List<Map<String, String>> agesList = Collections.singletonList(ageMap);

        List<AllInfoPerson> result = getList.allInfosPerson(personsList, agesList);

        assertTrue(result.isEmpty());
    }

    @Test
    void getMedicalRecord_returnMapWithMedicalrecors(){
        // GIVEN
        String firstName = "John";
        String lastName = "Doe";

        Medicalrecord medicalrecord1 = new Medicalrecord("John", "Doe", "03/06/1989",
                Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"),
                Arrays.asList("")
        );
        Medicalrecord medicalrecord2 = new Medicalrecord("Jane", "Doe", "03/06/1992",
                Arrays.asList(),
                Arrays.asList("peanut")
        );
        Medicalrecord medicalrecord3 = new Medicalrecord("Jim", "Test", "03/06/2010",
                Arrays.asList(),
                Arrays.asList("")
        );
        List<Medicalrecord> listMedicalrecords = Arrays.asList(medicalrecord1, medicalrecord2, medicalrecord3);

        // WHEN
        when(readJsonData.getMedicalrecordList()).thenReturn(listMedicalrecords);
        Map<String, String> result = getList.getMedicalRecord(firstName, lastName);

        // THEN
        verify(readJsonData).getMedicalrecordList();
        assertNotNull(result);
        assertEquals("John", result.get("firstName"));
        assertEquals("Doe", result.get("lastName"));
        String expectedMedications = "pharmacol:5000mg, terazine:10mg, noznazol:250mg";
        assertEquals(expectedMedications, result.get("medications"));
        assertEquals("", result.get("allergies"));
    }

    @Test
    void getMedicalRecord_whenNoRecordFound_returnsEmptyMapAndLogsWarning() {
        // Given
        String firstName = "NonExistent";
        String lastName = "Person";
        List<Medicalrecord> medicalRecords = Arrays.asList(
                new Medicalrecord("John", "Doe", "01/01/1990", Collections.emptyList(), Collections.emptyList()),
                new Medicalrecord("Jane", "Doe", "02/02/1990", Collections.emptyList(), Collections.emptyList())
        );
        when(readJsonData.getMedicalrecordList()).thenReturn(medicalRecords);

        // When
        Map<String, String> result = getList.getMedicalRecord(firstName, lastName);

        // Then
        assertTrue(result.isEmpty(), "Expected an empty map when no medical record is found.");
    }

    @Test
    void sortByAddress_returnListInfosPersonSort(){
        // GIVEN
        AllInfoPerson person1 = new AllInfoPerson("1", "Address 1", "Doe", "John",
                "35", "", "", "", "");
        AllInfoPerson person2 = new AllInfoPerson("1", "Address 1", "Doe", "Jane",
                "35", "", "", "", "");
        AllInfoPerson person3 = new AllInfoPerson("1", "Address 2", "Test", "Jimmy",
                "35", "", "", "", "");
        List<AllInfoPerson> personList = Arrays.asList(person1, person2, person3);

        String firestationAddress1 = "Address 1";
        String firestationAddress2 = "Address 2";
        List<String> firestationAddress = Arrays.asList(firestationAddress1, firestationAddress2);

        // WHEN
        List<AllInfoPerson> result = getList.sortByAddress(personList, firestationAddress);

        assertNotNull(result);
        assertEquals(personList.size(), result.size());
        assertEquals("Address 1", result.get(0).getAddress());
        assertEquals("Address 1", result.get(1).getAddress());
        assertEquals("Address 2", result.get(2).getAddress());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("Jane", result.get(1).getFirstName());
        assertEquals("Doe", result.get(1).getLastName());
        assertEquals("Jimmy", result.get(2).getFirstName());
        assertEquals("Test", result.get(2).getLastName());
        assertTrue(firestationAddress.containsAll(result.stream().map(AllInfoPerson::getAddress).collect(Collectors.toList())));
    }

    @Test
    void sortByAddress_whenListInfoPersonsIsEmpty_returnsEmptyListAndLogsWarning() {
        // Given
        List<AllInfoPerson> listInfoPersons = Collections.emptyList();
        List<String> listFirestationAddress = Arrays.asList("Address 1", "Address 2");

        // When
        List<AllInfoPerson> result = getList.sortByAddress(listInfoPersons, listFirestationAddress);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void sortByAddress_whenListFirestationAddressIsEmpty_returnsEmptyListAndLogsWarning() {
        // Given
        AllInfoPerson person1 = new AllInfoPerson("1", "Address 1", "Doe", "John",
                "35", "medications", "allergies", "email@example.com", "111-222-3333");
        List<AllInfoPerson> listInfoPersons = Arrays.asList(person1);
        List<String> listFirestationAddress = Collections.emptyList();

        // When
        List<AllInfoPerson> result = getList.sortByAddress(listInfoPersons, listFirestationAddress);

        // Then
        assertTrue(result.isEmpty(), "Expected an empty list when listFirestationAddress is empty.");
    }

    @Test
    void allFirestationsAddress_returnListAddress(){
        // GIVEN
        List<String> listFirestationNumber = Arrays.asList("1", "2");

        Firestation station1 = new Firestation("Address 1", "1");
        Firestation station2 = new Firestation("Address 2", "2");
        List<Firestation> listFirestation = Arrays.asList(station1, station2);

        // WHEN
        when(readJsonData.getFirestationList()).thenReturn(listFirestation);

        List<String> result = getList.allFirestationsAddress(listFirestationNumber);

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Address 1"));
        assertTrue(result.contains("Address 2"));
    }

    @Test
    void allFirestationsAddress_whenListFirestationsNumbersIsEmpty_returnsEmptyListAndLogsWarning() {
        // Given
        List<String> listFirestationsNumbers = Collections.emptyList();

        // When
        List<String> result = getList.allFirestationsAddress(listFirestationsNumbers);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmailByCity_returnListEmail(){

        // GIVEN
        String city = "City";

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email1");
        Person person2 = new Person("Jane", "Doe", "Address 2", "City", "Zip", "Phone", "Email2");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "Phone", "Email3");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        // WHEN
        when(readJsonData.getPersonList()).thenReturn(listPersons);

        List<String> result = getList.getEmailByCity(city);

        // THEN
        assertEquals(3, result.size());
        assertTrue(result.contains("Email1"));
        assertTrue(result.contains("Email2"));
        assertTrue(result.contains("Email3"));
    }

    @Test
    void getEmailByCity_whenCityIsNull_returnsEmptyListAndLogsWarning() {
        // When
        List<String> result = getList.getEmailByCity(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmailByCity_whenCityIsEmpty_returnsEmptyListAndLogsWarning() {
        // When
        List<String> result = getList.getEmailByCity("");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmailByCity_whenListPersonsIsEmpty_returnsEmptyListAndLogsWarning() {
        // Given
        when(readJsonData.getPersonList()).thenReturn(new ArrayList<>());

        // When
        List<String> result = getList.getEmailByCity("AnyCity");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmailByCity_whenNoEmailsFound_returnsEmptyListAndLogsInfo() {
        // Given
        String city = "NonExistingCity";
        List<Person> persons = Arrays.asList(
                new Person("John", "Doe", "Address 1", "City", "12345",
                        "555-1234", "john@example.com"),
                new Person("Jane", "Doe", "456 Elm St", "City", "67890",
                        "555-5678", "jane@example.com"));
        when(readJsonData.getPersonList()).thenReturn(persons);

        // When
        List<String> result = getList.getEmailByCity(city);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getFirestationNumber_returnListFirestationNumber(){

        // GIVEN
        Firestation firestation1 = new Firestation("Address 1", "1");
        Firestation firestation2 = new Firestation("Address 2", "2");
        Firestation firestation3 = new Firestation("Address 3", "3");
        List<Firestation> listFirestations = Arrays.asList(firestation1, firestation2, firestation3);

        // WHEN
        when(readJsonData.getFirestationList()).thenReturn(listFirestations);

        List<String> result = getList.getFirestationNumber();

        // THEN
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("3"));
    }

    @Test
    void getFirestationNumber_whenListFirestationsIsEmpty_returnsEmptyListAndLogsWarning() {
        // Given
        when(readJsonData.getFirestationList()).thenReturn(new ArrayList<>());

        // When
        List<String> result = getList.getFirestationNumber();

        // Then
        assertTrue(result.isEmpty());
    }

}
