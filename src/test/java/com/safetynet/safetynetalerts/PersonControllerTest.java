package com.safetynet.safetynetalerts;

import com.safetynet.safetynetalerts.controller.PersonController;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.implement.ImplEncapsulateModelsPrsFstMdrDAOPerson;
import com.safetynet.safetynetalerts.service.URIs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

     @Autowired
     private MockMvc mockMvc;

    @MockBean
    private ImplEncapsulateModelsPrsFstMdrDAOPerson personRepository;

    @MockBean
    private URIs URIsService;

    @Test
    public void addPerson_ShouldReturnCreatedStatusAndLocationHeader() throws Exception {
        // The JSON representation of the person to be added
        String personJson = "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"age\":30}";

        // Perform POST request and verify the response
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/person/John")));
    }

    @Test
    public void updatePerson_ShouldReturnOkStatusWithLocationHeader() throws Exception {
        // Setup mock service behavior
        doNothing().when(personRepository).update(any(Person.class));

        // JSON representation of the person to be updated
        String personJson = "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"age\":30}";

        // Perform PATCH request and verify the response
        mockMvc.perform(patch("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", containsString("/person/John/Doe")));
    }

    @Test
    public void deletePerson_WhenDeleted_ReturnsOkStatus() throws Exception {
        // Given
        doReturn(true).when(personRepository).delete(any(Person.class));

        // When & Then
        mockMvc.perform(delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void deletePerson_WhenNotDeleted_ReturnsNotFoundStatus() throws Exception {
        // Given
        doReturn(false).when(personRepository).delete(any(Person.class));

        // When & Then
        mockMvc.perform(delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Nonexistent\", \"lastName\":\"Person\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPersonCoverByFirestation_WithValidStationNumber_ReturnsListOfPersons() throws Exception {
        // Given
        List<String> expectedPersons = Arrays.asList("John Doe", "Jane Doe");
        when(URIsService.getPersonsCoverByFirestation(anyString())).thenReturn(expectedPersons);

        // When & Then
        mockMvc.perform(get("/firestation?stationNumber=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"John Doe\",\"Jane Doe\"]"));
    }

    @Test
    public void getPersonCoverByFirestation_WithoutStationNumber_ReturnsBadRequest() throws Exception {
        // Given
        // No specific setup needed for this test scenario since the request param is not provided

        // When & Then
        mockMvc.perform(get("/firestation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getMinorChildAtAddress_ReturnsListOfMinorChildren() throws Exception {
        // Given
        List<String> expectedChildren = Arrays.asList("Child1 Name", "Child2 Name");
        when(URIsService.getChildrenAtAddress(eq("1234 Street Name"))).thenReturn(expectedChildren);

        // When & Then
        mockMvc.perform(get("/childAlert")
                        .param("address", "1234 Street Name")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Child1 Name\",\"Child2 Name\"]"));
    }

    @Test
    public void getPhoneCoverByFirestation_ReturnsListOfPhones() throws Exception {
        // Given
        List<String> expectedPhones = Arrays.asList("123-456-7890", "098-765-4321");
        when(URIsService.getPhonesNumbersByFirestation(eq("1"))).thenReturn(expectedPhones);

        // When & Then
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"123-456-7890\",\"098-765-4321\"]"));
    }

    @Test
    public void getPersonAndFirestationNumberByAddress_ReturnsListOfPersonsAndFirestationNumber() throws Exception {
        // Given
        List<String> expectedData = Arrays.asList("John Doe, Station 1", "Jane Doe, Station 1");
        when(URIsService.getPersonsAndFirestationNumberByAddress(eq("123 Main St"))).thenReturn(expectedData);

        // When & Then
        mockMvc.perform(get("/fire")
                        .param("address", "123 Main St")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"John Doe, Station 1\",\"Jane Doe, Station 1\"]"));
    }

    @Test
    public void getHomeCoverByFirestation_ReturnsListOfCoveredAddresses() throws Exception {
        // Given
        List<String> expectedAddresses = Arrays.asList("123 Main St", "456 Elm St");
        when(URIsService.getAddressCoverByFirestation(any())).thenReturn(expectedAddresses);

        // When & Then
        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1", "2") // Simulate request parameter for multiple stations
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"123 Main St\",\"456 Elm St\"]"));
    }

    @Test
    public void getPersonInfo_ReturnsPersonInfos() throws Exception {
        // Given
        List<String> expectedPersonInfos = Arrays.asList("John Doe, 30, 123 Main St", "Jane Doe, 25, 123 Main St");
        when(URIsService.getPersonsInfo(eq("John"), eq("Doe"))).thenReturn(expectedPersonInfos);

        // When & Then
        mockMvc.perform(get("/personInfo")
                        .param("firstname", "John")
                        .param("lastname", "Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"John Doe, 30, 123 Main St\",\"Jane Doe, 25, 123 Main St\"]"));
    }

    @Test
    public void getAllEmail_ReturnsEmailsOfCityResidents() throws Exception {
        // Given
        List<String> expectedEmails = Arrays.asList("john.doe@example.com", "jane.doe@example.com");
        when(URIsService.getAllEmailsByCity(eq("Anytown"))).thenReturn(expectedEmails);

        // When & Then
        mockMvc.perform(get("/communityEmail")
                        .param("city", "Anytown")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"john.doe@example.com\",\"jane.doe@example.com\"]"));
    }
}
