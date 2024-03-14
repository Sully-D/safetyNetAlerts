package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.repository.ImplEncapsulateModelsPrsFstMdrDAOFirestation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

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
     * @return A ResponseEntity with the location of the added firestation or no content if the firestation object is null.
     */
    @PostMapping("/firestation")
    public ResponseEntity<Object> add(@RequestBody Firestation firestation) {
        firestationService.add(firestation);
        // Check if the firestation object is null after attempt to add
        if (Objects.isNull(firestation)) {
            return ResponseEntity.noContent().build();
        }
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
     * @return A ResponseEntity with code 200 for updated firestation or no content if the firestation object is null.
     */
    @PatchMapping("/firestation")
    public ResponseEntity<Object> update(@RequestBody Firestation firestation) {
        firestationService.update(firestation);
        // Check if the firestation object is null after attempt to update
        if (Objects.isNull(firestation)) {
            return ResponseEntity.noContent().build();
        }
        // Code 200 for the updated firestation
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a fire station record from the system.
     * The method expects a fire station object in the request body that should be removed.
     *
     * @param firestation The fire station object to be deleted.
     * @return A ResponseEntity with code 200 for deleted person or no content if the person object is null.
     */
    @DeleteMapping("/firestation")
    public ResponseEntity<Object> delete(@RequestBody Firestation firestation) {
        firestationService.delete(firestation);
        // Check if the firestation object is null after attempt to add
        if (Objects.isNull(firestation)) {
            return ResponseEntity.noContent().build();
        }
        // Code 200 for deleted firestation
        return ResponseEntity.ok().build();
    }
}
