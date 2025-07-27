import com.vmlens.report.assertion.OnDescriptionAndLeftBeforeRightNoOp
import com.vmlens.report.assertion.OnEventNoOp
import com.vmlens.setup.Setup

plugins {
    id("java")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "com.vmlens"
version = "1.2.7"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("com.vmlens:api:1.2.8-SNAPSHOT")
    testImplementation("org.hamcrest:java-hamcrest:2.0.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.vmlens:report:1.2.8-SNAPSHOT")
        classpath("com.vmlens:standalone:1.2.8-SNAPSHOT")
        classpath("com.vmlens:sync-bug:1.2.8-SNAPSHOT")
    }
}

tasks.register("vmlensReport") {
    doLast {
        val agentDirectory = File(buildDir, Setup.AGENT_DIRECTORY)
        val reportDirectory = File(project.buildDir.absolutePath + "/" + Setup.REPORT_DIRECTORY);
        val result = com.anarsoft.race.detection.main.ProcessEvents(
            File(Setup.eventDir(agentDirectory)).toPath(),
            reportDirectory.toPath(),
            OnDescriptionAndLeftBeforeRightNoOp(), OnEventNoOp()
        ).process()
    }
}


tasks.test {
    doFirst{
        val agentDirectory = File(buildDir, Setup.AGENT_DIRECTORY)
        val setup = Setup(agentDirectory, "").setup()
        jvmArgs = listOf(setup.argLine()) + (jvmArgs ?: listOf())
    }
    useJUnitPlatform()
    finalizedBy("vmlensReport")
}



