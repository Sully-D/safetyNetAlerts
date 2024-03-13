package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Medicalrecord;
import com.safetynet.safetynetalerts.repository.ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     */
    @PostMapping("/medicalRecord")
    public void add(@RequestBody Medicalrecord medicalrecord) {
        medicalrecordService.add(medicalrecord);
    }

    /**
     * Updates an existing medical record in the system.
     * This method expects a complete medical record object in the request body,
     * including the information to be updated.
     *
     * @param medicalrecord The medical record containing updated information.
     */
    @PatchMapping("/medicalRecord")
    public void update(@RequestBody Medicalrecord medicalrecord) {
        medicalrecordService.update(medicalrecord);
    }

    /**
     * Deletes a medical record from the system.
     * The method expects a medical record object in the request body that identifies
     * the record to be removed, typically using a unique identifier contained within the object.
     *
     * @param medicalrecord The medical record to be deleted.
     */
    @DeleteMapping("/medicalRecord")
    public void delete(@RequestBody Medicalrecord medicalrecord) {
        medicalrecordService.delete(medicalrecord);
    }
}
