package br.com.mauroscl.parsing

class PaginaMercadoVistaParser(
    private val resumoFinanceiroMercadoVistaParser: ResumoFinanceiroMercadoVistaParser,
    private val negocioMercadoVistaParser: NegocioMercadoVistaParser
) : IPaginaParser {
    override fun parse(conteudo: String): Pagina {
        val mercado = Mercado.AVISTA
        val pagina: Pagina
        val temResumoFinanceiro = !conteudo.contains(IDENTIFICADOR_SEM_RESUMO_FINANCEIRO)
        pagina = if (temResumoFinanceiro) {
            val resumoFinanceiro = resumoFinanceiroMercadoVistaParser.parse(conteudo)
            Pagina.comResumoFinanceiro(mercado, resumoFinanceiro)
        } else {
            Pagina.semResumoFinanceiro(mercado)
        }
        pagina.adicionarNegocios(obterNegociosRealizados(conteudo))
        return pagina
    }

    private fun obterNegociosRealizados(conteudo: String): List<NegocioRealizado> {
        val posicaoInicial =
            obterIndiceInicial(conteudo, IdentificadorMercado.IDENTIFICADOR_MERCADO_AVISTA)
        val posicaoFinal = obterIndiceInicial(conteudo, IDENTIFICADOR_RESUMO_FINANCEIRO)
        val negociosRealizados = conteudo.substring(posicaoInicial, posicaoFinal)
        return negociosRealizados.split(System.lineSeparator())
            .dropLastWhile { it.isEmpty() }
            .drop(1) // skip header
            .map { negociacao: String -> negocioMercadoVistaParser.parsearLinha(negociacao) }
    }

    private fun obterIndiceInicial(conteudo: String, delimitador: String): Int {
        val indice = conteudo.indexOf(delimitador)
        if (indice == -1) {
            throw FormatoInformacaoDesconhecidoException(
                "Delimitador não encontrado: $delimitador"
            )
        }
        return indice
    }

    companion object {
        private const val IDENTIFICADOR_RESUMO_FINANCEIRO =
            "Resumo dos Neg\u00F3cios Resumo Financeiro"
        private const val IDENTIFICADOR_SEM_RESUMO_FINANCEIRO = "Total Bovespa / Soma CONTINUA..."
    }
}
