plugins {
    application
}

application {
    mainClass.set("ru.gostev.jaxws.CliMainKt")
}

dependencies {
    runtimeOnly(libs.sun.xml.jaxwsRt)

    implementation(projects.jaxWsService.client)
    implementation(libs.clikt)
    implementation(libs.mordant)

    testImplementation(libs.kotlin.test)
}
