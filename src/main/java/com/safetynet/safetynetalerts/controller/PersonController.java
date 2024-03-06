package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.service.ImplEncapsulateModelsPrsFstMdrDAOPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PersonController {

    @Autowired
    private ImplEncapsulateModelsPrsFstMdrDAOPerson personService;

    @PostMapping("/person")
    public void addPerson(@RequestBody Person person) {
        personService.add(person);
    }

    @PatchMapping("/person")
    public void updatePerson(@RequestBody Person person) {personService.update(person);}

    @DeleteMapping("/person")
    public void deletePerson(@RequestBody Person person) {personService.delete(person);}
}
