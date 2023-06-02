package br.com.weather.weatherrest.data.weather;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class SearchPatterns {
    private final HashSet<String> patterns;
    private final File file;
    private boolean modified;

    public SearchPatterns(String filePath) {
        this.patterns = new HashSet<>();
        this.file = new File(filePath);
        populateFromFile();
    }

    public boolean contains(String string) {
        return this.patterns.contains(string.toLowerCase());
    }

    public void add(String string) {
        this.patterns.add(string.toLowerCase());
        this.modified = true;
    }

    public void remove(String string) {
        this.patterns.add(string.toLowerCase());
        this.modified = true;
    }

    public void storePatterns() {
        if (!this.modified)
            return;

        try (var reader = new FileWriter(file)) {
            try (var writer = new BufferedWriter(reader)) {
                writer.write(toJSON().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateFromFile() {
        if (!this.file.exists())
            return;

        try (var reader = new FileReader(file)) {
            var array = JsonParser.parseReader(reader).getAsJsonArray();
            for (var pattern : array) {
                this.patterns.add(pattern.getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonArray toJSON() {
        var array = new JsonArray();
        this.patterns.forEach((p) -> {
            array.add(p);
        });
        return array;
    }
}
