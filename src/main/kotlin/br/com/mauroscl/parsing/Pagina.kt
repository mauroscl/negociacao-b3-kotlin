package br.com.mauroscl.parsing

import io.smallrye.mutiny.tuples.Tuple2
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

    fun obterNegocios(prazoNegociacao: PrazoNegociacao) =
        this.negocios
            .filter { negocio -> negocio.prazo == prazoNegociacao }

    fun obterNegocios() = this.negocios.toList()

    fun temResumoFinanceiro(): Boolean {
        return resumoFinanceiro != null
    }

    fun agruparNegocios() {
        val negociosAgrupados = this.negocios
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

        negociosAgrupados
            .filter { it.prazo == PrazoNegociacao.DAYTRADE }
            .groupBy { it.titulo }
            .forEach { entry ->
                ajustarNegociosDeDayTrade(entry.value)?.also { tuple ->
                    run {
                        negociosAgrupados.remove(tuple.item1)
                        negociosAgrupados.addAll(tuple.item2)
                    }
                }
            }

        this.negocios = negociosAgrupados
    }

    fun ratearCustos() {
        if (resumoFinanceiro == null) return;
        val valorLiquidoTotal = negocios.sumOf { it.valorOperacionalEmMoeda }
        if (valorLiquidoTotal.compareTo(BigDecimal.ZERO) == 0) return

        val quantidadeNegociosTributados = BigDecimal.valueOf(obterQuantidadesNegociosTributados().toLong())
        val taxaOperacional = resumoFinanceiro.taxaOperacional
            .divide(quantidadeNegociosTributados, 10, RoundingMode.HALF_UP)
        val valorImpostos = resumoFinanceiro.valorImpostos
            .divide(quantidadeNegociosTributados, 10, RoundingMode.HALF_UP)
        for (negocioRealizado in negocios) {
            val outrosCustos = resumoFinanceiro.custoParaRatear
                .multiply(negocioRealizado.valorOperacionalEmMoeda)
                .divide(valorLiquidoTotal, 2, RoundingMode.HALF_UP)
            negocioRealizado.adicionarCustos(taxaOperacional, outrosCustos, valorImpostos)
        }
    }

    /**
     * Ajusta negocios de daytrade quando tiverem quantidades diferentes entre COMPRA e VENDA.
     * primeiro elemento da tupla retorna o negocio para ser removido
     * segundo elemento da lista retorna os negocios para serem adicionados
     * se retornar null nenhuma negócio deve ser adicionado ou removido
     **/
    private fun ajustarNegociosDeDayTrade(daytradesRealizados: List<NegocioRealizado>): Tuple2<NegocioRealizado, List<NegocioRealizado>>? {
        if (daytradesRealizados.size != 2) throw RuntimeException("Agrupamento inválido")
        val negocio1 = daytradesRealizados[0]
        val negocio2 = daytradesRealizados[1]
        if (negocio1.quantidade == negocio2.quantidade) return null

        val negocioComMaiorQuantidade: NegocioRealizado
        val negocioComMenorQuantidade: NegocioRealizado
        if (negocio1.quantidade > negocio2.quantidade) {
            negocioComMaiorQuantidade = negocio1
            negocioComMenorQuantidade = negocio2
        } else {
            negocioComMaiorQuantidade = negocio2
            negocioComMenorQuantidade = negocio1
        }
        val quantidadePosicao = negocioComMaiorQuantidade.quantidade - negocioComMenorQuantidade.quantidade
        val negocioPosicao = NegocioRealizado.comValorOperacionalUnitario(
            negocioComMaiorQuantidade.titulo,
            negocioComMaiorQuantidade.tipo,
            PrazoNegociacao.POSICAO,
            quantidadePosicao,
            negocioComMaiorQuantidade.getValorOperacionalUnitario()
        )
        val negocioDayTrade = NegocioRealizado.comValorOperacionalUnitario(
            negocioComMaiorQuantidade.titulo,
            negocioComMaiorQuantidade.tipo,
            PrazoNegociacao.DAYTRADE,
            negocioComMenorQuantidade.quantidade,
            negocioComMaiorQuantidade.getValorOperacionalUnitario()
        )
        return Tuple2.of(negocioComMaiorQuantidade, listOf(negocioDayTrade, negocioPosicao))
    }

    private fun obterQuantidadesNegociosTributados(): Int {
        return negocios
            .count { it.temTaxaOperacional() }
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
