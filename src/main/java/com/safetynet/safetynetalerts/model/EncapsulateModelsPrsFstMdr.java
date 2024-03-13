package com.safetynet.safetynetalerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Model class that encapsulates lists of {@link Person}, {@link Firestation}, and {@link Medicalrecord} objects.
 * This class is typically used for loading and saving aggregated data to and from JSON format.
 * Utilizes Lombok to generate boilerplate code such as getters, setters, toString,
 * equals, and hashCode methods.
 */
@Data
public class EncapsulateModelsPrsFstMdr {

    /**
     * List of {@link Person} objects representing individuals.
     * Annotated with @JsonProperty to map the "persons" field in JSON.
     */
    @JsonProperty("persons")
    private List<Person> personList;

    /**
     * List of {@link Firestation} objects representing fire station assignments.
     * Annotated with @JsonProperty to map the "firestations" field in JSON.
     */
    @JsonProperty("firestations")
    private List<Firestation> firestationList;

    /**
     * List of {@link Medicalrecord} objects representing individuals' medical records.
     * Annotated with @JsonProperty to map the "medicalrecords" field in JSON.
     */
    @JsonProperty("medicalrecords")
    private List<Medicalrecord> medicalrecordList;
}
