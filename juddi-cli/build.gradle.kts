plugins {
    application
}

group = "ru.gostev.juddi"

application {
    mainClass.set("ru.gostev.juddi.CliMainKt")
}

dependencies {
    implementation("com.sun.xml.ws:jaxws-rt:2.3.2")
    implementation("com.sun.xml.ws:rt:2.3.2")
    implementation("com.sun.xml.ws:jaxws-ri:2.3.2")

    implementation(libs.juddi.uddiWs)
    implementation(libs.juddi.client)

    implementation(libs.clikt)
    implementation(libs.mordant)
}
