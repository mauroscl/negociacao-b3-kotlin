package br.com.mauroscl

import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import jakarta.inject.Inject
import picocli.CommandLine
import picocli.CommandLine.Command


@QuarkusMain
@Command(name = "negociacao", subcommands = [ParserCommand::class, ProcessarCommand::class, ParserAluguelCommand::class])
class MainPicocli : QuarkusApplication {

    @Inject
    private lateinit var factory: CommandLine.IFactory
    override fun run(vararg args: String): Int {
        return CommandLine(this, factory)
            .execute(*args)
    }
}
