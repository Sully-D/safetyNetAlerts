package com.safetynet.safetynetalerts;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.implement.ImplEncapsulateModelsPrsFstMdrDAOFirestation;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImplEncapsulateModelsPrsFstMdrDAOFirestationTest {

    @Mock
    private JsonToObject jsonToObject;
    @InjectMocks
    private ImplEncapsulateModelsPrsFstMdrDAOFirestation daoFirestation;
    @Mock
    private EncapsulateModelsPrsFstMdr readJsonData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jsonToObject.readJsonData()).thenReturn(readJsonData);
        lenient().when(readJsonData.getFirestationList()).thenReturn(new ArrayList<>());
        daoFirestation.init();
    }


    @Test
    void add_FirestationSuccessfully() {
        // Given
        Firestation firestation = new Firestation("Address 1", "1");
        EncapsulateModelsPrsFstMdr readJsonData = new EncapsulateModelsPrsFstMdr(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        readJsonData.getFirestationList().add(firestation);

        when(jsonToObject.saveJsonData(any(EncapsulateModelsPrsFstMdr.class))).thenReturn(true);

        // When
        Firestation result = daoFirestation.add(firestation);

        // Then
        assertNotNull(result);
        assertEquals("1", result.getStation());
        verify(jsonToObject, times(1)).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void add_NullFirestation_ReturnsNull() {
        // When
        Firestation result = daoFirestation.add(null);

        // Then
        assertNull(result);
        assertTrue(readJsonData.getFirestationList().isEmpty());
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void addFirestation_FailureToSaveData() {
        Firestation newFirestation = new Firestation("Address 2", "2");

        when(jsonToObject.saveJsonData(readJsonData)).thenReturn(false);

        Firestation result = daoFirestation.add(newFirestation);

        assertNull(result);
        assertTrue(readJsonData.getFirestationList().contains(newFirestation));
        verify(jsonToObject, times(1)).saveJsonData(readJsonData);
    }


    @Test
    void updateFirestation_Successfully() {
        Firestation existingFirestation = new Firestation("Address 1", "1");
        when(readJsonData.getFirestationList()).thenReturn(Collections.singletonList(existingFirestation));
        Firestation firestationToUpdate = new Firestation("Address 1", "2");

        when(jsonToObject.saveJsonData(any(EncapsulateModelsPrsFstMdr.class))).thenReturn(true);

        daoFirestation.update(firestationToUpdate);

        verify(jsonToObject, times(1)).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
        assertEquals("2", existingFirestation.getStation());
    }

    @Test
    void updateFirestation_WithIncompleteInfo() {
        Firestation firestationToUpdate = new Firestation(null, null);

        daoFirestation.update(firestationToUpdate);

        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void updateFirestation_NonExisting() {
        Firestation firestationToUpdate = new Firestation("Non-Existing Address", "2");

        daoFirestation.update(firestationToUpdate);

        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void deleteFirestation_Successfully() {
        List<Firestation> listFirestation = new ArrayList<>();
        Firestation firestationToDelete = new Firestation("Address 1", "1");
        listFirestation.add(firestationToDelete);

        when(readJsonData.getFirestationList()).thenReturn(listFirestation);

        when(jsonToObject.saveJsonData(readJsonData)).thenReturn(true);

        boolean result = daoFirestation.delete(firestationToDelete);

        assertTrue(result);
        verify(jsonToObject, times(1)).saveJsonData(readJsonData);
        assertTrue(listFirestation.isEmpty());
    }

    @Test
    void deleteFirestation_NotFound() {
        Firestation firestationToDelete = new Firestation("Non Existing Address", "99");

        boolean result = daoFirestation.delete(firestationToDelete);

        assertFalse(result);
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void deleteFirestation_NullObject() {
        boolean result = daoFirestation.delete(null);

        assertFalse(result);
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }


}
