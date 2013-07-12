package com.puzzletimer.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.puzzletimer.models.ConfigurationEntry;

public class ConfigurationDAO {
    private Connection connection;

    public ConfigurationDAO(Connection connection) {
        this.connection = connection;
    }

    public ConfigurationEntry[] getAll() {
        ArrayList<ConfigurationEntry> entries = new ArrayList<ConfigurationEntry>();

        try {
            Statement statement = this.connection.createStatement();

            ResultSet resultSet = statement.executeQuery(
                "SELECT KEY, VALUE FROM CONFIGURATION");

            while (resultSet.next()) {
                String key = resultSet.getString(1);
                String value = resultSet.getString(2);

                entries.add(new ConfigurationEntry(key, value));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        ConfigurationEntry[] entriesArray = new ConfigurationEntry[entries.size()];
        entries.toArray(entriesArray);

        return entriesArray;
    }

    public void update(ConfigurationEntry entry) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE CONFIGURATION SET VALUE = ? WHERE KEY = ?");

            statement.setString(1, entry.getValue());
            statement.setString(2, entry.getKey());

            if (0 == statement.executeUpdate()) {
                statement.close();
                statement = this.connection.prepareStatement(
                    "INSERT INTO CONFIGURATION VALUES (?,?)");

                statement.setString(2, entry.getValue());
                statement.setString(1, entry.getKey());
                statement.executeUpdate();
            }

            statement.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
