package com.puzzletimer.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.puzzletimer.models.Category;
import com.puzzletimer.models.Scramble;
import com.puzzletimer.models.Solution;
import com.puzzletimer.models.Timing;

public class SolutionDAO {
    private Connection connection;

    public SolutionDAO(Connection connection) {
        this.connection = connection;
    }

    public Solution[] getAll(Category category) {
        ArrayList<Solution> solutions = new ArrayList<Solution>();

        try {
            PreparedStatement statement = this.connection.prepareStatement(
                "SELECT SOLUTION_ID, CATEGORY_ID, SCRAMBLER_ID, SEQUENCE, START, END, PENALTY " +
                "FROM SOLUTION " +
                "WHERE CATEGORY_ID = ? " +
                "ORDER BY START DESC");

            statement.setString(1, category.getCategoryId().toString());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UUID solutionId = UUID.fromString(resultSet.getString(1));
                UUID categoryId = UUID.fromString(resultSet.getString(2));
                String scramblerId = resultSet.getString(3);
                String sequence = resultSet.getString(4);
                Date start = resultSet.getTimestamp(5);
                Date end = resultSet.getTimestamp(6);
                String penalty = resultSet.getString(7);

                // TODO: should use parser
                Scramble scramble = new Scramble(scramblerId, sequence.split("\\s+"));
                Solution solution = new Solution(solutionId, categoryId, scramble, new Timing(start, end), penalty);

                solutions.add(solution);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        Solution[] solutionArray = new Solution[solutions.size()];
        solutions.toArray(solutionArray);

        return solutionArray;
    }

    public void insert(Solution solution) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO SOLUTION VALUES (?, ?, ?, ?, ?, ?, ?)");

            StringBuilder sequence = new StringBuilder();
            for (String move : solution.getScramble().getSequence()) {
                sequence.append(move + " ");
            }

            statement.setString(1, solution.getSolutionId().toString());
            statement.setString(2, solution.getCategoryId().toString());
            statement.setString(3, solution.getScramble().getScramblerId());
            statement.setString(4, sequence.toString().trim());
            statement.setTimestamp(5, new Timestamp(solution.getTiming().getStart().getTime()));
            statement.setTimestamp(6, new Timestamp(solution.getTiming().getEnd().getTime()));
            statement.setString(7, solution.getPenalty());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void update(Solution solution) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE SOLUTION SET END = ?, PENALTY = ? WHERE SOLUTION_ID = ?");

            statement.setTimestamp(1, new Timestamp(solution.getTiming().getEnd().getTime()));
            statement.setString(2, solution.getPenalty());
            statement.setString(3, solution.getSolutionId().toString());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void delete(Solution solution) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM SOLUTION WHERE SOLUTION_ID = ?");

            statement.setString(1, solution.getSolutionId().toString());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
