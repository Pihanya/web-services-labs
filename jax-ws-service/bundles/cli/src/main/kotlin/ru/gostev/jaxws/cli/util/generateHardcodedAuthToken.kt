package ru.gostev.jaxws.cli.util

fun generateHardcodedAuthToken(): String =
    BasicAuthTokenGenerator.generateString(login = "login", password = "password")
