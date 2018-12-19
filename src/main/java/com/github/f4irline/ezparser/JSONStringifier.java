package com.github.f4irline.ezparser;

import com.github.f4irline.ezparser.JSONObject;
import java.util.List;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Handles and manages the ArrayList which is a representation of the
 * .json data file.
 * 
 * @author Tommi Lepola
 * @version 2.0
 * @since 2018.1106
 */
public class JSONStringifier {
    private ArrayList<JSONObject> objects;
    private List<String> lines;
    private String indent = "    ";

    /**
     * Initializes the stringifier by first initializing the 
     * needed ArrayLists and then initializing the JSON list.
     * 
     * <p>
     * The ArrayList "lines" holds all the lines of the JSON file.
     * </p>
     * 
     * <p>
     * The ArrayList "objects" holds all the JSONObjects.
     * </p>
     */
    public JSONStringifier() {
        lines = new ArrayList<>();
        objects = new ArrayList<>();
        createJSONList();
    }

    /**
     * Reads the whole .json file into an ArrayList and adds the objects
     * to another ArrayList.
     * 
     * <p>
     * The method readFileToArray reads the whole data.json
     * file into an ArrayList and adds the object keys and values to the
     * objects ArrayList.
     * </p>
     * 
     * <p>
     * The idea behind it was that it could be easier to 
     * read the .json file by having it in an ArrayList first and 
     * getting all the data from that ArrayList by iterating through
     * and checking where all the needed data is.
     * </p>
    */
    private void createJSONList() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("data.json"))) {
            String line;
            boolean nextIsObject = false;
            boolean objectEnd = false;
            JSONObject jsonObject = new JSONObject();
            while ((line = fileReader.readLine()) != null) {
                // Replace all unnecessary clutter from the lines first
                // to help using substrings etc.
                line = line.replaceAll(indent, "");
                line = line.replaceAll("},", "}");
                lines.add(line);
                line = line.replaceAll(",", "");
                line = line.replaceAll(" ", "");
                line = line.replaceAll("\"", "");
                
                // If we know that the current line holds info from the object,
                // put the key and value from the line to the JSONObject.
                if (nextIsObject && line.contains(":") && !line.contains("list:[")) {
                    int indexOfSeparator = line.indexOf(":");
                    jsonObject.putKeyValue(line.substring(0, indexOfSeparator), line.substring(indexOfSeparator+1));
                // If we know that we've just added all the key-value pairs from an object,
                // add the object to the ArrayList which holds all the JSONObjects.
                } else if (objectEnd) {
                    objects.add(jsonObject);
                    objectEnd = false;
                }
                // If line contains "{", the next line is going to hold info of the JSONObject.
                // Therefore we initialize a new JSONObject.
                if (line.contains("{")) {
                    nextIsObject = true;
                    jsonObject = new JSONObject();
                // If line contains "}", we're not inside a singular JSONObject anymore.
                } else if (line.contains("}")) {
                    nextIsObject = false;
                    objectEnd = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes an object from the JSON.
     * 
     * <p>
     * First it changes the key to be a String and compares it in
     * whole ArrayList which holds the .json file data. If a match is found,
     * it uses checkObjectBoundaries(index) method to check where the object 
     * (which holds that index) starts and ends.
     * </p>
     * 
     * <p>
     * After getting the boundaries fo the object, it loops through the lines ArrayList
     * again from the object's end to object's start, removing the lines between.
     * </p>
     * 
     * <p>
     * After removing it from the list, it asks for the boundaries of the whole
     * list in the .json for jsonWriter to be able to rewrite the list.
     * </p>
     * 
     * <p>
     * After finding the start and end of the list in the ArrayList,
     * it calls the writeToJSON method from the jsonWriter to write the 
     * ArrayList in to the .json file itself.
     * </p>
     * 
     * @param id - the key of the item which will be deleted from the JSON.
     * @param jsonWriter - the object which holds all the means to write to the .json file.
     */
    public void removeObjectFromJSON(int id, JSONWriter jsonWriter) {
        int index = 0;
        String idString = Integer.toString(id);
        int[] objectBounds = null;

        // Iterate through all the lines and check
        // in which index is the object to be removed.
        for (String line : lines) {
            index++;
            if (line.contains(idString)) {
                // identifier found, ask for the object's boundaries.
                objectBounds = checkObjectBoundaries(index);
                break;
            }
        }

        int objectStartIndex = objectBounds[0];
        int objectEndIndex = objectBounds[1];

        // Iterate the lines starting from the end of the object to the start of the object.
        // Remove all the lines between there.
        for (int i = objectEndIndex; i >= objectStartIndex; i--) {
            lines.remove(i);
        }

        int[] listBoundaries = checkListBoundaries();

        jsonWriter.writeToJSON(lines, listBoundaries[0], listBoundaries[1]-2);
    }

    /**
     * Checks the to-be-removed object's boundaries.
     * 
     * @param index - the to-be-removed object's identifier's index in the lines ArrayList.
     * @return - the boundaries of the object in an array.
     */
    private int[] checkObjectBoundaries(int index) {
        int objectStartIndex = 0;
        int objectEndIndex = 0;
        int[] boundArray = new int [2];
        for (int i = index; i > 0; i--) {
            if (lines.get(i).equals("{")) {
                objectStartIndex = i;
                break;
            }
        }

        for (int i = index; i < lines.size(); i++) {
            if (lines.get(i).equals("}")) {
                objectEndIndex = i;
                break;
            }
        }

        boundArray[0] = objectStartIndex;
        boundArray[1] = objectEndIndex;

        return boundArray;
    }

    /**
     * Adds any kind of object to the JSON using Java Reflection API.
     * 
     * @param obj - object to be added.
     * @param jsonWriter - the filewriter which handles writing to the JSON file.
     */
    public void addObjectToJSON(Object obj, JSONWriter jsonWriter) {
        Class<?> cls = obj.getClass();

        int[] listBoundaries = checkListBoundaries();

        // Get all public fields from the object to an array.
        Field[] fields = cls.getDeclaredFields();

        // The object starts from the end of the list (where the line is "]").
        int objectStartIndex = listBoundaries[1] - 1;

        // The object ends at the object start index + the number of public fields + 1.
        int objectEndIndex = objectStartIndex + fields.length + 1;

        // Used to iterate through all the fields of the object.
        int fieldIndex = 0;

        // Add the object start, "{" to the object start index.
        lines.add(objectStartIndex, "{");

        // Iterate the lines starting from where the new object starts from until the end of the new object.
        for(int i = objectStartIndex+1; i < objectEndIndex; i++) {
            // Get the name of the field that's being currently iterated.
            String fieldName = fields[fieldIndex].getName();
            Field field = null;
            Object value = null;
            try {
                // Get the field into a Field object and make it accessible.
                field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            // Now that we have the name of the field object, get the value from the field object as well.
            if (field != null) {
                try {
                    value = field.get(obj);
                    // If the value is a string, add quote marks to it for proper JSON formatting.
                    if (value instanceof String) {
                        value = (String) "\"" + value + "\"";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // If were not at the end of the object fields, add a "," to the ending of it for proper JSON formatting.
            if (i != objectEndIndex-1) {
                lines.add(i, ("\""+fieldName+"\"" + " : " + value + ","));
            } else {
                lines.add(i, ("\""+fieldName+"\"" + " : " + value));
            }

            fieldIndex++;
        }

        // When we're done iterating through the lines and the object to be added, add "}" to end the object.
        lines.add(objectEndIndex, "}");

        // Call JSONWriter to write the content of the "lines" ArrayList to the JSON.
        jsonWriter.writeToJSON(lines, listBoundaries[0], objectEndIndex);
    }

    /**
     * Handles changing the object's value.
     * 
     * <p>
     * The method first finds the object by searching for it's integer identifier.
     * After finding the object, it searches for the index of the key which's value
     * is going to be changed from the "lines" ArrayList.
     * </p>
     * 
     * <p>
     * After finding the index of the key (and the value therefore), it removes the
     * line altogether and applies the new value to it.
     * </p>
     * 
     * @param key - the key of the value to be changed.
     * @param value - the new value.
     * @param id - the integer identifier of the object which's value is to be changed.
     * @param jsonWriter - FileWriter which handles the writing to the JSON file.
     * @return - true if succesful (if the key was found from the object), false if the key was not found from the object.
     */
    public boolean changeObjectValue(Object key, Object value, int id, JSONWriter jsonWriter) {
        int index = 0;
        int objectIdentifierIndex = 0;
        int keyIndex = -1;
        String idString = Integer.toString(id);

        // Iterate through all the lines and check
        // in which index is the object to be removed.
        for (String line : lines) {
            index++;
            if (line.contains(idString)) {
                // identifier found, ask for the object's boundaries.
                objectIdentifierIndex = index;
                break;
            }
        }

        // Find the index of the key-value pair in the ArrayList.
        keyIndex = findObjectKey(key, objectIdentifierIndex);

        // keyIndex is initially -1, so if it hasn't been changed from the findObjectKey() method,
        // we know that the key was not found and we return false. If it was found, it will be 
        // of value 0 or more so we can use that to write the new value in.
        if (keyIndex > -1) {
            writeNewValue(key, value, keyIndex, jsonWriter);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finds the index of the key-value pair which is going to be changed.
     * 
     * @param key - the key which's value will be changed.
     * @param objectIdentifierIndex - the identifier index of the object which's value will be changed.
     * @return - the index of the key-value pair if found, -1 if not found.
     */
    private int findObjectKey(Object key, int objectIdentifierIndex) {
        int[] objectBoundaries = checkObjectBoundaries(objectIdentifierIndex);

        int objectStartIndex = objectBoundaries[0];
        int objectEndIndex = objectBoundaries[1];

        for (int i = objectIdentifierIndex; i > objectStartIndex; i--) {
            if (lines.get(i).contains(key.toString())) {
                return i;
            }
        }
        
        for (int i = objectIdentifierIndex; i < objectEndIndex; i++) {
            if (lines.get(i).contains(key.toString())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Writes new value of the key-value pair to the JSON.
     * 
     * @param key - the key of the value which will be changed.
     * @param value - the new value, which will be changed.
     * @param keyIndex - the index of the key-value pair, which will be changed.
     * @param jsonWriter - the FileWriter which handles writing to the JSON.
     */
    private void writeNewValue(Object key, Object value, int keyIndex, JSONWriter jsonWriter) {
        String newValue = "";
        // If the value is a string variable, add quotations to it for proper JSON formatting.
        if (value instanceof String) {
            newValue = "\""+value.toString()+"\"";
        } else {
            newValue = value.toString();
        }
        // Creating the new line.
        String line = "\""+key.toString()+"\""+" : "+newValue;

        // Remove the old line and add the new line to replace it.
        lines.remove(keyIndex);
        lines.add(keyIndex, line);

        // Check the list's boundaries and rewrite it to JSON.
        int[] listBoundaries = checkListBoundaries();
        jsonWriter.writeToJSON(lines, listBoundaries[0], listBoundaries[1]-2);
    }

    /**
     * Checks the boundaries of the list in the .json file.
     * 
     * <p>
     * Iterates through the lines which are a representation of the
     * .json file and finds the starting and ending of the list in the
     * file.
     * </p>
     * 
     * @return listBoundaries an Array which holds the ending and beginning
     * indexes of the .json file. The beginning is in the index[0] of the array
     * and ending is in the index[1] of the array.
     */
    private int[] checkListBoundaries() {
        int[] listBoundaries = new int[2];

        int index = 0;


        for (String line : lines) {
            index++;
            if (line.contains("]")) {
                listBoundaries[1] = index;
                break;
            } else if (line.contains("[")) {
                listBoundaries[0] = index;
            }
        }

        return listBoundaries;
    }

    /**
     * Returns the JSONObjects in an ArrayList.
     * 
     * @return - the JSONObjects in an ArrayList.
     */
    public ArrayList<JSONObject> getJSONObjects() {
        return objects;
    }
}