package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.ImplEncapsulateModelsPrsFstMdrDAOFirestation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class responsible for handling HTTP requests related to fire station operations,
 * such as adding, updating, and deleting fire station entries.
 */
@RestController
public class FirestationController {

    @Autowired
    private ImplEncapsulateModelsPrsFstMdrDAOFirestation firestationService;

    /**
     * Adds a new fire station record to the system.
     *
     * @param firestation The fire station object to be added, received as a request body.
     */
    @PostMapping("/firestation")
    public void add(@RequestBody Firestation firestation) {
        firestationService.add(firestation);
    }

    /**
     * Updates an existing fire station record in the system.
     * The method expects a fire station object in the request body with updated information.
     *
     * @param firestation The fire station object containing updated data.
     */
    @PatchMapping("/firestation")
    public void update(@RequestBody Firestation firestation) {
        firestationService.update(firestation);
    }

    /**
     * Deletes a fire station record from the system.
     * The method expects a fire station object in the request body that should be removed.
     *
     * @param firestation The fire station object to be deleted.
     */
    @DeleteMapping("/firestation")
    public void delete(@RequestBody Firestation firestation) {
        firestationService.delete(firestation);
    }
}
