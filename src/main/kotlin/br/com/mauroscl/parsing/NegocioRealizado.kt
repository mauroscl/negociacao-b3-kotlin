package br.com.mauroscl.parsing

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class NegocioRealizado private constructor(
    val titulo: String,
    val tipo: TipoNegociacao,
    val prazo: PrazoNegociacao,
    val quantidade: Int,
    // valor sem custos
    val valorOperacional: BigDecimal
) {
    val valorOperacionalEmMoeda: BigDecimal
    var taxaOperacional: BigDecimal = BigDecimal.ZERO

    // é calculado em cima da taxa de corretagem. Deve ser rateado de forma igual por todos os ativos
    // negociados
    var valorImpostos: BigDecimal
    var outrosCustos: BigDecimal

    // valor com custos
    var valorLiquidacao: BigDecimal

    // valor com custos dividido pela quantidade
    var valorLiquidacaoUnitario: BigDecimal

    init {
        valorImpostos = BigDecimal.ZERO
        outrosCustos = BigDecimal.ZERO
        valorLiquidacao = BigDecimal.ZERO
        valorLiquidacaoUnitario = BigDecimal.ZERO
        val prefixo = titulo.substring(0, 3)
        val multiplicador = MULTIPLICADOR_MAP.getOrDefault(prefixo, BigDecimal.ONE)
        valorOperacionalEmMoeda = valorOperacional.multiply(multiplicador)
    }

    fun temTaxaOperacional(): Boolean {
        return !titulo.contains("FII")
    }

    fun obterChave(): NegociacaoChave {
        return NegociacaoChave(titulo, tipo, prazo)
    }

    fun adicionarTaxaOperacional(taxaOperacional: BigDecimal) {
        this.taxaOperacional = taxaOperacional
    }

    fun adicionarCustos(
        taxaOperacional: BigDecimal,
        outrosCustos: BigDecimal,
        valorImpostos: BigDecimal
    ) {
        if (titulo.startsWith(CODIGO_MINI_SP)) {
            println(
                String.format(
                    "Não é possível calcular o custo corretamente para contratos WSP: %s", tipo
                )
            )
        }
        // no mercado futuro a taxa operacional já vem setada. Não precisa sobrescrever
        if (temTaxaOperacional()) {
            this.taxaOperacional = taxaOperacional
        }
        this.valorImpostos = valorImpostos
        this.outrosCustos = outrosCustos
        valorLiquidacao = if (TipoNegociacao.COMPRA == tipo) {
            valorOperacionalEmMoeda.add(taxaOperacional).add(outrosCustos).add(valorImpostos)
        } else {
            valorOperacionalEmMoeda
                .subtract(taxaOperacional)
                .subtract(outrosCustos)
                .subtract(valorImpostos)
        }
        if (quantidade > 0) {
            valorLiquidacaoUnitario = valorLiquidacao.divide(
                BigDecimal.valueOf(quantidade.toLong()), 10, RoundingMode.HALF_UP
            )
        }
    }

    val quantidadeComSinal get() = if (this.tipo == TipoNegociacao.COMPRA) this.quantidade else - this.quantidade

    override fun toString(): String {
        val numberFormat = NumberFormat.getInstance(Locale.forLanguageTag("pt-BR"))
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 10
        numberFormat.isGroupingUsed = false
        return ("NegocioRealizado("
                + "titulo='"
                + titulo
                + '\''
                + ", tipo="
                + tipo
                + ", prazo="
                + prazo
                + ", quantidade="
                + quantidade
                +  //   ", valorOperacional=" + valorOperacional +
                ", valorOperacionalEmMoeda="
                + valorOperacionalEmMoeda
                + ", taxaOperacional="
                + taxaOperacional
                +  //  ", outrosCustos=" + outrosCustos +
                ", valorLiquidacao="
                + numberFormat.format(valorLiquidacao)
                + ", valorLiquidacaoUnitario="
                + numberFormat.format(valorLiquidacaoUnitario)
                + ')')
    }

    companion object {
        private val MULTIPLICADOR_MAP = mapOf(
            Pair("WDO", BigDecimal.TEN),
            Pair("WIN", BigDecimal.valueOf(0.2)),
            Pair("BGI", BigDecimal.valueOf(330)),
            Pair("CCM", BigDecimal.valueOf(450))
        )
        private const val CODIGO_MINI_SP = "WSP"
        @JvmStatic
        fun comValorOperacionalUnitario(
            titulo: String,
            tipo: TipoNegociacao,
            prazo: PrazoNegociacao,
            quantidade: Int,
            valorOperacionalUnitario: BigDecimal
        ): NegocioRealizado {
            val valorOperacionalTotal =
                valorOperacionalUnitario.multiply(BigDecimal.valueOf(quantidade.toLong()))
            return NegocioRealizado(titulo, tipo, prazo, quantidade, valorOperacionalTotal)
        }

        @JvmStatic
        fun comValorOperacionalTotal(
            titulo: String,
            tipo: TipoNegociacao,
            prazo: PrazoNegociacao,
            quantidade: Int,
            valorOperacionalTotal: BigDecimal
        ): NegocioRealizado {
            return NegocioRealizado(titulo, tipo, prazo, quantidade, valorOperacionalTotal)
        }
    }
}
