package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.repository.JsonToObject;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Service
public class ImplEncapsulateModelsPrsFstMdrDAOPerson implements EncapsulateModelsPrsFstMdrDAO<Person> {

    @Override
    public void add(Person person) {
        JsonToObject jsonToObject = new JsonToObject();

        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();

        List<Person> personList = readJsonData.getPersonList();
        personList.add(person);
        readJsonData.setPersonList(personList);

        jsonToObject.saveJsonData(readJsonData);
    }

    @Override
    public void update(Person personUpdate) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Person> personList = readJsonData.getPersonList();

        String firstName = personUpdate.getFirstName();
        String lastName = personUpdate.getLastName();

        Optional<Person> findPerson = personList.stream()
                .filter(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName))
                .findFirst();
        findPerson.ifPresentOrElse(person -> {
            person.setAddress(personUpdate.getAddress());
            person.setCity(personUpdate.getCity());
            person.setZip(personUpdate.getZip());
            person.setPhone(personUpdate.getPhone());
            person.setEmail(personUpdate.getEmail());

            System.out.println("UPDATE COMPLETE !");

            readJsonData.setPersonList(personList);
            jsonToObject.saveJsonData(readJsonData);
        }, () -> {
            System.out.println("PERSON NOT FOUND");
        });
    }

    @Override
    public void delete(Person personToDelete) {
        JsonToObject jsonToObject = new JsonToObject();
        EncapsulateModelsPrsFstMdr readJsonData = jsonToObject.readJsonData();
        List<Person> personList = readJsonData.getPersonList();

        personList.remove(personToDelete);

        readJsonData.setPersonList(personList);
        jsonToObject.saveJsonData(readJsonData);
    }

}
