package com.will.currency.exchange.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String POOL_KEY = "db.pool.key";
    private static final int DEFAULT_POOL_SIZE = 10;
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
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(PropertiesUtil.get(URL_KEY));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
