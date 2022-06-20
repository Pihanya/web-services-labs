package ru.gostev.juddi.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import ru.gostev.juddi.integration.ServiceFinder
import ru.gostev.juddi.model.BusinessServiceDto

class FindCommand : CliktCommand(name = "find", help = "Find service by name") {

    private val serviceName: String by option(help = "Name of service").required()

    override fun run() {
        val businessServices: List<BusinessServiceDto> = ServiceFinder().findBusinessServiceByName(serviceName)
        when {
            businessServices.isEmpty() -> echo("Could not find any business service by name [$serviceName]")
            (businessServices.size == 1) -> {
                echo("Found a single service by name [$serviceName]:")
                echo(businessServices.first())
            }
            else -> {
                echo("Found a [${businessServices.size}] services by name [$serviceName]")
                businessServices.forEach(::echo)
            }
        }
    }
}
