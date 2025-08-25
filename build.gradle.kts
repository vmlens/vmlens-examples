import com.vmlens.gradle.VMLens

plugins {
    id("java")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "com.vmlens"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("com.vmlens:api:1.2.12")
    testImplementation("org.hamcrest:java-hamcrest:2.0.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("com.vmlens:standalone:1.2.12")
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



