plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

application {
    mainClass.set("com.prostuti.server.ApplicationKt")
}

dependencies {
    implementation(project(":core:model")) {
        // Use the "server" (JVM) target of the KMP module
        targetConfiguration = "serverRuntimeElements"
    }

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.logback.classic)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.postgresql)
    implementation(libs.hikaricp)
    implementation(libs.dotenv.kotlin)

    implementation(libs.bcrypt)

    // CSV parsing (Apache Commons CSV — per SKILL.md §6, never manual split)
    implementation(libs.commons.csv)

    // Auth tests
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.junit)
}
