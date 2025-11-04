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

dependencies {
    implementation("org.graalvm.sdk:graal-sdk:23.1.1")
    implementation("org.graalvm.truffle:truffle-api:23.1.1")
    implementation("org.graalvm.js:js-scriptengine:23.1.1")
    testImplementation("com.vmlens:api:1.2.22")
    testImplementation("org.hamcrest:java-hamcrest:2.0.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.youtrackdb:youtrackdb-core:0.5.0-SNAPSHOT")
}

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("com.vmlens:standalone:1.2.22")
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



