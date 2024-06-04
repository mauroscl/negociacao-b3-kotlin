package br.com.mauroscl

import br.com.mauroscl.infra.LoggerDelegate
import br.com.mauroscl.service.IProcessamentoNotaService
import br.com.mauroscl.service.ImportadorNota
import jakarta.transaction.Transactional
import picocli.CommandLine
import picocli.CommandLine.Command
import kotlin.system.exitProcess

@Command(name = "parser")
class ParserCommand(private val importadorNota: ImportadorNota, private val processamentoNotaService: IProcessamentoNotaService) :
    Runnable {

    private val logger by LoggerDelegate()

    @CommandLine.Parameters(index = "0", arity = "1", description = ["Caminho do arquivo pdf a ser processado"])
    private lateinit var arquivo: String

    @CommandLine.Option(
        names = ["--process"],
        description = ["Indica se as notas devem ser processadas", "true: persiste as notas e realiza o processamento", "false: apenas imprime o conteúdo das notas no console"],
        defaultValue = "true",
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    var processarNotas: Boolean = true

    @Transactional
    override fun run() {
        try {
            val notaNegociacao = importadorNota.executar(this.arquivo)
            for (pagina in notaNegociacao.paginas) {
                println(pagina.mercado)
                println(pagina.resumoFinanceiro)
                pagina.obterNegocios().forEach { println(it) }
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