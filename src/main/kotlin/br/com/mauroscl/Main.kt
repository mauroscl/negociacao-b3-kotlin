package br.com.mauroscl

import br.com.mauroscl.parsing.IdentificadorMercado
import br.com.mauroscl.parsing.NegocioRealizado
import br.com.mauroscl.parsing.NotaNegociacaoParser
import br.com.mauroscl.parsing.PdfLoader
import java.util.function.Consumer
import kotlin.system.exitProcess

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            System.err.println("É necessário informar o caminho absoluto de um arquivo para ser processado.")
            System.err.println("Exemplo de uso:")
            System.err.println("java -jar negociacao-b3-1.0-SNAPSHOT.jar c:\\arquivos\\nota-fiscal.pdf")
            exitProcess(1)
        }
        val pdfLoader = PdfLoader(NotaNegociacaoParser(IdentificadorMercado()))
        val notaNegociacao = pdfLoader.parseByArea(args[0])
        notaNegociacao.unificarPaginas()
        for (pagina in notaNegociacao.paginas) {
            println(pagina.mercado)
            println(pagina.resumoFinanceiro)
            pagina.negocios.forEach(Consumer { x: NegocioRealizado -> println(x) })
        }
    }
}
