package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.service.ImplEncapsulateModelsPrsFstMdrDAOFirestation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FirestationController {

    @Autowired
    private ImplEncapsulateModelsPrsFstMdrDAOFirestation firestationService;

    @PostMapping("/firestation")
    public void add(@RequestBody Firestation firestation) {
        firestationService.add(firestation);
    }

    @PatchMapping("/firestation")
    public void update(@RequestBody Firestation firestation) {firestationService.update(firestation);}

    @DeleteMapping("/firestation")
    public void delete(@RequestBody Firestation firestation) {firestationService.delete(firestation);}
}
