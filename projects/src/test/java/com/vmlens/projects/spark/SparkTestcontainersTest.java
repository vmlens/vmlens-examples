package com.vmlens.projects.spark;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.spark.api.java.function.FilterFunction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static com.vmlens.api.Runner.runParallel;

public class SparkTestcontainersTest {



    //@Test
    void shouldRunSimpleSparkJob() throws URISyntaxException {
        URL absoluteResource = SparkTestcontainersTest.class.getResource("/spark.txt");
        SparkSession spark = SparkSession.builder().master("local[2]").appName("Simple Application").getOrCreate();



        System.out.println("Pid: " +  ProcessHandle.current().pid());


        try(AllInterleavings allInterleavings =
                    new AllInterleavingsBuilder()
                            .build("spark")) {
            while (allInterleavings.hasNext()) {
                runParallel(() -> {

                            try {
                                Dataset<String>  logData = spark.read().textFile(Paths.get(absoluteResource.toURI()).toString()).cache();
                                logData.filter((FilterFunction<String>) s -> s.contains("a")).count();
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }

                        },
                        () -> {
                            try {
                                Dataset<String>  logData = spark.read().textFile(Paths.get(absoluteResource.toURI()).toString()).cache();
                                logData.filter((FilterFunction<String>) s -> s.contains("a")).count();
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }

                        });


            }
        }

    }
}
