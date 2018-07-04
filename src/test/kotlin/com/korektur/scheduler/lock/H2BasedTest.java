package com.korektur.scheduler.lock;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import javax.sql.DataSource;
import java.sql.SQLException;

public class H2BasedTest {

    protected Server server;
    protected DataSource dataSource;

    public void createServer() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        server = Server.createTcpServer();
        server.start();

        dataSource = createDataSource();
    }

    private DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:~/test");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    public void stopServer() {
        server.stop();
    }
}
