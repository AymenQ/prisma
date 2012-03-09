package com.puzzletimer.models;

public class ConfigurationEntry {
    private final String key;
    private final String value;

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
