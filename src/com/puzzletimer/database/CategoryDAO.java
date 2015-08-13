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

    public Category[] getAll() {
        ArrayList<Category> categories = new ArrayList<Category>();

        try {
            // category
            Statement categoryStatement = this.connection.createStatement();

            ResultSet categoryResultSet = categoryStatement.executeQuery(
                "SELECT CATEGORY_ID, SCRAMBLER_ID, DESCRIPTION, USER_DEFINED FROM CATEGORY " +
                "ORDER BY \"ORDER\"");

            while (categoryResultSet.next()) {
                UUID categoryId = UUID.fromString(categoryResultSet.getString(1));
                String scramblerId = categoryResultSet.getString(2);
                String description = categoryResultSet.getString(3);
                boolean isUserDefined = categoryResultSet.getBoolean(4);

                // tips
                PreparedStatement tipsStatement = this.connection.prepareStatement(
                    "SELECT TIP_ID FROM CATEGORY_TIPS " +
                    "WHERE CATEGORY_ID = ? " +
                    "ORDER BY \"ORDER\"");

                tipsStatement.setString(1, categoryId.toString());

                ResultSet tipsResultSet = tipsStatement.executeQuery();

                ArrayList<String> tipIds = new ArrayList<String>();
                while (tipsResultSet.next()) {
                    tipIds.add(tipsResultSet.getString(1));
                }

                String[] tipIdsArray = new String[tipIds.size()];
                tipIds.toArray(tipIdsArray);

                categories.add(
                    new Category(
                        categoryId,
                        scramblerId,
                        description,
                        isUserDefined,
                        tipIdsArray));
            }

            categoryStatement.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        Category[] categoriesArray = new Category[categories.size()];
        categories.toArray(categoriesArray);

        return categoriesArray;
    }

    public void insert(Category category) {
        try {
            this.connection.setAutoCommit(false);

            // category
            PreparedStatement categoryStatement = this.connection.prepareStatement(
                "INSERT INTO CATEGORY VALUES (?, ?, ?, ?, ?)");

            categoryStatement.setInt(1, 0);
            categoryStatement.setString(2, category.getCategoryId().toString());
            categoryStatement.setString(3, category.getScramblerId());
            categoryStatement.setString(4, category.getDescription());
            categoryStatement.setBoolean(5, category.isUserDefined());

            categoryStatement.executeUpdate();

            categoryStatement.close();

            // tips
            PreparedStatement tipsStatement = this.connection.prepareStatement(
                "INSERT INTO CATEGORY_TIPS VALUES (?, ?, ?)");

            for (int i = 0; i < category.getTipIds().length; i++) {
                tipsStatement.setInt(1, i);
                tipsStatement.setString(2, category.getCategoryId().toString());
                tipsStatement.setString(3, category.getTipIds()[i]);

                tipsStatement.addBatch();
            }

            tipsStatement.executeBatch();

            tipsStatement.close();

            this.connection.commit();
        } catch (SQLException e) {
            try {
                this.connection.rollback();
            } catch (SQLException e1) {
                throw new DatabaseException(e1);
            }

            throw new DatabaseException(e);
        } finally {
            try {
                this.connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
    }

    public void update(Category category) {
        try {
            this.connection.setAutoCommit(false);

            // category
            PreparedStatement categoryStatement = this.connection.prepareStatement(
                "UPDATE CATEGORY SET SCRAMBLER_ID = ?, DESCRIPTION = ?, USER_DEFINED = ? " +
                "WHERE CATEGORY_ID = ?");

            categoryStatement.setString(1, category.getScramblerId());
            categoryStatement.setString(2, category.getDescription());
            categoryStatement.setBoolean(3, category.isUserDefined());
            categoryStatement.setString(4, category.getCategoryId().toString());

            categoryStatement.executeUpdate();

            categoryStatement.close();

            // delete old tips
            PreparedStatement deleteTipsStatement = this.connection.prepareStatement(
                "DELETE FROM CATEGORY_TIPS " +
                "WHERE CATEGORY_ID = ?");

            deleteTipsStatement.setString(1, category.getCategoryId().toString());

            deleteTipsStatement.executeUpdate();

            deleteTipsStatement.close();

            // tips
            PreparedStatement tipsStatement = this.connection.prepareStatement(
                "INSERT INTO CATEGORY_TIPS VALUES (?, ?, ?)");

            for (int i = 0; i < category.getTipIds().length; i++) {
                tipsStatement.setInt(1, i);
                tipsStatement.setString(2, category.getCategoryId().toString());
                tipsStatement.setString(3, category.getTipIds()[i]);

                tipsStatement.addBatch();
            }

            tipsStatement.executeBatch();

            tipsStatement.close();

            this.connection.commit();
        } catch (SQLException e) {
            try {
                this.connection.rollback();
            } catch (SQLException e1) {
                throw new DatabaseException(e1);
            }

            throw new DatabaseException(e);
        } finally {
            try {
                this.connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
    }

    public void delete(Category category) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM CATEGORY " +
                "WHERE CATEGORY_ID = ?");

            statement.setString(1, category.getCategoryId().toString());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
