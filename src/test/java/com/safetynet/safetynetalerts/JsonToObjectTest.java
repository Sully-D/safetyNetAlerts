package com.safetynet.safetynetalerts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JsonToObjectTest {

    private JsonToObject jsonToObject;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        jsonToObject = new JsonToObject();
    }

    @Test
    void readJsonData_Success() {
        EncapsulateModelsPrsFstMdr result = jsonToObject.readJsonData();
        assertNotNull(result);
    }

    @Test
    void saveJsonData_Success() {
        EncapsulateModelsPrsFstMdr dataJson = new EncapsulateModelsPrsFstMdr();

        boolean result = jsonToObject.saveJsonData(dataJson);
        assertTrue(result);
    }

}
