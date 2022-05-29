package ru.gostev.jaxws.client

import java.util.Date
import java.util.GregorianCalendar
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

private val DATATYPE_FACTORY: DatatypeFactory = DatatypeFactory.newInstance()

fun Date.toGregorianCalendar(): XMLGregorianCalendar = GregorianCalendar()
    .apply { timeInMillis = this@toGregorianCalendar.toInstant().toEpochMilli() }
    .let(DATATYPE_FACTORY::newXMLGregorianCalendar)
