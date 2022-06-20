package ru.gostev.juddi.model

data class BusinessServiceDto(

    val name: String,

    val description: String,

    val serviceKey: String,

    val bindingTemplates: List<BindingTemplateDto>,
)
