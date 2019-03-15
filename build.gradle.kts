plugins {
    java
    application
}

group = "spaulding"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven")
    }
}

dependencies {
    compile("org.apache.kafka:kafka-streams:2.1.0")
    compile("org.slf4j:slf4j-simple:1.7.25")
    compile("org.apache.avro:avro:1.8.2")
    compile("org.apache.commons:commons-lang3:3.0")
    compile("org.apache.commons:commons-configuration2:2.4")
    compile("commons-beanutils:commons-beanutils:1.9.3")
    compile("io.confluent:kafka-avro-serializer:4.1.0")
    compile("io.confluent:kafka-streams-avro-serde:4.1.0")
    testCompile("org.apache.kafka:kafka-streams-test-utils:2.1.0")
    testCompile("junit", "junit", "4.12")
}

application {
  mainClassName = "app.spaulding.kafka.streams.StreamsStarterApp"
}

configurations.all {
    exclude("slf4j-log4j12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Main-Class"] = "app.spaulding.kafka.streams.StreamsStarterApp"
    }
    from(configurations.runtime.map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
