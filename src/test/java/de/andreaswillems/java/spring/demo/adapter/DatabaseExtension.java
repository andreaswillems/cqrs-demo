package de.andreaswillems.java.spring.demo.adapter;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseExtension implements Extension, BeforeAllCallback, BeforeEachCallback {
    private static PostgreSQLContainer<?> database;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (database == null) {
            database = new PostgreSQLContainer<>("postgres:15.2");
            database.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    database.close();
                }
            });
        }
        System.setProperty("spring.datasource.url", database.getJdbcUrl());
        System.setProperty("spring.datasource.username", database.getUsername());
        System.setProperty("spring.datasource.password", database.getPassword());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Flyway flyway = Flyway.configure()
            .dataSource(database.getJdbcUrl(), database.getUsername(), database.getPassword())
            .cleanDisabled(false)
            .load();
        flyway.clean();
        flyway.migrate();
    }
}
