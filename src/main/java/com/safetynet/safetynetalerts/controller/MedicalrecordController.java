package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Medicalrecord;
import com.safetynet.safetynetalerts.repository.implement.ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controller class responsible for handling HTTP requests related to medical record operations.
 * Supports adding, updating, and deleting medical records.
 */
@RestController
public class MedicalrecordController {

    @Autowired
    private ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord medicalrecordService;

    /**
     * Adds a new medical record to the system.
     *
     * @param medicalrecord The medical record to be added, encapsulated within the request body.
     * @return A ResponseEntity with the location of the added medicalrecord
     */
    @PostMapping("/medicalRecord")
    public ResponseEntity<Object> add(@Validated @RequestBody Medicalrecord medicalrecord) {
        medicalrecordService.add(medicalrecord);

        // Creating URI for the newly added medicalrecord using both firstName and lastName
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(medicalrecord.getFirstName(), medicalrecord.getLastName())
                .toUri();
        return ResponseEntity.created(location).build();
    }


    /**
     * Updates an existing medical record in the system.
     * This method expects a complete medical record object in the request body,
     * including the information to be updated.
     *
     * @param medicalrecord The medical record containing updated information.
     * @return A ResponseEntity with code 200 for updated medicalrecord or no content if the medicalrecord object is null.
     */
    @PatchMapping("/medicalRecord")
    public ResponseEntity<Object> update(@RequestBody Medicalrecord medicalrecord) {
        medicalrecordService.update(medicalrecord);

        // Code 200 for the updated medicalrecord
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(medicalrecord.getFirstName(), medicalrecord.getLastName())
                .toUri();
        return ResponseEntity.ok().location(location).build();
    }

    /**
     * Deletes a medical record from the system.
     * The method expects a medical record object in the request body that identifies
     * the record to be removed, typically using a unique identifier contained within the object.
     *
     * @param medicalrecord The medical record to be deleted.
     * @return A ResponseEntity with code 200 for deleted medicalrecord or no content if the medicalrecord object is null.
     */
    @DeleteMapping("/medicalRecord")
    public ResponseEntity<Object> delete(@RequestBody Medicalrecord medicalrecord) {
        boolean isDeleted = medicalrecordService.delete(medicalrecord);

        if (!isDeleted) {
            // Assume isDeleted is false if the medicalrecord was not found or could not be deleted
            return ResponseEntity.notFound().build();
        }

        // Code 200 for the updated medicalrecord
        return ResponseEntity.ok().build();
    }
}
