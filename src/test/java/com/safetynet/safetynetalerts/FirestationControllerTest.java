package com.safetynet.safetynetalerts;

import com.safetynet.safetynetalerts.controller.FirestationController;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.implement.ImplEncapsulateModelsPrsFstMdrDAOFirestation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(FirestationController.class)
public class FirestationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImplEncapsulateModelsPrsFstMdrDAOFirestation firestationRepository;

    @Test
    public void addFirestation_ReturnsCreatedStatus() throws Exception {
        // GIVEN
        Firestation newFirestation = new Firestation("New Address", "1");
        String firestationJson = "{\"address\":\"New Address\",\"station\":\"1\"}";

        // WHEN & THEN
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firestationJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(firestationRepository).add(any(Firestation.class));
    }

    @Test
    public void updateFirestation_ReturnsOkStatusWithLocationHeader() throws Exception {
        // GIVEN
        Firestation firestationToUpdate = new Firestation("123 Main St", "1");
        String firestationJson = "{\"address\":\"123 Main St\",\"station\":\"1\"}";
        doNothing().when(firestationRepository).update(any(Firestation.class));

        // WHEN & THEN
        mockMvc.perform(patch("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firestationJson))
                .andExpect(status().isOk())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/firestation/123%20Main%20St"));
    }

    @Test
    public void deleteFirestation_WhenDeleted_ReturnsOkStatus() throws Exception {
        // GIVEN
        doReturn(true).when(firestationRepository).delete(any(Firestation.class));

        // WHEN & THEN
        mockMvc.perform(delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"123 Main St\",\"station\":\"1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteFirestation_WhenNotDeleted_ReturnsNotFoundStatus() throws Exception {
        // GIVEN
        doReturn(false).when(firestationRepository).delete(any(Firestation.class));

        // WHEN & THEN
        mockMvc.perform(delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"Unknown Address\",\"station\":\"99\"}"))
                .andExpect(status().isNotFound());
    }

}
