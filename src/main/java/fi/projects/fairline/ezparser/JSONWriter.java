/**
 * Package of the json parser.
 */
package fi.projects.fairline.ezparser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Handles all the writing to the .json data file.
 * 
 * @author Tommi Lepola
 * @version 1.0
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
     * items.
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
     * @param lines the ArrayList which is a representation of the .json file.
     * @param startIndex the line where the list in the .json starts.
     * @param lastIndex the line where the list in the .json ends.
     */
    public void writeToJSON(List<String> lines, int startIndex, int lastIndex) {
        int index = 0;
        jsonString = new StringBuilder();
        for (String line : lines) {
            if (line.contains("}") || line.contains("]")) {
                if (!line.contains("{") && !line.contains("[")) {
                    indentAmount -= 4;
                }
            }
            indent();
            if (index > startIndex-1 && index < lines.size()-1 && index != lastIndex && line.contains("}")) {
                jsonString.append(line+",\n");
            } else {
                jsonString.append(line+"\n");
            }
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