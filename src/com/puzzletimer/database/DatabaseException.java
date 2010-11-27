package com.puzzletimer.database;

import java.sql.SQLException;

@SuppressWarnings("serial")
public class DatabaseException extends RuntimeException {
    private SQLException sqlException;

    public DatabaseException(SQLException sqlException) {
        this.sqlException = sqlException;
    }

    public SQLException getSqlException() {
        return this.sqlException;
    }

    @Override
    public String getMessage() {
        return this.sqlException.getMessage();
    }
}
