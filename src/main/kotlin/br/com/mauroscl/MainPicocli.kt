package br.com.mauroscl

import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import picocli.CommandLine
import picocli.CommandLine.Command
import javax.inject.Inject


@QuarkusMain
@Command(name = "negociacao", subcommands = [ParserCommand::class, ProcessarCommand::class])
class MainPicocli : QuarkusApplication {

    @Inject
    private lateinit var factory: CommandLine.IFactory
    override fun run(vararg args: String): Int {
        return CommandLine(this, factory)
            .execute(*args)
    }
}
