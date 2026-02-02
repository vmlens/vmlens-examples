package com.vmlens.projects.questdb;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;

import static com.vmlens.api.Runner.runParallel;


@Testcontainers
public class QuestDbTest {

    @Container
    static GenericContainer<?> questdb =
            new GenericContainer<>("questdb/questdb:latest")
                    .withExposedPorts(8812, 9009) // 8812 = PG wire
                    .withEnv("QDB_PG_USER", "admin")
                    .withEnv("QDB_PG_PASSWORD", "quest");

    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        String jdbcUrl = String.format(
                "jdbc:postgresql://%s:%d/qdb",
                questdb.getHost(),
                questdb.getMappedPort(8812)
        );

        connection = DriverManager.getConnection(
                jdbcUrl,
                "admin",
                "quest"
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        connection.close();
    }

    //@Test
    void shouldInsertAndQueryData() throws Exception {
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE trades (
                    symbol STRING,
                    price DOUBLE,
                    ts TIMESTAMP
                ) TIMESTAMP(ts)
            """);

            stmt.execute("""
                INSERT INTO trades VALUES
                ('AAPL', 187.5, now()),
                ('AAPL', 188.1, now()),
                ('GOOG', 142.3, now())
            """);
        }
        try(AllInterleavings allInterleavings =
                    new AllInterleavingsBuilder()
                            .build("questDb")) {
            while(allInterleavings.hasNext()) {
                runParallel(() -> {
                            try {
                                Statement stmt = connection.createStatement();
                                ResultSet rs = stmt.executeQuery("""
                SELECT symbol, count(*) 
                FROM trades 
                WHERE symbol = 'AAPL'
            """);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                        },
                        () -> {
                            try {
                                Statement stmt = connection.createStatement();
                                ResultSet rs = stmt.executeQuery("""
                SELECT symbol, count(*) 
                FROM trades 
                WHERE symbol = 'AAPL'
            """);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            }
        }


    }

}
