[![Maven Central](https://img.shields.io/maven-central/v/com.github.f4irline/ezparser.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.f4irline%22%20AND%20a:%22ezparser%22)

# EzParser - A Simple JSON Parser Library

## Introduction

This library was made for a school project. The project consists of a Shopping List application and this library. The library was used on the Shopping List application to save items and read items from a JSON file.

## How to use

1. Either download this library and add it manually as a dependency or use Maven and add the library as a dependency for the Maven project. [Link](https://mvnrepository.com/artifact/com.github.f4irline/ezparser/3.2.1).
2. Create the EzParser object after importing it. E.g. EzParser ezParser = new EzParser();
3. Create a basic Java object.
   - The object can and should only contain basic variables, such as strings, integers, doubles, booleans etc. The EzParser can't write anything like arrays or lists or such.
   - The object has to have an "id" named integer. Every object that is written to the JSON must have that id and it should be a unique id for every object.
   - The object's variables that user want's to save into the JSON file must be public due to Java Reflection API that's used.
4. Write the object to the JSON. E.g. ezParser.write(myObject);
5. Get the items from the parser to a ArrayList of LinkedHashMap type. E.g. ArrayList<LinkedHashMap<Object, Object>> myObjects = ezParser.getItems();
   - All the objects are now in the ArrayList. Every LinkedHashMap represents one JSON object. So you should iterate through the ArrayList and also iterate through every LinkedHashMap, while using the key-value pairs to your liking.

You can check out the Shopping List application which uses the EzParser as an example. [Link](https://github.com/f4irline/ShoppingList)