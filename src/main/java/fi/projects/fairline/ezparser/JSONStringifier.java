/**
 * Package of the json parser.
 */
package fi.projects.fairline.ezparser;

import fi.projects.fairline.ezparser.JSONObject;
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
 * @version 1.0
 * @since 2018.1106
 */
public class JSONStringifier {
    private ArrayList<JSONObject> objects;
    private List<String> lines;
    private String indent = "    ";

    public JSONStringifier() {
        lines = new ArrayList<>();
        objects = new ArrayList<>();
        createJSONList();
    }

    /**
     * Reads the whole .json file into an ArrayList and puts all the
     * already existing items into a LinkedHashMap.
     * 
     * <p>
     * The method readFileToArray reads the whole data.json
     * file into an ArrayList and if items already exist in the .json
     * file, adds them into a LinkedHashMap.
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
                line = line.replaceAll(indent, "");
                line = line.replaceAll("},", "}");
                lines.add(line);
                line = line.replaceAll(",", "");
                line = line.replaceAll(" ", "");
                line = line.replaceAll("\"", "");
                if (nextIsObject && line.contains(":") && !line.contains("list:[")) {
                    int indexOfSeparator = line.indexOf(":");
                    jsonObject.putKeyValue(line.substring(0, indexOfSeparator), line.substring(indexOfSeparator+1));
                } else if (objectEnd) {
                    objects.add(jsonObject);
                    objectEnd = false;
                }
                if (line.contains("{")) {
                    nextIsObject = true;
                    jsonObject = new JSONObject();
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
     * Removes an item from the JSON.
     * 
     * <p>
     * First it changes the key to be a String and compares it in
     * whole ArrayList which holds the .json file data. If a match is found,
     * it defines the index of the item to be removed to be that one. Then 
     * it removes it using the index from the ArrayList.
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
     * @param key the key of the item which will be deleted from the JSON.
     * @param jsonWriter the object which holds all the means to write to the .json file.
     */
    public void removeItemFromJSON(int key, JSONWriter jsonWriter) {
        int removeIndex = 0;
        int index = 0;
        String keyString = Integer.toString(key);

        for (String line : lines) {
            index++;
            if (line.contains(keyString)) {
                removeIndex = index;
            }
        }

        int objectStartIndex = removeIndex - 2;
        int idIndex = removeIndex - 1;
        int itemIndex = removeIndex;
        int amountIndex = removeIndex + 1;
        int objectEndIndex = removeIndex + 2;

        lines.remove(objectEndIndex);
        lines.remove(amountIndex);
        lines.remove(itemIndex);
        lines.remove(idIndex);
        lines.remove(objectStartIndex);

        int[] listBoundaries = checkListBoundaries();

        jsonWriter.writeToJSON(lines, listBoundaries[0], listBoundaries[1]-2);
    }

    public void addObjectToJSON(Object obj, JSONWriter jsonWriter) {
        Class<?> cls = obj.getClass();

        int[] listBoundaries = checkListBoundaries();

        Field[] fields = cls.getDeclaredFields();

        int objectStartIndex = listBoundaries[1] - 1;
        int objectEndIndex = objectStartIndex + fields.length + 1;

        int fieldIndex = 0;

        lines.add(objectStartIndex, "{");

        for(int i = objectStartIndex+1; i < objectEndIndex; i++) {
            String fieldName = fields[fieldIndex].getName();
            Field field = null;
            Object value = null;
            try {
                field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            if (field != null) {
                try {
                    value = field.get(obj);
                    if (value instanceof String) {
                        value = (String) "\"" + value + "\"";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (i != objectEndIndex-1) {
                lines.add(i, ("\""+fieldName+"\"" + " : " + value + ","));
            } else {
                lines.add(i, ("\""+fieldName+"\"" + " : " + value));
            }

            fieldIndex++;
        }

        lines.add(objectEndIndex, "}");

        jsonWriter.writeToJSON(lines, listBoundaries[0], objectEndIndex);
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

    public ArrayList<JSONObject> getJSONList() {
        return objects;
    }
}