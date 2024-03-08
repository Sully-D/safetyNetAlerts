package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JsonToObject {
    private InputStream loadJsonFileAsStream() {
        // Chargement du fichier depuis les ressources
        return getClass().getClassLoader().getResourceAsStream("data.json");
    }

    private File getWritableJsonFile() {
        // Définir un chemin accessible en écriture pour sauvegarder le fichier
        return new File(System.getProperty("src.main.resources"), "data.json");
    }

    public EncapsulateModelsPrsFstMdr readJsonData() {
        EncapsulateModelsPrsFstMdr dataJson;
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = loadJsonFileAsStream()) {
            if (is == null) {
                throw new IOException("Le fichier de données JSON est introuvable");
            }
            dataJson = objectMapper.readValue(is, EncapsulateModelsPrsFstMdr.class);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier JSON", e);
        }
        return dataJson;
    }

    public boolean saveJsonData(EncapsulateModelsPrsFstMdr dataJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // getWritableJsonFile() pour obtenir un chemin en écriture
            objectMapper.writeValue(getWritableJsonFile(), dataJson);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'écriture du fichier JSON", e);
        }
        return true;
    }

    public String writeListToJson(List<String> listToConvert){
        try {

            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writeValueAsString(listToConvert);

            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
