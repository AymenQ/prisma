package com.puzzletimer.models;

public class ConfigurationEntry {
    private String key;
    private String value;

    public ConfigurationEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
