package com.safetynet.safetynetalerts.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class that encapsulates lists of {@link Person}, {@link Firestation}, and {@link Medicalrecord} objects.
 * This class is typically used for loading and saving aggregated data to and from JSON format.
 * Utilizes Lombok to generate boilerplate code such as getters, setters, toString,
 * equals, and hashCode methods.
 */
@NoArgsConstructor
@Data
public class EncapsulateModelsPrsFstMdr {

    private List<Person> personList = new ArrayList<>();
    private List<Firestation> firestationList = new ArrayList<>();
    private List<Medicalrecord> medicalrecordList = new ArrayList<>();

    @JsonCreator
    public EncapsulateModelsPrsFstMdr(@JsonProperty("persons") List<Person> personList,
                                      @JsonProperty("firestations") List<Firestation> firestationList,
                                      @JsonProperty("medicalrecords") List<Medicalrecord> medicalrecordList) {
        this.personList = personList != null ? personList : new ArrayList<>();
        this.firestationList = firestationList != null ? firestationList : new ArrayList<>();
        this.medicalrecordList = medicalrecordList != null ? medicalrecordList : new ArrayList<>();
    }
}
