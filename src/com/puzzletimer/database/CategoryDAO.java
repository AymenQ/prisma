package com.puzzletimer.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import com.puzzletimer.models.Category;

public class CategoryDAO {
    private Connection connection;

    public CategoryDAO(Connection connection) {
        this.connection = connection;
    }

    public Category[] getAll() throws SQLException {
        Statement statement = this.connection.createStatement();

        ResultSet resultSet = statement.executeQuery(
            "SELECT CATEGORY_ID, SCRAMBLER_ID, DESCRIPTION, USER_DEFINED FROM CATEGORY " +
            "ORDER BY \"ORDER\"");

        ArrayList<Category> categories = new ArrayList<Category>();
        while (resultSet.next()) {
            UUID categoryId = UUID.fromString(resultSet.getString(1));
            String scramblerId = resultSet.getString(2);
            String description = resultSet.getString(3);
            boolean isUserDefined = resultSet.getBoolean(4);

            categories.add(new Category(categoryId, scramblerId, description, isUserDefined));
        }

        Category[] categoriesArray = new Category[categories.size()];
        categories.toArray(categoriesArray);

        return categoriesArray;
    }

    public void insert(Category category) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(
            "INSERT INTO CATEGORY VALUES (?, ?, ?, ?, ?)");

        statement.setInt(1, 0);
        statement.setString(2, category.getCategoryId().toString());
        statement.setString(3, category.scramblerId.toString());
        statement.setString(4, category.description);
        statement.setBoolean(5, category.isUserDefined());

        statement.execute();

        statement.close();
    }

    public void update(Category category) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(
            "UPDATE CATEGORY SET SCRAMBLER_ID = ?, DESCRIPTION = ?, USER_DEFINED = ? WHERE CATEGORY_ID = ?");

        statement.setString(1, category.scramblerId.toString());
        statement.setString(2, category.description);
        statement.setBoolean(3, category.isUserDefined());
        statement.setString(4, category.getCategoryId().toString());

        statement.execute();

        statement.close();
    }

    public void delete(Category category) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(
            "DELETE FROM CATEGORY WHERE CATEGORY_ID = ?");

        statement.setString(1, category.getCategoryId().toString());

        statement.execute();

        statement.close();
    }
}
