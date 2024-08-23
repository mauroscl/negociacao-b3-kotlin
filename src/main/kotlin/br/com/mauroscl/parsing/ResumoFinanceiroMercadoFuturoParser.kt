package br.com.mauroscl.parsing

import br.com.mauroscl.parsing.ExpressoesRegulares.NUMERO_DECIMAL_REGEX
import java.math.BigDecimal
import java.util.regex.Pattern

class ResumoFinanceiroMercadoFuturoParser {
    fun parse(conteudo: String): ResumoFinanceiro {
        return try {
            val valorOperacoes = procurarValorComSinal(conteudo, CABECALHO1, CABECALHO2)
            val valorLiquido = procurarValorComSinal(conteudo, CABECALHO4_PATTERN, LINHA_AVISO)
            val custosMap = obterCustos(conteudo)
            val valorImpostos = obterValorImpostos(conteudo)
            ResumoFinanceiro(
                valorOperacoes,
                valorLiquido,
                custosMap[TAXA_OPERACIONAL_KEY]!!,
                false,
                custosMap[IRRF_NORMAL_KEY]!!,
                custosMap[IRRF_DAYTRADE_KEY]!!,
                valorImpostos
            )
        } catch (e: FormatoInformacaoDesconhecidoException) {
            println("Erro ao fazer parse do resumo financeiro")
            println(conteudo)
            throw e
        }
    }

    private fun procurarValorComSinal(
        conteudo: String, delimitadorInicial: String, delimitadorFinal: String
    ): BigDecimal {
        val delimitadorInicialIndex = obterIndiceInicial(conteudo, delimitadorInicial)
        val inicio = delimitadorInicialIndex + delimitadorInicial.length
        val fim = obterIndiceInicial(conteudo, delimitadorFinal)
        val linha = conteudo.substring(inicio, fim).trim { it <= ' ' }
        return obterValorComSignal(linha)
    }

    private fun procurarValorComSinal(
        conteudo: String, delimitadorInicial: Pattern, delimitadorFinal: String
    ): BigDecimal {
        val inicio = obterIndiceFinal(conteudo, delimitadorInicial)
        val fim = obterIndiceInicial(conteudo, delimitadorFinal)
        val linha = conteudo.substring(inicio, fim).trim { it <= ' ' }
        return obterValorComSignal(linha)
    }

    private fun obterValorComSignal(texto: String): BigDecimal {
        val matcher = VALOR_COM_SINAL_PATTERN.matcher(texto)
        if (!matcher.find()) {
            throw FormatoInformacaoDesconhecidoException("Valor não encontrado: $texto")
        }
        return NumeroParser.parse(matcher.group(1), matcher.group(3))
    }

    private fun obterCustos(conteudo: String): Map<String, BigDecimal> {
        val delimitadorInicialIndex = obterIndiceInicial(conteudo, CABECALHO2)
        val inicio = delimitadorInicialIndex + CABECALHO2.length
        val fim = obterIndiceInicial(conteudo, CABECALHO3)
        val linha = conteudo.substring(inicio, fim)
        val matcher = TAXAS_PATTERN.matcher(linha)
        if (!matcher.find()) {
            throw FormatoInformacaoDesconhecidoException(
                "IRRF e Taxa operacional não encontrados: $linha"
            )
        }
        val irrfNormal = BigDecimal(
            NumeroFormatador.converterDePortuguesParaIngles(matcher.group(IRRF_NORMAL_GROUP_NAME))
        )
        val irrfDayTrade = BigDecimal(
            NumeroFormatador.converterDePortuguesParaIngles(
                matcher.group(IRRF_DAYTRADE_GROUP_NAME)
            )
        )
        val taxaOperacional = BigDecimal(
            NumeroFormatador.converterDePortuguesParaIngles(
                matcher.group(TAXA_OPERACIONAL_GROUP_NAME)
            )
        )
        return java.util.Map.of(
            IRRF_NORMAL_KEY, irrfNormal,
            IRRF_DAYTRADE_KEY, irrfDayTrade,
            TAXA_OPERACIONAL_KEY, taxaOperacional
        )
    }

    private fun obterValorImpostos(conteudo: String): BigDecimal {
        val inicio = obterIndiceFinal(conteudo, CABECALHO3)
        val fim = obterIndiceInicial(conteudo, CABECALHO4_PATTERN)
        val linha = conteudo.substring(inicio, fim)
        val matcher = NUMERO_DECIMAL_PATTERN.matcher(linha)
        for (i in 1..2) {
            if (!matcher.find()) {
                throw FormatoInformacaoDesconhecidoException("Valor impostos não encontrado: $linha")
            }
        }
        return BigDecimal(NumeroFormatador.converterDePortuguesParaIngles(matcher.group(0)))
    }

    private fun obterIndiceInicial(conteudo: String, delimitador: String): Int {
        val indice = conteudo.indexOf(delimitador)
        if (indice == -1) {
            throw FormatoInformacaoDesconhecidoException(
                DELIMITADOR_NAO_ENCONTRADO + delimitador
            )
        }
        return indice
    }

    private fun obterIndiceFinal(conteudo: String, delimitador: String): Int {
        val indice = conteudo.indexOf(delimitador)
        if (indice == -1) {
            throw FormatoInformacaoDesconhecidoException(
                DELIMITADOR_NAO_ENCONTRADO + delimitador
            )
        }
        return indice + delimitador.length
    }

    private fun obterIndiceInicial(conteudo: String, pattern: Pattern): Int {
        val matcher = pattern.matcher(conteudo)
        return if (matcher.find()) {
            matcher.start()
        } else {
            throw FormatoInformacaoDesconhecidoException(
                DELIMITADOR_NAO_ENCONTRADO + pattern.pattern()
            )
        }
    }

    private fun obterIndiceFinal(conteudo: String, pattern: Pattern): Int {
        val matcher = pattern.matcher(conteudo)
        return if (matcher.find()) {
            matcher.end()
        } else {
            throw FormatoInformacaoDesconhecidoException(
                DELIMITADOR_NAO_ENCONTRADO + pattern.pattern()
            )
        }
    }

    companion object {
        private val CABECALHO1 =
            "Venda dispon\u00EDvel Compra dispon\u00EDvel Venda Op\u00E7\u00F5es Compra Op\u00E7\u00F5es Valor dos neg\u00F3cios" + System.lineSeparator()
        private val CABECALHO2 =
            System.lineSeparator() + "IRRF IRRF Day Trade (proj.) Taxa operacional Taxa registro BM&F Taxas BM&F (emol+f.gar)" + System.lineSeparator()
        private val CABECALHO3 =
            " +Outros Custos Impostos Ajuste de posi\u00E7\u00E3o Ajuste day trade Total de custos operacionais" + System.lineSeparator()
        private val CABECALHO4_PATTERN = Pattern.compile(
            "Outros IRRF \\w+ Total Conta Investimento Total Conta Normal Total liquido \\(#\\) Total l\u00EDquido da nota"
        )
        private const val LINHA_AVISO =
            "+Custos BM&F, conforme Of\u00EDcio Circular BM&F 079/2007-DG"
        private const val VALOR_COM_SINAL_REGEX = "($NUMERO_DECIMAL_REGEX)\\s\\|\\s*([CD]?)$"
        private val VALOR_COM_SINAL_PATTERN = Pattern.compile(VALOR_COM_SINAL_REGEX)
        private const val IRRF_NORMAL_GROUP_NAME = "irrfnormal"
        private const val IRRF_DAYTRADE_GROUP_NAME = "irrfdaytrade"
        private const val TAXA_OPERACIONAL_GROUP_NAME = "taxaoperacional"
        private const val IRRF_NORMAL_KEY = "IRRF_NORMAL"
        private const val IRRF_DAYTRADE_KEY = "IRRF_DAYTRADE"
        private const val TAXA_OPERACIONAL_KEY = "TAXA_OPERACIONAL"
        private const val TAXAS_REGEX = ("(?<"
                + IRRF_NORMAL_GROUP_NAME
                + ">"
                + NUMERO_DECIMAL_REGEX
                + ")\\|[CD]?\\s+(?<"
                + IRRF_DAYTRADE_GROUP_NAME
                + ">"
                + NUMERO_DECIMAL_REGEX
                + ")"
                + "\\s(?<"
                + TAXA_OPERACIONAL_GROUP_NAME
                + ">"
                + NUMERO_DECIMAL_REGEX
                + ")")
        private val TAXAS_PATTERN = Pattern.compile(TAXAS_REGEX)
        private val NUMERO_DECIMAL_PATTERN = Pattern.compile(NUMERO_DECIMAL_REGEX)
        private const val DELIMITADOR_NAO_ENCONTRADO = "Delimitador não encontrado: "
    }
}
