package com.vmlens.projects.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetSocketAddress;

import static com.vmlens.api.Runner.runParallel;


@Testcontainers
class CassandraIntegrationTest {

    @Container
    static CassandraContainer<?> cassandra =
            new CassandraContainer<>("cassandra:4.1");

    private static CqlSession session;

    @BeforeAll
    static void setUp() {
        session = CqlSession.builder()
                .addContactPoint(
                        new InetSocketAddress(
                                cassandra.getHost(),
                                cassandra.getMappedPort(9042)))
                .withLocalDatacenter("datacenter1")
                .build();
    }

    @AfterAll
    static void tearDown() {
        if (session != null) {
            session.close();
        }
    }

    private static void createSchema(CqlSession session) {
        session.execute("""
            CREATE KEYSPACE IF NOT EXISTS test
            WITH replication = {
              'class': 'SimpleStrategy',
              'replication_factor': 1
            }
        """);

        session.execute("""
            CREATE TABLE IF NOT EXISTS test.users (
                id INT PRIMARY KEY,
                name TEXT
            )
        """);
    }

   @Test
    void shouldInsertAndReadData() {
        createSchema(session);
        try (AllInterleavings allInterleavings =
                     new AllInterleavingsBuilder()
                             .withRemoveCycleThreshold(5)
                             .build("cassandra")) {
            while (allInterleavings.hasNext()) {
                runParallel(
                        () -> {    session.execute(
                                "INSERT INTO test.users (id, name) VALUES (1, 'Alice')");
                        },
                        () -> {
                            ResultSet rs =
                                    session.execute(
                                            "SELECT name FROM test.users WHERE id = 1");

                            Row row = rs.one();
                        }
                );
            }
        }
    }
}
