import java.lang.Thread.sleep

plugins {
    java
    kotlin("jvm") version "1.4.32"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
}

//val snitchVersion = "fe68a855f2"
val snitchVersion = "before_after-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("org.jetbrains.exposed:exposed-core:0.31.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.31.1")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.31.1")
    implementation("com.github.memoizr.snitch:sparkjava:$snitchVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.github.memoizr.snitch:core:$snitchVersion") {

        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.sparkjava:spark-core:2.9.3") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.github.memoizr:Shank:3.0.0") {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("com.auth0:java-jwt:3.16.0")
    implementation("com.github.f4b6a3:ulid-creator:3.1.0")


    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("com.lambdaworks:scrypt:1.4.0")

    implementation("com.beust:klaxon:5.4")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    testImplementation("org.mockito:mockito-core:3.8.0")

    testImplementation("khttp:khttp:1.0.0")
    testImplementation("com.github.memoizr:assertk:1.1.0")
    testImplementation("com.github.random-object-kreator:random-object-kreator:2.0.0")
    testImplementation("io.mockk:mockk:1.10.6")
    testImplementation("junit:junit:4.13")
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}


tasks.named<Zip>("distZip") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}


tasks.create<Exec>("startPostgres") {
    dependsOn("stopPostgres")
    commandLine("docker",
        "run",
        "-p", "5432:5432",
        "--name", "db",
        "-e", "POSTGRES_USER=test",
        "-e", "POSTGRES_DB=test",
        "-e", "POSTGRES_PASSWORD=test",
        "-d", "postgres:10.5")
    doLast {
        sleep(5000)
    }
}

tasks.create<Exec>("stopPostgres") {
    commandLine("docker", "kill", "db")
    commandLine("docker", "rm", "-f", "db")
}
tasks.create<JavaExec>("populate") {
    group = "Execution"
    description = "Run the main class with JavaExecTask"
    classpath = sourceSets["test"].runtimeClasspath
    main = "pl.estatemanager.PopulationKt"
}

application {
    mainClass.set("pl.estatemanager.ServerKt")
}