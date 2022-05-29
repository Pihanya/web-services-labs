package ru.gostev.rest

import com.github.ajalt.clikt.core.subcommands
import ru.gostev.rest.cli.PersonsCli
import ru.gostev.rest.cli.CreateCommand
import ru.gostev.rest.cli.FindCommand
import ru.gostev.rest.cli.RemoveCommand
import ru.gostev.rest.cli.UpdateCommand
import ru.gostev.rest.client.PersonClient

fun main(args: Array<String>) {
    val personClient = PersonClient()
    PersonsCli()
        .subcommands(
            CreateCommand(personClient),
            FindCommand(personClient),
            UpdateCommand(personClient),
            RemoveCommand(personClient),
        )
        .main(args)
}
