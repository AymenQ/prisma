package com.puzzletimer.database;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.puzzletimer.models.ColorScheme;
import com.puzzletimer.models.ColorScheme.FaceColor;

public class ColorDAO {
    private Connection connection;

    public ColorDAO(Connection connection) {
        this.connection = connection;
    }

    public ColorScheme[] getAll() {
        HashMap<String, ArrayList<FaceColor>> faceColorMap = new HashMap<String, ArrayList<FaceColor>>();

        try {
            Statement statement = this.connection.createStatement();

            ResultSet resultSet = statement.executeQuery(
                "SELECT PUZZLE_ID, FACE_ID, DEFAULT_R, DEFAULT_G, DEFAULT_B, R, G, B FROM COLOR " +
                "ORDER BY \"ORDER\"");

            while (resultSet.next()) {
                String puzzleId = resultSet.getString(1);
                String faceId = resultSet.getString(2);
                int defaultR = resultSet.getInt(3);
                int defaultG = resultSet.getInt(4);
                int defaultB = resultSet.getInt(5);
                int r = resultSet.getInt(6);
                int g = resultSet.getInt(7);
                int b = resultSet.getInt(8);

                if (!faceColorMap.containsKey(puzzleId)) {
                    faceColorMap.put(puzzleId, new ArrayList<FaceColor>());
                }

                faceColorMap.get(puzzleId).add(
                    new FaceColor(
                        puzzleId,
                        faceId,
                        new Color(defaultR, defaultG, defaultB),
                        new Color(r, g, b)));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        ArrayList<ColorScheme> colorSchemes = new ArrayList<ColorScheme>();
        for (String puzzleId : faceColorMap.keySet()) {
            FaceColor[] faceColors = new FaceColor[faceColorMap.get(puzzleId).size()];
            faceColorMap.get(puzzleId).toArray(faceColors);
            colorSchemes.add(new ColorScheme(puzzleId, faceColors));
        }

        ColorScheme[] colorSchemesArray = new ColorScheme[colorSchemes.size()];
        colorSchemes.toArray(colorSchemesArray);

        return colorSchemesArray;
    }

    public void update(ColorScheme colorScheme) {
        try {
            this.connection.setAutoCommit(false);

            PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE COLOR SET R = ?, G = ?, B = ? WHERE PUZZLE_ID = ? AND FACE_ID = ?");

            for (FaceColor faceColor : colorScheme.getFaceColors()) {
                statement.setInt(1, faceColor.getColor().getRed());
                statement.setInt(2, faceColor.getColor().getGreen());
                statement.setInt(3, faceColor.getColor().getBlue());
                statement.setString(4, colorScheme.getPuzzleId());
                statement.setString(5, faceColor.getFaceId());

                statement.addBatch();
            }

            statement.executeBatch();
            statement.close();

            this.connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            try {
                this.connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
    }
}
