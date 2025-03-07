package com.dev.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.beans.PropertyVetoException;

public class C3P0DataSource implements DataSource {
    private final ComboPooledDataSource dataSource;

    public C3P0DataSource(String url, String user, String password) throws PropertyVetoException {
        dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() throws SQLException {
        dataSource.close();
    }
}
