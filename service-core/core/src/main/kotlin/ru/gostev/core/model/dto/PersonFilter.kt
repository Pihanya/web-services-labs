package ru.gostev.core.model.dto

import java.time.LocalDate

data class PersonFilter(

    val firstName: String? = null,

    val secondName: String? = null,

    val birthPlace: String? = null,

    val birthDateFrom: LocalDate? = null,

    val birthDateTo: LocalDate? = null,
)
