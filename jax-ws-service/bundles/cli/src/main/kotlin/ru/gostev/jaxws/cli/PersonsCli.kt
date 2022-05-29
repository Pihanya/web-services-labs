package ru.gostev.jaxws.cli

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.context
import ru.gostev.jaxws.cli.util.ColorHelpFormatter

class PersonsCli : NoOpCliktCommand(
    name = "cli",
    help = "Command line interface for managing persons"
) {

    init {
        context { helpFormatter = ColorHelpFormatter() }
    }
}
