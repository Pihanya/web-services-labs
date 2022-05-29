package ru.gostev.core.model.dto

import java.time.LocalDate

data class PersonDto(

    val id: Long,

    val firstName: String,

    val secondName: String,

    val birthPlace: String,

    val birthDate: LocalDate,
)
