package com.puzzletimer.state;

import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.models.ConfigurationEntry;

public class ConfigurationManager {
    private ArrayList<ConfigurationListener> listeners;
    private HashMap<String, ConfigurationEntry> entryMap;

    public ConfigurationManager(ConfigurationEntry[] entries) {
        this.listeners = new ArrayList<ConfigurationListener>();

        this.entryMap = new HashMap<String, ConfigurationEntry>();
        for (ConfigurationEntry entry : entries) {
            this.entryMap.put(entry.getKey(), entry);
        }
    }

    public String getConfiguration(String key) {
        return this.entryMap.get(key).getValue();
    }

    public void setConfiguration(String key, String value) {
        this.entryMap.put(key, new ConfigurationEntry(key, value));
        for (ConfigurationListener listener : this.listeners) {
            listener.configurationEntryUpdated(key, value);
        }
    }

    public void addConfigurationListener(ConfigurationListener listener) {
        this.listeners.add(listener);
    }

    public void removeConfigurationListener(ConfigurationListener listener) {
        this.listeners.remove(listener);
    }
}
