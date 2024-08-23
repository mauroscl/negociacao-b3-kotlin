package br.com.mauroscl

import br.com.mauroscl.infra.LoggerDelegate
import br.com.mauroscl.parsing.PdfLoader
import br.com.mauroscl.service.IProcessamentoOperacaoEmprestimoService
import picocli.CommandLine
import picocli.CommandLine.Command
import kotlin.system.exitProcess

@Command(name = "parser-aluguel")
class ParserAluguelCommand(
    private val pdfLoader: PdfLoader, private val operacaoEmprestimoService: IProcessamentoOperacaoEmprestimoService
) : Runnable {

    private val logger by LoggerDelegate()

    @CommandLine.Parameters(index = "0", arity = "1", description = ["Caminho do arquivo pdf a ser processado"])
    private lateinit var arquivo: String

    override fun run() {
        try {
            val paginas = pdfLoader.parseByArea(this.arquivo)
//            paginas.forEach { println(it) }
            this.operacaoEmprestimoService.processar(paginas);
        } catch (ex: Exception) {
            logger.error("Erro ao processar nota {}", arquivo, ex)
            println("Erro ao processar nota $arquivo")
            println(ex.message)
            println(ex.stackTrace)
            exitProcess(1)
        }
    }
}