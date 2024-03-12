package com.safetynet.safetynetalerts.model;

import lombok.Data;

import java.util.List;

@Data
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
