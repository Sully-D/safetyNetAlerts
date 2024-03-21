package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.implement.ImplEncapsulateModelsPrsFstMdrDAOFirestation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
     * @return A ResponseEntity with the location of the added firestation
     */
    @PostMapping("/firestation")
    public ResponseEntity<Object> add(@RequestBody Firestation firestation) {
        firestationService.add(firestation);

        // Creating URI for the newly added firestation
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{address}")
                .buildAndExpand(firestation.getAddress())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Updates an existing fire station record in the system.
     * The method expects a fire station object in the request body with updated information.
     *
     * @param firestation The fire station object containing updated data.
     * @return A ResponseEntity with code 200 for updated firestation
     */
    @PatchMapping("/firestation")
    public ResponseEntity<Object> update(@RequestBody Firestation firestation) {
        firestationService.update(firestation);

        // Creating URI for the updated firestation
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{address}")
                .buildAndExpand(firestation.getAddress())
                .toUri();

        return ResponseEntity.ok().location(location).build();
    }

    /**
     * Deletes a fire station record from the system.
     * The method expects a fire station object in the request body that should be removed.
     *
     * @param firestation The fire station object to be deleted.
     * @return A ResponseEntity with code 200 for deleted person
     */
    @DeleteMapping("/firestation")
    public ResponseEntity<Object> delete(@RequestBody Firestation firestation) {
        boolean isDeleted = firestationService.delete(firestation);

        if (!isDeleted) {
            // Assume isDeleted is false if the firestation was not found or could not be deleted
            return ResponseEntity.notFound().build();
        }

        // // Code 200 if deleted
        return ResponseEntity.ok().build();
    }
}
