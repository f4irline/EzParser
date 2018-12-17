package com.github.f4irline.ezparser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Handles all the writing to the .json data file.
 * 
 * @author Tommi Lepola
 * @version 2.0
 * @since 2018.1106
 */
public class JSONWriter {

    private FileWriter fileWriter;
    private int indentAmount = 0;
    private StringBuilder jsonString;

    public JSONWriter() {
        try {
            fileWriter = new FileWriter("data.json", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the JSON file if it is empty or if it does not exist.
     * 
     * <p>
     * initJSON writes a basic structure for the .json file. Currently
     * it only writes a structure so that there is a list which can hold different
     * objects.
     * </p>
     */
    public void initJSON() {
        try {
            fileWriter = new FileWriter("data.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonString = new StringBuilder();
        jsonString.append("{");
        jsonString.append("\n");
        jsonString.append("    \"list\": [");
        jsonString.append("\n");
        jsonString.append("    ]");
        jsonString.append("\n");
        jsonString.append("}");
        
        try {
            fileWriter.write(jsonString.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        flushWriter();
    }

    /**
     * Writes the content of the ArrayList to the .json file.
     * 
     * <p>
     * The writeToFile method first appends all the items from the
     * ArrayList to a StringBuilder while iterating through the ArrayList.
     * </p>
     * 
     * <p>
     * While appending it also checks how much every line should be indented
     * in the .json file and where the object separating commas (",") should be put.
     * </p>
     * 
     * @param lines - the ArrayList which is a representation of the .json file.
     * @param startIndex - the line where the list in the .json starts.
     * @param lastIndex - the line where the list in the .json ends.
     */
    public void writeToJSON(List<String> lines, int startIndex, int lastIndex) {
        int index = 0;
        jsonString = new StringBuilder();
        for (String line : lines) {

            // If the current line contains "}" or "]", we know that we should indent 4 spaces less.
            if (line.contains("}") || line.contains("]")) {
                if (!line.contains("{") && !line.contains("[")) {
                    indentAmount -= 4;
                }
            }
            
            // Append the indentation to the stringbuilder.
            indent();

            // If we're inside the list which holds the objects BUT we're not iterating through the last
            // line of the object, add a "," to the end of it.
            if (index > startIndex-1 && index < lines.size()-1 && index != lastIndex && line.contains("}")) {
                jsonString.append(line+",\n");
            // Else we're at the end of the object, so we won't add a "," to it for proper JSON formatting.
            } else {
                jsonString.append(line+"\n");
            }

            // If the current line contains "{" or "[", we know that we should indent 4 spaces more.
            if (line.contains("{") || line.contains("[")) {
                if (!line.contains("}") && !line.contains("]")) {
                    indentAmount += 4;
                }
            }
            index++;
        }
        try {
            fileWriter = new FileWriter("data.json");
            fileWriter.write(jsonString.toString());
            flushWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends indentation in the writeToJSON method to the StringBuilder.
     */
    private void indent() {
        for (int i = 0; i < indentAmount; i++) {
            jsonString.append(" ");
        }
    }

    /**
     * Flushes the jsonWriter when called.
     */
    private void flushWriter() {
        try {
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the jsonWriter when called.
     */
    public void closeWriter() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}