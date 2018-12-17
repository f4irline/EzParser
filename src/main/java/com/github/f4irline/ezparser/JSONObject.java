package com.github.f4irline.ezparser;

import java.util.LinkedHashMap;

/**
 * Manages the format of every JSON object which is added to the list.
 * 
 * <p>
 * JSONObject holds all the key-value pairs of the object.
 * </p>
 * 
 * @author Tommi Lepola
 * @version 2.0
 * @since 2018.1106
 */
public class JSONObject {
    LinkedHashMap<Object, Object> valuesMap;
    Object key;
    Object value;

    /**
     * Initializes the object by initializing the LinkedHashMap.
     */
    public JSONObject() {
        valuesMap = new LinkedHashMap<Object, Object>();
    }

    /**
     * Puts key and value pair to the LinkedHashMap.
     * 
     * @param key - the key (for example in the pair: "id" : 101, the "id" is the key)
     * @param value - the value (for example in the pair: "id" : 101, 101 is the value)
     */
    public void putKeyValue(Object key, Object value) {
        valuesMap.put(key, value);
    } 

    /**
     * Returns the LinkedHashMap which holds all the key-value pairs of the object.
     * 
     * @return - the LinkedHashMap which holds all the key-value pairs of the object.
     */
    public LinkedHashMap<Object, Object> getValuesMap() {
        return valuesMap;
    }
}