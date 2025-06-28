buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:10.7.2")
    }
}

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jooq.jooq-codegen-gradle") version "3.19.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
    id("org.flywaydb.flyway") version "10.15.0"
    id("com.avast.gradle.docker-compose") version "0.17.1"
}

group = "com.k1e1n04"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Database
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql:42.7.7")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Testcontainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter:1.21.2")
    testImplementation("org.testcontainers:postgresql:1.21.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    // jOOQ
    jooqCodegen("org.postgresql:postgresql:42.7.7")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

sourceSets {
    main {
        java.srcDirs("build/generated-sources/jooq")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// jOOQコード生成をKotlinコンパイルの前に実行
tasks.named("compileKotlin") {
    dependsOn("jooqCodegen")
}

// jOOQコード生成をFlywayマイグレーションの後に実行
tasks.named("jooqCodegen") {
    dependsOn(tasks.named("flywayMigrate"))
}

// KtLintから生成されたjOOQコードを除外
tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
    mustRunAfter("jooqCodegen")
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    filter {
        exclude("**/build/generated-sources/**")
    }
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/book_management"
            user = System.getenv("DB_USER") ?: "app"
            password = System.getenv("DB_PASSWORD") ?: "password"
        }

        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
                includes = ".*"
            }

            target {
                packageName = "com.k1e1n04.bookmanagement.jooq"
                directory = "build/generated-sources/jooq"
            }
        }
    }
}

flyway {
    url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/book_management"
    user = System.getenv("DB_USER") ?: "app"
    password = System.getenv("DB_PASSWORD") ?: "password"
    locations = arrayOf("classpath:db/migration")
}
