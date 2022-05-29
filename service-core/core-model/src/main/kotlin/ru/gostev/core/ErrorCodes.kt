package ru.gostev.core

object ErrorCodes {

    object Persons001UnknownError : ErrorCode("PERSONS-001")

    object Persons002FirstNameIsBlank : ErrorCode("PERSONS-002")

    object Persons003SecondNameIsBlank : ErrorCode("PERSONS-003")

    object Persons004BirthPlaceIsBlank : ErrorCode("PERSONS-004")

    object Persons005BirthDateIsIncorrect : ErrorCode("PERSONS-005")

    object Persons006EntityNotFound : ErrorCode("PERSONS-006")
}
