package br.com.mauroscl.parsing

import br.com.mauroscl.parsing.ExpressoesRegulares.NUMERO_DECIMAL_REGEX
import java.math.BigDecimal
import java.util.regex.Matcher
import java.util.regex.Pattern

class ResumoFinanceiroMercadoVistaParser {
    fun parse(conteudo: String): ResumoFinanceiro {
        val valorOperacoes = obterValorOperacoes(conteudo)
        val valorNota = obterValorNota(conteudo)
        val taxaOperacional = obterTaxaOperacional(conteudo).abs()
        val valorIrrfComSinal = obterValorIrrfNormal(conteudo)
        val irrfNormalParticipaDoCusto = valorIrrfComSinal.compareTo(BigDecimal.ZERO) < 0
        val valorIrrfNormal = valorIrrfComSinal.abs()
        val valorIrrfDayTrade = obterValorIrrfDayTrade(conteudo)
        val valorImpostos = obterValorImpostos(conteudo)
        return ResumoFinanceiro(
            valorOperacoes,
            valorNota,
            taxaOperacional,
            irrfNormalParticipaDoCusto,
            valorIrrfNormal,
            valorIrrfDayTrade,
            valorImpostos
        )
    }

    private fun obterValorOperacoes(conteudo: String): BigDecimal {
        val expressao = "Valor líquido das operações$NUMERO_CREDITO_DEBITO_REGEX"
        return obterNumeroComSinalOpcional(conteudo, expressao)
    }

    private fun obterValorNota(conteudo: String): BigDecimal {
        val expressao = "Líquido para\\s.+$NUMERO_CREDITO_DEBITO_REGEX"
        return obterNumeroComSinalOpcional(conteudo, expressao)
    }

    private fun obterTaxaOperacional(conteudo: String): BigDecimal {
        val expressao = "Taxa Operacional$NUMERO_CREDITO_DEBITO_REGEX"
        return obterNumeroComSinalOpcional(conteudo, expressao)
    }

    private fun obterValorIrrfNormal(conteudo: String): BigDecimal {
        return obterNumeroComSinalOpcional(conteudo, IRRF_NORMAL_REGEX)
    }

    private fun obterValorIrrfDayTrade(conteudo: String): BigDecimal {
        return if (conteudo.contains(IRRF_DAYTRADE_LITERAL)) obterNumeroSemSinal(
            conteudo,
            IRRF_DAY_TRADE_REGEX
        ) else BigDecimal.ZERO
    }

    private fun obterValorImpostos(conteudo: String): BigDecimal {
        val expressao = "Impostos$NUMERO_CREDITO_DEBITO_OPCIONAL_REGEX"
        return obterNumeroComSinalOpcional(conteudo, expressao).abs()
    }

    private fun obterNumeroComSinalOpcional(conteudo: String, expressao: String): BigDecimal {
        val matcher = aplicarExpressao(conteudo, expressao)
        val numero = matcher.group("numero")
        val sinal = matcher.group("cd") ?: ""
        return NumeroParser.parse(numero, sinal)
    }

    private fun obterNumeroSemSinal(conteudo: String, expressao: String): BigDecimal {
        val matcher = aplicarExpressao(conteudo, expressao)
        val numero = matcher.group("numero")
        return NumeroParser.parse(numero, "")
    }

    private fun aplicarExpressao(conteudo: String, expressao: String): Matcher {
        val pattern = Pattern.compile(expressao, Pattern.DOTALL)
        val matcher = pattern.matcher(conteudo)
        if (!matcher.find()) {
            throw FormatoInformacaoDesconhecidoException("Número não encontrado")
        }
        return matcher
    }

    companion object {
        private const val NUMERO_GROUP_NAME = "\\s(?<numero>"
        private const val IRRF_DAYTRADE_LITERAL = "IRRF Day Trade"
        private const val NUMERO_CREDITO_DEBITO_REGEX =
            "$NUMERO_GROUP_NAME$NUMERO_DECIMAL_REGEX)\\s(?<cd>C|D)"
        private const val NUMERO_CREDITO_DEBITO_OPCIONAL_REGEX =
            "$NUMERO_GROUP_NAME$NUMERO_DECIMAL_REGEX)(\\s(?<cd>C|D))?"
        private const val IRRF_NORMAL_REGEX = ("I\\.R\\.R\\.F\\.\\ss\\/\\soperações, base R\\$"
                + NUMERO_DECIMAL_REGEX
                + NUMERO_GROUP_NAME
                + NUMERO_DECIMAL_REGEX
                + ")(\\s(?<cd>C|D))?")
        private const val IRRF_DAY_TRADE_REGEX =
            "$IRRF_DAYTRADE_LITERAL.+Projeção R\\$\\s(?<numero>$NUMERO_DECIMAL_REGEX)"
    }
}
