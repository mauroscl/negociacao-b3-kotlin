package br.com.mauroscl.parsing

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.math.BigDecimal
import java.math.RoundingMode

class Pagina @BsonCreator internal constructor(
    @BsonProperty("mercado") val mercado: Mercado,
    @BsonProperty("resumoFinanceiro") val resumoFinanceiro: ResumoFinanceiro?
) {
    @BsonProperty("negocios")
    var negocios: MutableList<NegocioRealizado>

    init {
        negocios = ArrayList()
    }

    fun adicionarNegocios(novasNegociacoes: List<NegocioRealizado>) {
        negocios.addAll(novasNegociacoes)
    }

    fun temResumoFinanceiro(): Boolean {
        return resumoFinanceiro != null
    }

    fun agruparNegocios() {
        negocios = negocios
            .groupBy { it.obterChave() }
            .entries
            .map {
                val quantidadeTotal = it.value.sumOf { nr -> nr.quantidade }
                val valorTotal = it.value.sumOf { nr -> nr.valorOperacional }
                NegocioRealizado.comValorOperacionalTotal(
                    it.key.titulo,
                    it.key.tipo,
                    it.key.prazo,
                    quantidadeTotal,
                    valorTotal
                )
            }
            .sortedBy { it.titulo }
            .toMutableList()
    }

    fun ratearCustos() {
        val valorLiquidoTotal = negocios.sumOf { it.valorOperacionalEmMoeda }
        if (valorLiquidoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return
        }

        val resumoFinanceiroNaoNulo = resumoFinanceiro!!

        val quantidadeNegociosTributados = BigDecimal.valueOf(obterQuantidadesNegociosTributados())
        val taxaOperacional = resumoFinanceiroNaoNulo.taxaOperacional
            .divide(quantidadeNegociosTributados, 10, RoundingMode.HALF_UP)
        val valorImpostos = resumoFinanceiroNaoNulo.valorImpostos
            .divide(quantidadeNegociosTributados, 10, RoundingMode.HALF_UP)
        for (negocioRealizado in negocios) {
            val outrosCustos = resumoFinanceiroNaoNulo.custoParaRatear
                .multiply(negocioRealizado.valorOperacionalEmMoeda)
                .divide(valorLiquidoTotal, 2, RoundingMode.HALF_UP)
            negocioRealizado.adicionarCustos(taxaOperacional, outrosCustos, valorImpostos)
        }
    }

    private fun obterQuantidadesNegociosTributados(): Long {
        return negocios.stream()
            .filter { obj: NegocioRealizado? -> obj!!.temTaxaOperacional() }
            .count()
    }

    companion object {
        @JvmStatic
        fun comResumoFinanceiro(mercado: Mercado, resumoFinanceiro: ResumoFinanceiro?): Pagina {
            return Pagina(mercado, resumoFinanceiro)
        }

        @JvmStatic
        fun semResumoFinanceiro(mercado: Mercado): Pagina {
            return Pagina(mercado, null)
        }
    }
}
