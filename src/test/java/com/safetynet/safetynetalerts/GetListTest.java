package com.safetynet.safetynetalerts;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import com.safetynet.safetynetalerts.service.GetList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(jsonToObject.readJsonData()).thenReturn(readJsonData);
        getList = new GetList(jsonToObject);
    }

    @Test
    void getAddressFirestationByNumber_returnsCorrectAddresses() {
        // Préparation des données mockées
        Firestation station1 = new Firestation();
        Firestation station2 = new Firestation();
        station1.setStation("1");
        station1.setAddress("Address 1");
        station2.setStation("1");
        station2.setAddress("Address 2");
        List<Firestation> mockedList = Arrays.asList(station1, station2);

        when(readJsonData.getFirestationList()).thenReturn(mockedList);

        // Test
        List<String> addresses = getList.getAddressFirestationByNumber("1");

        // Assertions
        assertEquals(2, addresses.size(), "Le nombre d'adresses retournées devrait être 2");
        assertEquals("Address 1", addresses.get(0), "L'adresse retournée ne correspond pas à ce qui est attendu");
        assertEquals("Address 2", addresses.get(1), "L'adresse retournée ne correspond pas à ce qui est attendu");
    }
}
