package br.com.mauroscl.parsing

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.math.BigDecimal

class ResumoFinanceiro @BsonCreator internal constructor(
    @BsonProperty("valorOperacoes") val valorOperacoes: BigDecimal,
    @BsonProperty("valorNota")val valorNota: BigDecimal,
    @BsonProperty("taxaOperacional")val taxaOperacional: BigDecimal,
    @BsonProperty("irrfNormalParticipaDoCusto")val irrfNormalParticipaDoCusto: Boolean,
    @BsonProperty("valorIrrfNormal")val valorIrrfNormal: BigDecimal,
    @BsonProperty("valorIrrfDayTrade")val valorIrrfDayTrade: BigDecimal,
    @BsonProperty("valorImpostos")val valorImpostos: BigDecimal
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
