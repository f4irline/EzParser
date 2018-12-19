package com.github.f4irline.ezparser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * A simple parsing library for JSON files.
 * 
 * <p>
 * EzParser is a simple parsing library for JSON files. It provides
 * very basic functionality, such as writing an object to a .json.
 * </p>
 * 
 * <p>
 * Currently EzParser is able to write any kind of object to the JSON file using Java
 * Reflection API. The object's values that are to be written into the JSON file must be public
 * for the reflection API to work.
 * </p>
 * 
 * <p>
 * The parser will write the object to JSON with the following format:
 * {
 *   "list": [
 *     {
 *       "id" : 101,
 *       "item" : "ExampleItem",
 *       "amount" : 5
 *     }
 *   ]
 * }
 * </p>
 * 
 * <p>
 * The object can be any kind of custom made object, the parser will iterate through all the public values
 * of the object. The objects MUST have an int identifier value ("id") for removing to be possible.
 * </p>
 * 
 * <p>
 * EzParser has the items from the .json file in an ArrayList, which holds LinkedHashMaps
 * that represents every object given to the parser. The LinkedHashMaps have key-value pairs.
 * </p>
 * 
 * @author Tommi Lepola
 * @version 3.0
 * @since 2018.1106
 */
public class EzParser {

    private File jsonFile;
    private JSONWriter jsonWriter;
    private ArrayList<JSONObject> objects;
    private ArrayList<LinkedHashMap<Object, Object>> objectPairs;

    private JSONStringifier jsonStringifier;

    /**
     * Constructor for EzParser. After creating the EzParser object,
     * it starts initializing the JSON file.
     */
    public EzParser () {
        createJSONFile();
    }

    /**
     * Initializes the JSON file to be used.
     * 
     * <p>
     * The method first checks if a file exists already and if the file is not empty. 
     * If neither of those conditions apply, it creates a new .json file in the root directory
     * of the application. If both of the conditions are true, it just uses the existing
     * file, initializes the writer and stringifier for JSON and gets the existing items 
     * from the JSONStringifier to the ArrayList.
     * </p>
     * 
     */
    private void createJSONFile() {
        if (new File("data.json").isFile() &&
            new File("data.json").length() != 0) {
            System.out.println("File already exists. Using the existing file.");
            initJSONWriter();
            initList();
        } else {
            try {
                jsonFile = new File("data.json");
                jsonFile.createNewFile();

                initJSONWriter();

                jsonWriter.initJSON();

                initList();

                System.out.println("File initialized.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears the JSON completely and initializes everything again.
     */
    public void clearJSON() {
        try {
            jsonFile = new File("data.json");
            jsonFile.createNewFile();

            initJSONWriter();

            jsonWriter.initJSON();

            initJSONStringifier();

            objectPairs = new ArrayList<>();

            System.out.println("File initialized.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the ArrayList which holds the objects and 
     * the JSONStringifier which converts the objects from JSON to
     * Java strings.
     * 
     * <p>
     * The method first initializes the ArrayList and then the JSONStringifier.
     * After initializing the JSONStringifier it gets the all the JSONObjects from
     * the JSONStringifier and iterates through them and adds them to the ArrayList
     * which hold the info from them.
     * </p>
     */
    public void initList() {
        // Init the ArrayList which holds the items.
        objectPairs = new ArrayList<>();
        // Init the JSONStringifier.
        initJSONStringifier();
        // Init the values LinkedHashMap which basically hold
        // the key-value pairs of a SINGLE object.
        LinkedHashMap<Object, Object> values = null;
        // Get all the JSONObjects from the JSONStringifier.
        objects = jsonStringifier.getJSONObjects();

        // Iterate through all the JSONObjects
        for (JSONObject object : objects) {
            values = new LinkedHashMap<Object, Object>();
            // Iterate through the LinkedHashMap which holds the object values which
            // is related to the object which is being iterated currently.
            for (Map.Entry<Object, Object> entry : object.getValuesMap().entrySet()) {
                // Put the values from the object to the LinkedHashMap.
                values.put(entry.getKey(), entry.getValue());
            }
            // Add the LinkedHashMap to the ArrayList.
            objectPairs.add(values);
        }
    }

    /**
     * Initializes the JSONWriter which handles writing to the the JSON file.
     */
    private void initJSONWriter() {
        jsonWriter = new JSONWriter();
    }

    /**
     * Initializes the JSONStringifier which handles turning the JSON into an ArrayList which
     * the application can understand.
     */
    private void initJSONStringifier() {
        jsonStringifier = new JSONStringifier();
    }

    /**
     * Changes value of a single JSON line.
     * 
     * @param key - the key of the value to be changed.
     * @param value - the new value.
     * @param id - identifier of the object which's value is to be changed.
     */
    public void changeValue(Object key, Object value, int id) {
        jsonStringifier.changeObjectValue(key, value, id, jsonWriter);
    }

    /**
     * Writes any kind of object to the .json.
     * 
     * @param obj - the object that will be written.
     */
    public void write(Object obj) {
        jsonStringifier.addObjectToJSON(obj, jsonWriter);
    }

    /**
     * Removes an object from the .json.
     * 
     * @param key - the key of the object which exists in the .json.
     */
    public void remove(int key) {
        jsonStringifier.removeObjectFromJSON(key, jsonWriter);
    }

    /**
     * Returns the ArrayList which holds all the objects and their pairs.
     * 
     * <p>
     * The objects are basically converted to LinkedHashMaps which hold all the
     * key-value pairs.
     * </p>
     * 
     * @return - items the items ArrayList which holds all the object pairs.
     */
    public ArrayList<LinkedHashMap<Object, Object>> getItems() {
        return objectPairs;
    }
}