plugins {
    application
}

application {
    mainClass.set("ru.gostev.rest.CliMainKt")
}

dependencies {
    implementation(projects.restService.client)
    implementation(libs.clikt)
    implementation(libs.mordant)

    implementation(libs.kotlin.reflect)

    implementation(libs.kotlinLogging.jvm)
    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4jImpl)
}
