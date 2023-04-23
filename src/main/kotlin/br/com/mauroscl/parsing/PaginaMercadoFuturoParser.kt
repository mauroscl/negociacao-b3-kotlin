package br.com.mauroscl.parsing

class PaginaMercadoFuturoParser(
    private val resumoFinanceiroParser: ResumoFinanceiroMercadoFuturoParser,
    private val negocioParser: NegocioMercadoFuturoParser
) : IPaginaParser {
    override fun parse(conteudo: String): Pagina {
        val pagina: Pagina
        val mercado = Mercado.FUTURO
        val temResumoFinanceiro = !conteudo.contains(IDENTIFICADOR_SEM_RESUMO_FINANCEIRO)
        val identificadorFimNegocios: String
        if (temResumoFinanceiro) {
            val resumoFinanceiro = resumoFinanceiroParser.parse(conteudo)
            pagina = Pagina.comResumoFinanceiro(mercado, resumoFinanceiro)
            identificadorFimNegocios = IDENTIFICADOR_RESUMO_FINANCEIRO
        } else {
            pagina = Pagina.semResumoFinanceiro(mercado)
            identificadorFimNegocios = IDENTIFICADOR_SEM_RESUMO_FINANCEIRO
        }
        pagina.adicionarNegocios(obterNegociosRealizados(conteudo, identificadorFimNegocios))
        return pagina
    }

    private fun obterNegociosRealizados(
        conteudo: String,
        identificadorFimNegocios: String
    ): List<NegocioRealizado> {
        val posicaoInicial =
            obterIndiceInicial(conteudo, IdentificadorMercado.IDENTIFICADOR_MERCADO_FUTURO)
        val posicaoFinal = obterIndiceInicial(conteudo, identificadorFimNegocios)
        val negociosRealizados = conteudo.substring(posicaoInicial, posicaoFinal)

        return negociosRealizados.split(System.lineSeparator())
            .drop(1)
            .dropLastWhile { it.isEmpty() }
            .mapNotNull { negocioParser.parse(it) }
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
            "Venda dispon\u00EDvel Compra dispon\u00EDvel Venda Op\u00E7\u00F5es Compra Op\u00E7\u00F5es Valor dos neg\u00F3cios"
        private const val IDENTIFICADOR_SEM_RESUMO_FINANCEIRO = "CONTINUA..."
    }
}
