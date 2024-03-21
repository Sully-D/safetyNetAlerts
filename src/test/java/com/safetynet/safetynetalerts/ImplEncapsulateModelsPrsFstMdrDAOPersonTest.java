package com.safetynet.safetynetalerts;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.implement.ImplEncapsulateModelsPrsFstMdrDAOPerson;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImplEncapsulateModelsPrsFstMdrDAOPersonTest {

    @Mock
    private JsonToObject jsonToObject;
    @InjectMocks
    private ImplEncapsulateModelsPrsFstMdrDAOPerson daoPerson;
    @Mock
    private EncapsulateModelsPrsFstMdr readJsonData;

    private List<Person> personList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        personList = new ArrayList<>();
        lenient().when(readJsonData.getPersonList()).thenReturn(personList);
        when(jsonToObject.readJsonData()).thenReturn(readJsonData);
        daoPerson.init();
    }

    @Test
    void addPerson_Successfully() {
        Person newPerson = new Person("John", "Doe", "Address 1", "Test", "12345",
                "555-1234", "johndoe@example.com");
        readJsonData.setPersonList(new ArrayList<>()); // Make sure the list is initialized

        when(jsonToObject.saveJsonData(readJsonData)).thenReturn(true);

        Person result = daoPerson.add(newPerson);

        assertNotNull(result);
        assertTrue(readJsonData.getPersonList().contains(newPerson));
        verify(jsonToObject, times(1)).saveJsonData(readJsonData);
    }

    @Test
    void addPerson_Failure_ExistingPerson() {
        Person existingPerson = new Person("Jane", "Doe", "Address 1", "Test", "12345",
                "555-6789", "janedoe@example.com");
        readJsonData.setPersonList(List.of(existingPerson)); // Pre-population with an existing person

        Person newPerson = new Person("Jane", "Doe", "Address 2", "Test", "12345",
                "555-6789", "janedoe@example.com");

        Person result = daoPerson.add(newPerson);

        assertNull(result);
        assertEquals(1, readJsonData.getPersonList().size()); // No additions should be made
    }

    @Test
    void addPerson_NullPerson() {
        Person result = daoPerson.add(null);

        assertNull(result);
        assertTrue(readJsonData.getPersonList().isEmpty()); // No additions should be made
        verify(jsonToObject, never()).saveJsonData(readJsonData);
    }

    @Test
    void updatePerson_Successfully() {
        Person originalPerson = new Person("John", "Doe", "123", "Test", "12345",
                "555-1234", "johndoe@example.com");
        readJsonData.getPersonList().add(originalPerson);

        Person updatedPerson = new Person("John", "Doe", "456", "Test", "67890",
                "555-6789", "johndoe@example.com");

        when(jsonToObject.saveJsonData(readJsonData)).thenReturn(true);

        daoPerson.update(updatedPerson);

        assertEquals("456", originalPerson.getAddress());
        assertEquals("67890", originalPerson.getZip());
        verify(jsonToObject, times(1)).saveJsonData(readJsonData);
    }

    @Test
    void updatePerson_NotFound() {
        Person updatePerson = new Person("John", "Doe", "123", "Test", "12345",
                "555-1234", "johndoe@example.com");

        daoPerson.update(updatePerson);

        assertTrue(readJsonData.getPersonList().isEmpty());
        verify(jsonToObject, never()).saveJsonData(readJsonData);
    }

    @Test
    void updatePerson_NullObject() {
        daoPerson.update(null);

        assertTrue(readJsonData.getPersonList().isEmpty());
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void deletePerson_Successfully() {
        Person personToDelete = new Person("John", "Doe", "123", "Test", "12345",
                "555-1234", "johndoe@example.com");
        readJsonData.getPersonList().add(personToDelete);

        when(jsonToObject.saveJsonData(readJsonData)).thenReturn(true);

        boolean result = daoPerson.delete(personToDelete);

        assertTrue(result);
        assertFalse(readJsonData.getPersonList().contains(personToDelete));
        verify(jsonToObject, times(1)).saveJsonData(readJsonData);
    }

    @Test
    void deletePerson_NotFound() {
        Person personToDelete = new Person("Jane", "Doe", "Address 1", "Test", "12345",
                "555-6789", "janedoe@example.com");

        boolean result = daoPerson.delete(personToDelete);

        assertFalse(result);
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void deletePerson_NullObject() {
        boolean result = daoPerson.delete(null);

        assertFalse(result);
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

}
