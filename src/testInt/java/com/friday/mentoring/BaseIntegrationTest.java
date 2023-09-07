package com.friday.mentoring;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseIntegrationTest {

    @Container
    static JdbcDatabaseContainer datasource = new PostgreSQLContainer("postgres:15.4");

    @BeforeAll
    static void beforeAll() {
        datasource.start();
    }

    @AfterAll
    static void afterAll() {
        datasource.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", datasource::getJdbcUrl);
        registry.add("spring.datasource.username", datasource::getUsername);
        registry.add("spring.datasource.password", datasource::getPassword);
    }
}
