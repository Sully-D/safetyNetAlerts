package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.AllInfoPerson;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.ImplEncapsulateModelsPrsFstMdrDAOPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class PersonController {

    @Autowired
    private ImplEncapsulateModelsPrsFstMdrDAOPerson personService;

    @Autowired
    private com.safetynet.safetynetalerts.service.URI uriService;

    @PostMapping("/person")
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
         personService.add(person);
        if (Objects.isNull(person)) {
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}")
                .buildAndExpand(person.getFirstName())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/person")
    public void updatePerson(@RequestBody Person person) {
        personService.update(person);
    }

    @DeleteMapping("/person")
    public void deletePerson(@RequestBody Person person) {
        personService.delete(person);
    }


    @GetMapping("/firestation")
    public List<String> getPersonCoverByFirestation(@RequestParam(name = "stationNumber", required = false) String stationNumber) {
        return uriService.getPersonsCoverByFirestation(stationNumber);
    }

    @GetMapping("/childAlert")
    public List<String> getMinorChildAtAddress(@RequestParam(name = "address") String address){
        return uriService.getChildrenAtAddress(address);
    }

    @GetMapping("/phoneAlert")
    public List<String> getPhoneCoverByFirestation(@RequestParam(name = "firestation") String firestation){
        return uriService.getPhonesNumbersByFirestation(firestation);
    }

    @GetMapping("/fire")
    public List<AllInfoPerson> getPersonAndFirestationNumberByAddress(@RequestParam(name = "address") String address){
        return uriService.getPersonsAndFirestationNumberByAddress(address);
    }

    @GetMapping("/flood/stations")
    public List<AllInfoPerson> getHomeCoverByFirestation(@RequestParam(name = "stations") List<String> stations){
        return uriService.getAddressCoverByFirestation(stations);
    }

    @GetMapping("/personInfo")
    public String getPersonInfo(@RequestParam(name = "firstname") String firstname,
                                             @RequestParam(name = "lastname") String lastname){
        return uriService.getPersonsInfo(firstname, lastname);
    }

    @GetMapping("/communityEmail")
    public String getAllEmail(@RequestParam(name = "city") String city) {
        return uriService.getAllEmailsByCity(city);
    }
}
