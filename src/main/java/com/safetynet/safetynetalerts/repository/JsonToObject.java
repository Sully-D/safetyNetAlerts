package com.safetynet.safetynetalerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynetalerts.model.EncapsulateModelsPrsFstMdr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class for handling JSON file operations including reading from and writing to a JSON file.
 * It supports converting JSON data into Java objects and vice versa.
 */
public class JsonToObject {

    /**
     * Loads the JSON file as an InputStream from the resources directory.
     *
     * @return InputStream of the JSON file.
     */
    private InputStream loadJsonFileAsStream() {
        return getClass().getClassLoader().getResourceAsStream("data.json");
    }

    /**
     * Gets a File object pointing to a writable version of the JSON file located in the resources directory.
     *
     * @return File object pointing to the JSON file.
     */
    private File getWritableJsonFile() {
        return new File(System.getProperty("user.dir") + "/src/main/resources", "data.json");
    }

    /**
     * Reads JSON data from the file and converts it into an EncapsulateModelsPrsFstMdr object.
     *
     * @return EncapsulateModelsPrsFstMdr object populated with data read from the JSON file.
     * @throws RuntimeException if there is an error reading the JSON file or if the file is not found.
     */
    public EncapsulateModelsPrsFstMdr readJsonData() {
        EncapsulateModelsPrsFstMdr dataJson;
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = loadJsonFileAsStream()) {
            if (is == null) {
                throw new IOException("JSON data file not found.");
            }
            dataJson = objectMapper.readValue(is, EncapsulateModelsPrsFstMdr.class);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file", e);
        }
        return dataJson;
    }

    /**
     * Saves the provided EncapsulateModelsPrsFstMdr object data into the JSON file.
     *
     * @param dataJson The EncapsulateModelsPrsFstMdr object to save.
     * @return true if the operation was successful.
     * @throws RuntimeException if there is an error writing to the JSON file.
     */
    public boolean saveJsonData(EncapsulateModelsPrsFstMdr dataJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(getWritableJsonFile(), dataJson);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to JSON file", e);
        }
        return true;
    }

    /**
     * Converts a list of strings into a JSON string.
     *
     * @param listToConvert The list of strings to convert into JSON format.
     * @return A JSON string representation of the list or null if the conversion fails.
     */
    public String writeListToJson(List<String> listToConvert){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(listToConvert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
