package com.dev.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource {
    Connection getConnection() throws SQLException;
    void close() throws SQLException;

}
