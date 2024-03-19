package com.safetynet.safetynetalerts;

import com.safetynet.safetynetalerts.model.AllInfoPerson;
import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import com.safetynet.safetynetalerts.service.GetList;
import com.safetynet.safetynetalerts.service.URIs;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class URIsTest {

    @Mock
    private JsonToObject jsonToObject;
    @Mock
    private GetList getList;
    @Mock
    private EncapsulateModelsPrsFstMdr readJsonData;

    @InjectMocks
    private URIs uris;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        //when(jsonToObject.readJsonData()).thenReturn(readJsonData);
        getList.init();
    }

    @Test
    void getPersonsCoverByFirestation_returnListPersons(){

        // GIVEN
        String stationNumber = "1";

        List<String> addresses = Arrays.asList("Address 1", "Address 2");

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 2", "City", "Zip", "Phone", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "Phone", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        List<Map<String, String>> listPersonsAges = Arrays.asList(
                new HashMap<String, String>() {{ put("age", "25"); }},
                new HashMap<String, String>() {{ put("age", "30"); }},
                new HashMap<String, String>() {{ put("age", "17"); }}
        );

        // WHEN
        when(getList.getAddressFirestationByNumber(stationNumber)).thenReturn(addresses);
        when(getList.getPersonByAddressStation(addresses)).thenReturn(listPersons);
        when(getList.getAge(listPersons)).thenReturn(listPersonsAges);

        List<String> result = uris.getPersonsCoverByFirestation(stationNumber);


        // THEN
        assertNotNull(result);
        assertTrue(result.contains("[nbAdult=2, nbMinor=1]"));
    }

    @Test
    void getChildrenAtAddress_ReturnsChildrenDetails() {
        // GIVEN
        String address = "Address 1";

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "Phone", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        List<Map<String, String>> listPersonsAges = Arrays.asList(
                Map.of("lastName", "Doe", "firstName", "John", "age", "34"),
                Map.of("lastName", "Smith", "firstName", "Jane", "age", "28"),
                Map.of("lastName", "Test", "firstName", "Jim", "age", "10")
        );

        when(getList.getPersonByAddress(address)).thenReturn(listPersons);
        when(getList.getAge(listPersons)).thenReturn(listPersonsAges);

        // WHEN
        List<String> result = uris.getChildrenAtAddress(address);

        // THEN
        System.out.println(result);
        assertNotNull(result, "The result should not be null.");
        assertTrue(result.get(2).contains("Child"));
        assertTrue(result.get(2).contains("Jim"));
        assertTrue(result.get(2).contains("Test"));
        assertTrue(result.get(2).contains("10"));
    }

    @Test
    void getChildrenAtAddress_NoChildrenReturnsEmptyList() {
        // GIVEN
        String address = "Address 1";

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 1", "City", "Zip", "Phone", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2);

        List<Map<String, String>> listPersonsAges = Arrays.asList(
                Map.of("lastName", "Doe", "firstName", "John", "age", "34"),
                Map.of("lastName", "Smith", "firstName", "Jane", "age", "28")
        );

        when(getList.getPersonByAddress(address)).thenReturn(listPersons);
        when(getList.getAge(listPersons)).thenReturn(listPersonsAges);

        // WHEN
        List<String> result = uris.getChildrenAtAddress(address);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getPhonesNumbersByFirestation_ReturnsUniquePhoneNumbers() {
        // GIVEN
        String firestationNumber = "1";
        List<String> addresses = Arrays.asList("Address 1", "Address 2");

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "555-1234", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 1", "City", "Zip", "555-1234", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "555-5678", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        when(getList.getAddressFirestationByNumber(firestationNumber)).thenReturn(addresses);
        when(getList.getPersonByAddressStation(addresses)).thenReturn(listPersons);

        // WHEN
        List<String> result = uris.getPhonesNumbersByFirestation(firestationNumber);

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("555-1234"));
        assertTrue(result.contains("555-5678"));
    }

    @Test
    void getPhonesNumbersByFirestation_ReturnsEmptyListWhenNoAddresses() {
        // GIVEN
        String firestationNumber = "2";
        when(getList.getAddressFirestationByNumber(firestationNumber)).thenReturn(Arrays.asList());

        // WHEN
        List<String> result = uris.getPhonesNumbersByFirestation(firestationNumber);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getPhonesNumbersByFirestation_ReturnsEmptyListWhenNoPersons() {
        // GIVEN
        String firestationNumber = "3";
        List<String> addresses = Arrays.asList("Address 3");
        when(getList.getAddressFirestationByNumber(firestationNumber)).thenReturn(addresses);
        when(getList.getPersonByAddressStation(addresses)).thenReturn(Arrays.asList());

        // WHEN
        List<String> result = uris.getPhonesNumbersByFirestation(firestationNumber);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonsAndFirestationNumberByAddress_ReturnsFormattedInfo() {
        // GIVEN
        String address = "Address 1";

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "555-1234", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 1", "City", "Zip", "555-1234", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "555-5678", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        List<Map<String, String>> listPersonsAges = Arrays.asList(
                Map.of("firstName", "John", "lastName", "Doe", "age", "30", "medications", "", "allergies", ""),
                Map.of("firstName", "Jane", "lastName", "Doe", "age", "25", "medications", "", "allergies", "")
        );

        List<AllInfoPerson> mockedAllInfoPersons = listPersonsAges.stream().map(map ->
                new AllInfoPerson("1", address, map.get("lastName"), map.get("firstName"), map.get("age"),
                        map.get("medications"), map.get("allergies"), "", "555-1234")
        ).collect(Collectors.toList());

        when(getList.getPersonByAddress(address)).thenReturn(listPersons);
        when(getList.getAge(listPersons)).thenReturn(listPersonsAges);
        when(getList.allInfosPerson(listPersons, listPersonsAges)).thenReturn(mockedAllInfoPersons);

        // WHEN
        List<String> result = uris.getPersonsAndFirestationNumberByAddress(address);

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(info ->
                        info.contains("firestation:1") && info.contains("firstName:John") && info.contains("lastName:Doe")));
        assertTrue(result.stream().anyMatch(info ->
                        info.contains("firestation:1") && info.contains("firstName:Jane") && info.contains("lastName:Doe")));
    }

    @Test
    void getPersonsAndFirestationNumberByAddress_ReturnsEmptyListWhenNoPersons() {
        // GIVEN
        String address = "Unknown Address";
        when(getList.getPersonByAddress(address)).thenReturn(Collections.emptyList());

        // WHEN
        List<String> result = uris.getPersonsAndFirestationNumberByAddress(address);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getAddressCoverByFirestation_ReturnsFormattedInfo() {
        // GIVEN
        List<String> stationsNumbers = Arrays.asList("1", "2");

        List<String> addresses = Arrays.asList("Address 1", "Address 2");

        Person person1 = new Person("John", "Doe", "Address 1", "City", "Zip", "555-1234", "Email");
        Person person2 = new Person("Jane", "Doe", "Address 1", "City", "Zip", "555-1234", "Email");
        Person person3 = new Person("Jim", "Test", "Address 1", "City", "Zip", "555-5678", "Email");
        List<Person> listPersons = Arrays.asList(person1, person2, person3);

        List<Map<String, String>> listPersonsAges = Arrays.asList(
                Map.of("firstName", "John", "lastName", "Doe", "age", "30", "medications", "None", "allergies", "None"),
                Map.of("firstName", "Jane", "lastName", "Doe", "age", "25", "medications", "None", "allergies", "None")
        );

        when(getList.allFirestationsAddress(stationsNumbers)).thenReturn(addresses);
        when(getList.getPersonByAddressStation(addresses)).thenReturn(listPersons);
        when(getList.getAge(listPersons)).thenReturn(listPersonsAges);
        when(getList.allInfosPerson(listPersons, listPersonsAges)).thenReturn(Arrays.asList(
                new AllInfoPerson("1", "Address 1", "Doe", "John", "30", "None", "None", "john.doe@example.com", "555-1234"),
                new AllInfoPerson("2", "Address 2", "Doe", "Jane", "25", "None", "None", "jane.doe@example.com", "555-5678")
        ));
        when(getList.sortByAddress(any(), eq(addresses))).thenReturn(Arrays.asList(
                new AllInfoPerson("1", "Address 1", "Doe", "John", "30", "None", "None", "john.doe@example.com", "555-1234"),
                new AllInfoPerson("2", "Address 2", "Doe", "Jane", "25", "None", "None", "jane.doe@example.com", "555-5678")
        ));

        // WHEN
        List<String> result = uris.getAddressCoverByFirestation(stationsNumbers);

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(info ->
                        info.contains("address:Address 1") && info.contains("firstName:John") && info.contains("lastName:Doe")));
        assertTrue(result.stream().anyMatch(info ->
                        info.contains("address:Address 2") && info.contains("firstName:Jane") && info.contains("lastName:Doe")));
    }

    @Test
    void getAddressCoverByFirestation_ReturnsEmptyListWhenNoAddresses() {
        // GIVEN
        List<String> stationsNumbers = Arrays.asList("3");
        when(getList.allFirestationsAddress(stationsNumbers)).thenReturn(Collections.emptyList());

        // WHEN
        List<String> result = uris.getAddressCoverByFirestation(stationsNumbers);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonsInfo_ReturnsFormattedInfoForSpecificPerson() {
        // GIVEN
        String firstName = "John";
        String lastName = "Doe";

        List<String> listFirestationsNumbers = Arrays.asList("1", "2");

        List<String> listFirestationAddress = Arrays.asList("Address 1", "Address 2");

        List<Person> listPersonsByAddress = Arrays.asList(
                new Person(firstName, lastName, "Address 1", "City", "Zip", "555-1234", "john.doe@example.com")
        );

        List<Map<String, String>> listAgesPersons = Arrays.asList(
                Map.of("firstName", firstName, "lastName", lastName, "age", "30", "medications", "None", "allergies", "None")
        );

        when(getList.getFirestationNumber()).thenReturn(listFirestationsNumbers);
        when(getList.allFirestationsAddress(listFirestationsNumbers)).thenReturn(listFirestationAddress);
        when(getList.getPersonByAddressStation(listFirestationAddress)).thenReturn(listPersonsByAddress);
        when(getList.getAge(listPersonsByAddress)).thenReturn(listAgesPersons);
        when(getList.allInfosPerson(listPersonsByAddress, listAgesPersons)).thenReturn(Arrays.asList(
                new AllInfoPerson("1", "Address 1", lastName, firstName, "30", "None", "None", "john.doe@example.com", "555-1234")
        ));

        // WHEN
        List<String> result = uris.getPersonsInfo(firstName, lastName);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("firstName:John"));
        assertTrue(result.get(0).contains("lastName:Doe"));
        assertTrue(result.get(0).contains("age:30"));
    }

    @Test
    void getPersonsInfo_ReturnsEmptyListWhenPersonNotFound() {
        // GIVEN
        String firstName = "Nonexistent";
        String lastName = "Person";
        when(getList.getFirestationNumber()).thenReturn(Arrays.asList("1", "2"));
        when(getList.allFirestationsAddress(anyList())).thenReturn(Arrays.asList("Address 1", "Address 2"));
        when(getList.getPersonByAddressStation(anyList())).thenReturn(Arrays.asList());
        when(getList.getAge(anyList())).thenReturn(Arrays.asList());

        // WHEN
        List<String> result = uris.getPersonsInfo(firstName, lastName);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllEmailsByCity_ReturnsEmailsForCity() {
        // GIVEN
        String city = "TestCity";

        List<String> listEmails = Arrays.asList("email1@example.com", "email2@example.com");

        when(getList.getEmailByCity(city)).thenReturn(listEmails);

        // WHEN
        List<String> result = uris.getAllEmailsByCity(city);

        // THEN
        assertNotNull(result);
        assertEquals(listEmails.size(), result.size());
        assertTrue(result.containsAll(listEmails));
    }

    @Test
    void getAllEmailsByCity_ReturnsEmptyListWhenNoEmailsFound() {
        // GIVEN
        String city = "EmptyCity";

        when(getList.getEmailByCity(city)).thenReturn(Collections.emptyList());

        // WHEN
        List<String> result = uris.getAllEmailsByCity(city);

        // THEN
        assertTrue(result.isEmpty());
    }
}
