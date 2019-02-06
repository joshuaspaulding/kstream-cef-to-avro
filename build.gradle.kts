
plugins {
    java
}

group = "spaulding"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url="https://packages.confluent.io/maven/")
}

dependencies {
    compile("org.apache.avro:avro:1.8.2")
    compile("org.apache.kafka:kafka-streams:1.1.0")
    compile("io.confluent:kafka-avro-serializer:4.1.1")
    compile("io.confluent:kafka-streams-avro-serde:4.1.1")
    compile("com.google.guava:guava:27.0.1-jre")
    compile("org.apache.commons:commons-lang3:3.0")
    testCompile("junit", "junit", "4.12")
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
        attributes["Main-Class"] = "VF18.Application"
    }
    from(configurations.runtime.map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
