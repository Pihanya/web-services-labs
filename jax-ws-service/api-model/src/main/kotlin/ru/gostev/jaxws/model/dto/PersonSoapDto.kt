package ru.gostev.jaxws.model.dto

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlRootElement
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import java.time.LocalDate
import ru.gostev.jaxws.util.LocalDateXmlAdapter

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class PersonSoapDto {

    var id: Long? = null

    var firstName: String? = null

    var secondName: String? = null

    var birthPlace: String? = null

    @XmlJavaTypeAdapter(LocalDateXmlAdapter::class)
    var birthDate: LocalDate? = null
}
