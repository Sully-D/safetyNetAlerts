package com.safetynet.safetynetalerts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Firestation {

    private String address;
    private String station;

    public Firestation(String address, String station) {
        this.address = address;
        this.station = station;
    }
}
