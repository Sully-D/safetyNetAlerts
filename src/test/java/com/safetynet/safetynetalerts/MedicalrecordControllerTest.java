package com.safetynet.safetynetalerts;


import com.safetynet.safetynetalerts.controller.MedicalrecordController;
import com.safetynet.safetynetalerts.model.Medicalrecord;
import com.safetynet.safetynetalerts.repository.implement.ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MedicalrecordController.class)
public class MedicalrecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord medicalrecordService;

    @Test
    public void addMedicalRecord_ReturnsCreatedStatusAndLocationHeader() throws Exception {
        // Given
        String medicalrecordJson = "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"birthdate\":\"01/01/1990\"," +
                " \"medications\":[], \"allergies\":[]}";
        doNothing().when(medicalrecordService).update(any(Medicalrecord.class));

        // When & Then
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicalrecordJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/medicalRecord/John/Doe")));
    }

    @Test
    public void updateMedicalRecord_ReturnsOkStatusWithLocationHeader() throws Exception {
        // Given
        String medicalrecordJson = "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"birthdate\":\"01/01/1990\", \"medications\":[\"med1\",\"med2\"], \"allergies\":[\"allergy1\"]}";
        doNothing().when(medicalrecordService).update(any(Medicalrecord.class));

        // When & Then
        mockMvc.perform(patch("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicalrecordJson))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", containsString("/medicalRecord/John/Doe")));
    }

    @Test
    public void deleteMedicalRecord_WhenDeleted_ReturnsOkStatus() throws Exception {
        // Given
        doReturn(true).when(medicalrecordService).delete(any(Medicalrecord.class));

        // When & Then
        mockMvc.perform(delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteMedicalRecord_WhenNotDeleted_ReturnsNotFoundStatus() throws Exception {
        // Given
        doReturn(false).when(medicalrecordService).delete(any(Medicalrecord.class));

        // When & Then
        mockMvc.perform(delete("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Nonexistent\", \"lastName\":\"Person\"}"))
                .andExpect(status().isNotFound());
    }

}
