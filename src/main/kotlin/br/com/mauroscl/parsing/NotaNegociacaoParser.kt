package br.com.mauroscl.parsing

import br.com.mauroscl.infra.LoggerDelegate
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

@ApplicationScoped
class NotaNegociacaoParser(@Inject var identificadorMercado: IdentificadorMercado) {

    private val pattern = Pattern.compile(regex)
    private val logger by LoggerDelegate()
    fun parse(paginas: List<String>): NotaNegociacao {
        val primeiraPagina = paginas.first()
        val data = parsearData(primeiraPagina)
        val notaNegociacao = NotaNegociacao(data)
        paginas
            .map { pagina: String -> parsearPagina(pagina) }
            .forEach { pagina: Pagina -> notaNegociacao.adicionarPagina(pagina) }
        return notaNegociacao
    }

    private fun parsearPagina(pagina: String): Pagina {
        val mercado = identificadorMercado.obterMercado(pagina)
        return PAGINA_PARSER_MAP[mercado]!!.parse(pagina)
    }

    private fun parsearData(pagina: String): LocalDate {
        val matcher = pattern.matcher(pagina)
        matcher.find()
        val dataString = matcher.group(1)
        return LocalDate.parse(dataString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }


    companion object {
        private const val DATA_REGEX = "\\d{2}\\/\\d{2}\\/\\d{4}"
        private const val regex = "\\d+\\s\\d\\s($DATA_REGEX)"

        private val PAGINA_PARSER_MAP = mapOf(
            Mercado.AVISTA to
            PaginaMercadoVistaParser(
                ResumoFinanceiroMercadoVistaParser(),
                NegocioMercadoVistaParser()
            ),
            Mercado.FUTURO to
            PaginaMercadoFuturoParser(
                ResumoFinanceiroMercadoFuturoParser(),
                NegocioMercadoFuturoParser()
            )
        )
    }
}
