package ru.gostev.juddi

import com.github.ajalt.clikt.core.subcommands
import ru.gostev.juddi.cli.FindCommand
import ru.gostev.juddi.cli.JuddiCli
import ru.gostev.juddi.cli.PublishCommand

fun main(args: Array<String>) {
    JuddiCli()
        .subcommands(
            FindCommand(),
            PublishCommand(),
        )
        .main(args)
}
