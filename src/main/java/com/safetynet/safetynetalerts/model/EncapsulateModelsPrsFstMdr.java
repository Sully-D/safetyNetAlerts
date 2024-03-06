package com.safetynet.safetynetalerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EncapsulateModelsPrsFstMdr {

    @JsonProperty("persons")
    private List<Person> personList;
    @JsonProperty("firestations")
    private List<Firestation> firestationList;
    @JsonProperty("medicalrecords")
    private List<Medicalrecord> medicalrecordList;
}
