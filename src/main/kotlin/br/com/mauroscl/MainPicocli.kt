package br.com.mauroscl

import br.com.mauroscl.parsing.NegocioRealizado
import br.com.mauroscl.parsing.PdfLoader
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.function.Consumer
import javax.inject.Inject
import kotlin.system.exitProcess

@Command
class MainPicocli(@Inject private var pdfLoader: PdfLoader) : Runnable {

    @Parameters(index = "0")
    var arquivo: String? = null

    override fun run() {

        if (this.arquivo == null) {
            System.err.println("É necessário informar o caminho absoluto de um arquivo para ser processado.")
            System.err.println("Exemplo de uso:")
            System.err.println("java -jar negociacao-b3-1.0-SNAPSHOT.jar c:\\arquivos\\nota-fiscal.pdf")
            exitProcess(1)
        }
        val notaNegociacao = pdfLoader.parseByArea(this.arquivo!!)
        notaNegociacao.unificarPaginas()
        for (pagina in notaNegociacao.paginas) {
            println(pagina.mercado)
            println(pagina.resumoFinanceiro)
            pagina.negocios.forEach(Consumer { x: NegocioRealizado -> println(x) })
        }
    }
}
