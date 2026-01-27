import com.vmlens.gradle.VMLens

plugins {
    id("java")
}

group = "com.vmlens"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
    mavenCentral()

}

val luceneVersion = "9.7.0"

dependencies {
    implementation("org.graalvm.sdk:graal-sdk:23.1.1")
    implementation("org.graalvm.truffle:truffle-api:23.1.1")
    implementation("org.graalvm.js:js-scriptengine:23.1.1")
    testImplementation("com.vmlens:api:1.2.25-SNAPSHOT")
    testImplementation("org.hamcrest:java-hamcrest:2.0.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.apache.lucene:lucene-core:${luceneVersion}")
    testImplementation("org.apache.lucene:lucene-analysis-common:${luceneVersion}")
    testImplementation("org.apache.lucene:lucene-queryparser:${luceneVersion}")

    testImplementation("org.testcontainers:junit-jupiter:1.12.4")
    testImplementation("org.testcontainers:testcontainers:2.0.3")
    testImplementation("org.testcontainers:postgresql:1.12.4")
    testImplementation("org.postgresql:postgresql:42.7.1")


    testImplementation("org.testcontainers:kafka:1.19.3")
    testImplementation("org.apache.kafka:kafka-clients:3.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")

    runtimeOnly("io.grpc:grpc-netty-shaded:1.77.0")
    implementation("io.grpc:grpc-protobuf:1.77.0")
    implementation("io.grpc:grpc-inprocess:1.77.0")
    implementation("io.grpc:grpc-stub:1.77.0")




        testImplementation ("org.testcontainers:cassandra:1.19.3")

        testImplementation("com.datastax.oss:java-driver-core:4.17.0")







}

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("com.vmlens:standalone:1.2.25-SNAPSHOT")
    }
}


tasks.register("vmlensReport") {
    doLast {
        VMLens().withShowAllRuns().process(layout.buildDirectory.getAsFile().get());
    }
}

tasks.test {
    doFirst{
        jvmArgs(VMLens().setup(layout.buildDirectory.getAsFile().get()))
    }
    // VMLens currently does not work with jacoco
    jvmArgumentProviders.removeIf { it::class.java.simpleName == "JacocoAgent" }
    useJUnitPlatform()
    finalizedBy("vmlensReport")
}



