package com.will.currency.exchange.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionManager {
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final String POOL_KEY = "db.pool.key";
    private static final String JDBC_CLASS_NAME = "org.sqlite.JDBC";
    private static final String JDBC_PREFIX = "jdbc:sqlite:";
    private static final String DB_NAME = "db.sqlite";
    private static BlockingQueue<Connection> pool;

    static {
        initializeConnectionPool();
    }

    private static void initializeConnectionPool() {
        String poolSizeStr = PropertiesUtil.get(POOL_KEY);
        int poolSize = poolSizeStr == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSizeStr);
        pool = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Connection connection = open();
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close") ?
                            pool.add((Connection) proxy) :
                            method.invoke(connection, args));
            pool.add(proxyConnection);
        }
    }

    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection open() {
        Connection connection;
        try {
            URL resource = ConnectionManager.class.getClassLoader().getResource(DB_NAME);
            if (resource == null) {
                throw new RuntimeException("Database file not found in resources!");
            }
            String absolutePath = new File(resource.toURI()).getAbsolutePath();
            String url = JDBC_PREFIX + absolutePath;
            Class.forName(JDBC_CLASS_NAME);
            connection = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
