package com.safetynet.safetynetalerts;

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
}
