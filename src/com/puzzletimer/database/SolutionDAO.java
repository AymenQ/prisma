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
import com.puzzletimer.parsers.ScrambleParser;
import com.puzzletimer.parsers.ScrambleParserProvider;
import com.puzzletimer.scramblers.Scrambler;
import com.puzzletimer.scramblers.ScramblerProvider;

public class SolutionDAO {
    private Connection connection;
    private ScramblerProvider scramblerProvider;
    private ScrambleParserProvider scrambleParserProvider;

    public SolutionDAO(
            Connection connection,
            ScramblerProvider scramblerProvider,
            ScrambleParserProvider scrambleParserProvider) {
        this.connection = connection;
        this.scramblerProvider = scramblerProvider;
        this.scrambleParserProvider = scrambleParserProvider;
    }

    public Solution[] getAll(Category category) {
        Scrambler scrambler = this.scramblerProvider.get(category.getScramblerId());
        ScrambleParser scramblerParser = this.scrambleParserProvider.get(scrambler.getScramblerInfo().getPuzzleId());

        ArrayList<Solution> solutions = new ArrayList<Solution>();

        try {
            PreparedStatement statement = this.connection.prepareStatement(
                "SELECT SOLUTION_ID, CATEGORY_ID, SCRAMBLER_ID, SEQUENCE, START, END, PENALTY, COMMENT " +
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
                String comment = resultSet.getString(8);

                Scramble scramble = new Scramble(scramblerId, scramblerParser.parse(sequence));
                Solution solution = new Solution(solutionId, categoryId, scramble, new Timing(start, end), penalty, comment);

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
        insert(new Solution[] { solution });
    }

    public void insert(Solution[] solutions) {
        try {
            this.connection.setAutoCommit(false);

            PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO SOLUTION VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            for (Solution solution : solutions) {
                statement.setString(1, solution.getSolutionId().toString());
                statement.setString(2, solution.getCategoryId().toString());
                statement.setString(3, solution.getScramble().getScramblerId());
                statement.setString(4, solution.getScramble().getRawSequence());
                statement.setTimestamp(5, new Timestamp(solution.getTiming().getStart().getTime()));
                statement.setTimestamp(6, new Timestamp(solution.getTiming().getEnd().getTime()));
                statement.setString(7, solution.getPenalty());
                statement.setString(8, solution.getComment());

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

    public void update(Solution solution) {
        try {
            PreparedStatement statement = this.connection.prepareStatement(
                "UPDATE SOLUTION SET END = ?, PENALTY = ?, COMMENT = ? WHERE SOLUTION_ID = ?");

            statement.setTimestamp(1, new Timestamp(solution.getTiming().getEnd().getTime()));
            statement.setString(2, solution.getPenalty());
            statement.setString(3, solution.getComment());
            statement.setString(4, solution.getSolutionId().toString());

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
