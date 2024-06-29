package br.com.mauroscl.parsing

import br.com.mauroscl.parsing.ExpressoesRegulares.NUMERO_DECIMAL_REGEX
import br.com.mauroscl.parsing.ExpressoesRegulares.NUMERO_INTEIRO_REGEX
import java.math.BigDecimal
import java.util.regex.Pattern

class NegocioMercadoFuturoParser internal constructor() {
    private val pattern: Pattern = Pattern.compile(REGEX)

    fun parse(negociacao: String): NegocioRealizado? {
        val matcher = pattern.matcher(negociacao)
        if (!matcher.find() || matcher.groupCount() != 14) {
            println("Não foi possível detectar o tipo de negociação: $negociacao")
            throw FormatoInformacaoDesconhecidoException("Não foi possível detectar o tipo de negociação de mercado futuro.")
        }
        val prazoNegociacaoLiteral = matcher.group(9)
        if (TAXA_PERMANENCIA == prazoNegociacaoLiteral) {
            println("Tem taxa de permanência com valor " + matcher.group(10))
        }
        if (!PRAZOS_COM_CUSTO.contains(prazoNegociacaoLiteral)) {
            return null
        }
        val tipoNegociacao: TipoNegociacao =
            TipoNegociacao.criarPorCodigo(matcher.group(1))
        val codigo = matcher.group(2) + matcher.group(3)
        val prazoNegociacao = obterPrazo(prazoNegociacaoLiteral)
        val quantidade =
            Integer.valueOf(NumeroFormatador.converterDePortuguesParaIngles(matcher.group(5)))
        val preco = BigDecimal(NumeroFormatador.converterDePortuguesParaIngles(matcher.group(7)))
        val taxaOperacional =
            BigDecimal(NumeroFormatador.converterDePortuguesParaIngles(matcher.group(13)))
        val negocioRealizado: NegocioRealizado =
            NegocioRealizado.comValorOperacionalUnitario(
                codigo,
                tipoNegociacao,
                prazoNegociacao,
                quantidade,
                preco
            )
        negocioRealizado.adicionarTaxaOperacional(taxaOperacional)
        return negocioRealizado
    }

    private fun obterPrazo(prazoNegociacaoLiteral: String): PrazoNegociacao {
        if (!PRAZOS_VALIDOS.contains(prazoNegociacaoLiteral)) {
            throw FormatoInformacaoDesconhecidoException("Não foi possível detectar o tipo de negócio.")
        }
        return if (DAYTRADE == prazoNegociacaoLiteral) PrazoNegociacao.DAYTRADE else PrazoNegociacao.POSICAO
    }

    private companion object {
        private const val DAYTRADE = "DAY TRADE"
        private const val TAXA_PERMANENCIA = "TX. PERMAN\u00CANCIA"
        private const val NORMAL = "NORMAL"
        private const val AJUSTE_POSICAO = "AJUPOS"
        private const val LIQUIDACAO = "LIQUIDA\u00C7\u00C3O"
        private val PRAZOS_COM_CUSTO: Collection<String> = listOf(DAYTRADE, NORMAL, LIQUIDACAO)
        private val PRAZOS_VALIDOS: Collection<String> = listOf(TAXA_PERMANENCIA, DAYTRADE, AJUSTE_POSICAO, NORMAL, LIQUIDACAO)
        private const val COMPRA_VENDA_REGEX = "[CV]"
        private const val CREDITO_DEBITO_REGEX = "[CD]"
        private const val CODIGO_ATIVO_REGEX = "[A-Z]{3}"
        private const val CONTRATO_REGEX = "\\w{3,4}"
        private const val DATA_REGEX = "\\d{2}\\/\\d{2}\\/\\d{4}"
        private const val TIPO_NEGOCIO_REGEX = ".+"
        private const val REGEX = "^(" + COMPRA_VENDA_REGEX + ")\\s" +
                "(" + CODIGO_ATIVO_REGEX + ")\\s" +
                "(" + CONTRATO_REGEX + ")\\s" +
                "@?(" + DATA_REGEX + "\\s)?" +
                "-?(" + NUMERO_INTEIRO_REGEX + ")\\s" +
                "(" + NUMERO_DECIMAL_REGEX + ")\\s" +
                "(" + TIPO_NEGOCIO_REGEX + ")\\s" +
                "(" + NUMERO_DECIMAL_REGEX + ")\\s" +
                "(" + CREDITO_DEBITO_REGEX + "?)\\s" +
                "(" + NUMERO_DECIMAL_REGEX + ")"
    }
}
