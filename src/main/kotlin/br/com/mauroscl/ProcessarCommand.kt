package br.com.mauroscl

import br.com.mauroscl.infra.LoggerDelegate
import br.com.mauroscl.infra.NotaNegociacaoRepository
import br.com.mauroscl.service.IProcessamentoNotaService
import picocli.CommandLine
import picocli.CommandLine.Command
import java.time.LocalDate
import javax.inject.Inject

@Command(name = "processar-nota")
class ProcessarCommand(@Inject var processamentoNotaService: IProcessamentoNotaService): Runnable {

    @CommandLine.Parameters(index = "0", arity = "1" )
    private lateinit var data: LocalDate

    override fun run() {
        processamentoNotaService.processar(data)
    }
}