package br.com.mauroscl.parsing

import java.math.BigDecimal

class ResumoFinanceiro internal constructor(
    val valorOperacoes: BigDecimal,
    val valorNota: BigDecimal,
    val taxaOperacional: BigDecimal,
    val irrfNormalParticipaDoCusto: Boolean,
    val valorIrrfNormal: BigDecimal,
    val valorIrrfDayTrade: BigDecimal,
    val valorImpostos: BigDecimal
) {
    val taxaTotal: BigDecimal = valorOperacoes.subtract(valorNota)
    val custoParaRatear: BigDecimal

    init {
        var custoRatear = taxaTotal
            .subtract(taxaOperacional)
            .subtract(valorImpostos)
            .subtract(valorIrrfDayTrade)
        if (irrfNormalParticipaDoCusto) {
            custoRatear = custoRatear.subtract(valorIrrfNormal)
        }
        custoParaRatear = custoRatear
    }

    override fun toString(): String {
        return "ResumoFinanceiro(valorOperacoes=$valorOperacoes, valorNota=$valorNota, " +
                "taxaOperacional=$taxaOperacional, valorIrrfDayTrade=$valorIrrfDayTrade, " +
                "irrfNormalParticipaDoCusto=$irrfNormalParticipaDoCusto, valorIrrfNormal=$valorIrrfNormal, " +
                "taxaTotal=$taxaTotal, custoParaRatear=$custoParaRatear, valorImpostos=$valorImpostos)"
    }
}
