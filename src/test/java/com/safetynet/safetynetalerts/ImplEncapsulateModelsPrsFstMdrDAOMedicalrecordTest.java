package com.safetynet.safetynetalerts;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Medicalrecord;
import com.safetynet.safetynetalerts.repository.implement.ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImplEncapsulateModelsPrsFstMdrDAOMedicalrecordTest {

    @Mock
    private JsonToObject jsonToObject;
    @InjectMocks
    private ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord daoMedicalrecord;
    @Mock
    private EncapsulateModelsPrsFstMdr readJsonData;

    private List<Medicalrecord> medicalrecordList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        medicalrecordList = new ArrayList<>();
        lenient().when(readJsonData.getMedicalrecordList()).thenReturn(medicalrecordList);
        when(jsonToObject.readJsonData()).thenReturn(readJsonData);
        daoMedicalrecord.init();
    }

    @Test
    void addMedicalrecord_Successfully() {
        Medicalrecord newMedicalrecord = new Medicalrecord("John", "Doe", "01/01/1990", new ArrayList<>(), new ArrayList<>());

        when(jsonToObject.saveJsonData(any(EncapsulateModelsPrsFstMdr.class))).thenReturn(true);

        Medicalrecord result = daoMedicalrecord.add(newMedicalrecord);

        assertNotNull(result);
        assertTrue(readJsonData.getMedicalrecordList().contains(newMedicalrecord));
        verify(jsonToObject, times(1)).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void addMedicalrecord_Failure_ExistingRecord() {
        Medicalrecord existingRecord = new Medicalrecord("Jane", "Doe", "02/02/1990", new ArrayList<>(), new ArrayList<>());
        readJsonData.getMedicalrecordList().add(existingRecord);

        Medicalrecord newRecord = new Medicalrecord("Jane", "Doe", "03/03/1990", new ArrayList<>(), new ArrayList<>());

        Medicalrecord result = daoMedicalrecord.add(newRecord);

        assertNull(result);
        assertEquals(1, readJsonData.getMedicalrecordList().size());
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void addMedicalrecord_NullObject() {
        Medicalrecord result = daoMedicalrecord.add(null);

        assertNull(result);
        assertTrue(readJsonData.getMedicalrecordList().isEmpty());
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void updateMedicalrecord_Successfully() {
        Medicalrecord existingRecord = new Medicalrecord("John", "Doe", "01/01/1990", new ArrayList<>(), new ArrayList<>());
        medicalrecordList.add(existingRecord);

        Medicalrecord updateRecord = new Medicalrecord("John", "Doe", "01/01/1990", List.of("medication1"), List.of("allergy1"));

        daoMedicalrecord.update(updateRecord);

        assertTrue(medicalrecordList.contains(updateRecord));
        assertEquals(List.of("medication1"), existingRecord.getMedications());
        assertEquals(List.of("allergy1"), existingRecord.getAllergies());
        verify(jsonToObject, times(1)).saveJsonData(readJsonData);
    }


    @Test
    void updateMedicalrecord_NonExisting() {
        Medicalrecord updateRecord = new Medicalrecord("Jane", "Doe", "02/02/1990", List.of("medication2"), List.of("allergy2"));

        daoMedicalrecord.update(updateRecord);

        assertTrue(medicalrecordList.isEmpty());
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void updateMedicalrecord_NullObject() {
        daoMedicalrecord.update(null);

        assertTrue(medicalrecordList.isEmpty());
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void deleteMedicalrecord_Successfully() {
        Medicalrecord medicalrecordToDelete = new Medicalrecord("John", "Doe", "01/01/1990", new ArrayList<>(), new ArrayList<>());
        medicalrecordList.add(medicalrecordToDelete);

        when(jsonToObject.saveJsonData(readJsonData)).thenReturn(true);

        boolean result = daoMedicalrecord.delete(medicalrecordToDelete);

        assertTrue(result);
        assertFalse(medicalrecordList.contains(medicalrecordToDelete));
        verify(jsonToObject, times(1)).saveJsonData(readJsonData);
    }

    @Test
    void deleteMedicalrecord_NotFound() {
        Medicalrecord medicalrecordToDelete = new Medicalrecord("Jane", "Doe", "02/02/1990", List.of("medication2"), List.of("allergy2"));

        boolean result = daoMedicalrecord.delete(medicalrecordToDelete);

        assertFalse(result);
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }

    @Test
    void deleteMedicalrecord_NullObject() {
        boolean result = daoMedicalrecord.delete(null);

        assertFalse(result);
        verify(jsonToObject, never()).saveJsonData(any(EncapsulateModelsPrsFstMdr.class));
    }


}
