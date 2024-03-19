package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.ImplEncapsulateModelsPrsFstMdrDAOPerson;
import com.safetynet.safetynetalerts.service.URIs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Controller class handling HTTP requests related to person entities.
 * It provides endpoints for adding, updating, and deleting persons, as well as querying
 * information related to fire stations, children at an address, phone alerts, and more.
 */
@RestController
public class PersonController {

    @Autowired
    private ImplEncapsulateModelsPrsFstMdrDAOPerson personService;

    @Autowired
    private URIs URIsService;

    /**
     * Adds a new person to the system.
     *
     * @param person The person to be added.
     * @return A ResponseEntity with the location of the added person or no content if the person object is null.
     */
    @PostMapping("/person")
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        personService.add(person);
        // Check if the person object is null after attempt to add
        if (Objects.isNull(person)) {
            return ResponseEntity.noContent().build();
        }
        // Creating URI for the newly added person
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}")
                .buildAndExpand(person.getFirstName())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Updates information for an existing person.
     *
     * @param person The person with updated information.
     * @return A ResponseEntity 200 for updated person or no content if the person object is null.
     */
    @PatchMapping("/person")
    public ResponseEntity<Object> updatePerson(@RequestBody Person person) {
        personService.update(person);
        // Check if the person object is null after attempt to update
        if (Objects.isNull(person)) {
            return ResponseEntity.noContent().build();
        }
        // Code 200 update person
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a person from the system.
     *
     * @param person The person to be deleted.
     * @return A ResponseEntity 200 for delete person or no content if the person object is null.
     */
    @DeleteMapping("/person")
    public ResponseEntity<Object> deletePerson(@RequestBody Person person) {
        personService.delete(person);
        // Check if the person object is null after attempt to delete
        if (Objects.isNull(person)) {
            return ResponseEntity.noContent().build();
        }
        // Code 200 for delete person
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a list of persons covered by a specified fire station number.
     *
     * @param stationNumber The fire station number.
     * @return A list of strings with information about the persons covered by the fire station.
     */
    @GetMapping("/firestation")
    public List<String> getPersonCoverByFirestation(@RequestParam(name = "stationNumber", required = false) String stationNumber) {
        return URIsService.getPersonsCoverByFirestation(stationNumber);
    }

    /**
     * Retrieves a list of minors living at a specified address.
     *
     * @param address The address to check for minors.
     * @return A list of strings with information about the minors at the address.
     */
    @GetMapping("/childAlert")
    public List<String> getMinorChildAtAddress(@RequestParam(name = "address") String address){
        return URIsService.getChildrenAtAddress(address);
    }

    /**
     * Retrieves a list of phone numbers for residents covered by a specified fire station.
     *
     * @param firestation The fire station number.
     * @return A list of phone numbers of residents covered by the fire station.
     */
    @GetMapping("/phoneAlert")
    public List<String> getPhoneCoverByFirestation(@RequestParam(name = "firestation") String firestation){
        return URIsService.getPhonesNumbersByFirestation(firestation);
    }

    /**
     * Retrieves detailed information about persons and their associated fire station number living at a specified address.
     *
     * @param address The address to query.
     * @return A list of AllInfoPerson objects with detailed information.
     */
    @GetMapping("/fire")
    public List<String> getPersonAndFirestationNumberByAddress(@RequestParam(name = "address") String address){
        return URIsService.getPersonsAndFirestationNumberByAddress(address);
    }

    /**
     * Retrieves detailed information about homes covered by a list of fire station numbers.
     *
     * @param stations A list of fire station numbers.
     * @return A list of AllInfoPerson objects with detailed information about the homes covered.
     */
    @GetMapping("/flood/stations")
    public List<String> getHomeCoverByFirestation(@RequestParam(name = "stations") List<String> stations){
        return URIsService.getAddressCoverByFirestation(stations);
    }

    /**
     * Retrieves detailed information about a person given their first and last name.
     *
     * @param firstname The first name of the person.
     * @param lastname  The last name of the person.
     * @return A string containing JSON-formatted detailed information about the person.
     */
    @GetMapping("/personInfo")
    public List<String> getPersonInfo(@RequestParam(name = "firstname") String firstname,
                                      @RequestParam(name = "lastname") String lastname){
        return URIsService.getPersonsInfo(firstname, lastname);
    }

    /**
     * Retrieves all email addresses for residents within a specified city.
     *
     * @param city The city to query for resident emails.
     * @return A string containing JSON-formatted list of emails.
     */
    @GetMapping("/communityEmail")
    public List<String> getAllEmail(@RequestParam(name = "city") String city) {
        return URIsService.getAllEmailsByCity(city);
    }
}
