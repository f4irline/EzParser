/**
 * Package of the json parser.
 */
package fi.projects.fairline.ezparser;

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
 * very basic functionality, such as writing info to a .json.
 * </p>
 * 
 * <p>
 * Currently EzParser only accepts two string variables for it's write-method.
 * It adds the given string variables to a list in the .json file.
 * </p>
 * 
 * <p>
 * EzParser has the items from the .json file in an LinkedHashMap, which holds identifier
 * (key) and values for the item and the amount of the items as string variables.
 * </p>
 * 
 * @author Tommi Lepola
 * @version 2.0
 * @since 2018.1106
 */
public class EzParser {

    private File jsonFile;
    private JSONWriter jsonWriter;
    private ArrayList<JSONObject> items = new ArrayList<>();
    private ArrayList<LinkedHashMap<Object, Object>> allItems;

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
     * from the JSONStringifier to a LinkedHashMap.
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

    public void clearJSON() {
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

    /**
     * Reads the content of the .json file into the LinkedHashMap.
     */
    public void initList() {
        allItems = new ArrayList<>();
        initJSONStringifier();
        LinkedHashMap<Object, Object> values = null;
        items = jsonStringifier.getJSONList();
        for (JSONObject object : items) {
            values = new LinkedHashMap<Object, Object>();
            for (Map.Entry<Object, Object> entry : object.getValuesMap().entrySet()) {
                values.put(entry.getKey(), entry.getValue());
            }
            allItems.add(values);
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
     * Writes any kind of object to the .json.
     * 
     * @param obj the object that will be written.
     */
    public void write(Object obj) {
        jsonStringifier.addObjectToJSON(obj, jsonWriter);
    }

    /**
     * Removes an object from the .json.
     * 
     * @param key the key of the object which exists in the .json.
     */
    public void remove(int key) {
        jsonStringifier.removeItemFromJSON(key, jsonWriter);
    }

    /**
     * Returns the items LinkedHashMap.
     * 
     * @return items the items LinkedHashMap which holds all the list items and their
     * identifiers.
     */
    public ArrayList<LinkedHashMap<Object, Object>> getItems() {
        return allItems;
    }
}