package com.safetynet.safetynetalerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Represents a comprehensive model containing detailed information about a person,
 * including their association with a fire station, contact information, and medical details.
 * Utilizes Lombok to generate boilerplate code such as getters, setters, toString,
 * equals, and hashCode methods.
 */
@Data
@AllArgsConstructor
public class AllInfoPerson {

    String firestation;
    String address;
    String lastName;
    String firstName;
    String age;
    String medications;
    String allergies;
    String phone;
    String email;

}
