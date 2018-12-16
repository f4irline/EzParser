/**
 * Package of the json parser.
 */
package fi.projects.fairline.ezparser;

import java.util.List;
import java.util.LinkedHashMap;

/**
 * Manages the format of every JSON object which is added to the list.
 * 
 * <p>
 * JSONObject manages the format of every JSON object so that the strings
 * that are added into the .json match the standards of .json files.
 * </p>
 * 
 * @author Tommi Lepola
 * @version 1.0
 * @since 2018.1106
 */
public class JSONObject {
    String item;
    String amount;
    int keyValue;
    List<String> values;
    LinkedHashMap<Object, Object> valuesMap;
    Object key;
    Object value;

    public JSONObject() {
        valuesMap = new LinkedHashMap<Object, Object>();
    }

    public void putKeyValue(Object key, Object value) {
        valuesMap.put(key, value);
    } 

    public LinkedHashMap<Object, Object> getValuesMap() {
        return valuesMap;
    }

    /**
     * Returns the list which holds the item and amount values.
     * 
     * @return ArrayList which holds the item and amount values.
     */
    public List<String> getList() {
        return values;
    }
}