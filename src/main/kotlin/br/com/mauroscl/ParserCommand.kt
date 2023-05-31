package br.com.mauroscl

import br.com.mauroscl.infra.LoggerDelegate
import br.com.mauroscl.parsing.NegocioRealizado
import br.com.mauroscl.parsing.PdfLoader
import br.com.mauroscl.service.IProcessamentoNotaService
import picocli.CommandLine
import picocli.CommandLine.Command
import javax.inject.Inject
import javax.transaction.Transactional
import kotlin.system.exitProcess

@Command(name = "parser")
class ParserCommand(@Inject var pdfLoader: PdfLoader, @Inject var processamentoNotaService: IProcessamentoNotaService) :
    Runnable {

    private val logger by LoggerDelegate()

    @CommandLine.Parameters(index = "0", arity = "1")
    private lateinit var arquivo: String

    @CommandLine.Option(names = ["--process"], defaultValue = "true")
    var processarNotas: Boolean = true

    @Transactional
    override fun run() {
        try {
            val notaNegociacao = pdfLoader.parseByArea(this.arquivo)
            for (pagina in notaNegociacao.paginas) {
                println(pagina.mercado)
                println(pagina.resumoFinanceiro)
                pagina.negocios.forEach { x: NegocioRealizado -> println(x) }
            }
            if (processarNotas) {
                processamentoNotaService.processar(notaNegociacao)
            }
        } catch (ex: Exception) {
            logger.error("Erro ao processar nota {}", arquivo, ex)
            exitProcess(1)
        }
    }
}