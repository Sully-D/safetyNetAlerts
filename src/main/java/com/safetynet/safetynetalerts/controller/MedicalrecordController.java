package com.safetynet.safetynetalerts.controller;

import com.safetynet.safetynetalerts.model.Medicalrecord;
import com.safetynet.safetynetalerts.repository.ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MedicalrecordController {

    @Autowired
    private ImplEncapsulateModelsPrsFstMdrDAOMedicalrecord medicalrecordService;

    @PostMapping("/medicalRecord")
    public void add(@RequestBody Medicalrecord medicalrecord) {
        medicalrecordService.add(medicalrecord);
    }

    @PatchMapping("/medicalRecord")
    public void update(@RequestBody Medicalrecord medicalrecord) {medicalrecordService.update(medicalrecord);}

    @DeleteMapping("/medicalRecord")
    public void delete(@RequestBody Medicalrecord medicalrecord) {medicalrecordService.delete(medicalrecord);}
}
