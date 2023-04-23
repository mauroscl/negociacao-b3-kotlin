package br.com.mauroscl.parsing

class NotaNegociacaoParser(private val identificadorMercado: IdentificadorMercado) {
    fun parse(paginas: List<String>): NotaNegociacao {
        val notaNegociacao = NotaNegociacao()
        paginas
            .stream()
            .map { pagina: String -> parsearPagina(pagina) }
            .forEach { pagina: Pagina -> notaNegociacao.adicionarPagina(pagina) }
        return notaNegociacao
    }

    private fun parsearPagina(pagina: String): Pagina {
        val mercado = identificadorMercado.obterMercado(pagina)
        return PAGINA_PARSER_MAP[mercado]!!.parse(pagina)
    }

    companion object {
        private val PAGINA_PARSER_MAP = mapOf(
            Pair( Mercado.AVISTA,
            PaginaMercadoVistaParser(
                ResumoFinanceiroMercadoVistaParser(),
                NegocioMercadoVistaParser()
            )),
            Pair( Mercado.FUTURO,
            PaginaMercadoFuturoParser(
                ResumoFinanceiroMercadoFuturoParser(),
                NegocioMercadoFuturoParser()
            ))
        )
    }
}
