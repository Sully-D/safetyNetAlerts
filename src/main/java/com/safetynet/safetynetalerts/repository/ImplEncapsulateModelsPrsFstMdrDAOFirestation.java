package com.safetynet.safetynetalerts.repository;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Firestation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ImplEncapsulateModelsPrsFstMdrDAOFirestation implements EncapsulateModelsPrsFstMdrDAO<Firestation> {

    @Override
    public Firestation add(Firestation firestation) {
        JsonToObject jsonToObject = new JsonToObject();

        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();

        List<Firestation> firestationList = readJsonData.getFirestationList();
        firestationList.add(firestation);
        readJsonData.setFirestationList(firestationList);

        jsonToObject.saveJsonData(readJsonData);
        return firestation;
    }

    @Override
    public void update(Firestation firestationUpdate) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Firestation> firestationList = readJsonData.getFirestationList();

        String address = firestationUpdate.getAddress();

        Optional<Firestation> findFirestation = firestationList.stream()
                .filter(p -> p.getAddress().equals(address))
                .findFirst();
        findFirestation.ifPresentOrElse(firestation -> {
            firestation.setStation(firestationUpdate.getStation());

            System.out.println("UPDATE COMPLETE !");

            readJsonData.setFirestationList(firestationList);
            jsonToObject.saveJsonData(readJsonData);
        }, () -> {
            System.out.println("FIRESTATION NOT FOUND");
        });
    }

    @Override
    public void delete(Firestation firestationToDelete) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Firestation> firestationList = readJsonData.getFirestationList();

        firestationList.remove(firestationToDelete);

        readJsonData.setFirestationList(firestationList);
        jsonToObject.saveJsonData(readJsonData);
    }
}
