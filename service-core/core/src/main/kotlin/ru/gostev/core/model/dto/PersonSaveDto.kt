package ru.gostev.core.model.dto

import java.time.LocalDate

data class PersonSaveDto(

    val id: Long? = null,

    val firstName: String,

    val secondName: String,

    val birthPlace: String,

    val birthDate: LocalDate,
)
