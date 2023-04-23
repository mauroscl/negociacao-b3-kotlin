package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import br.com.mauroscl.parsing.NegocioRealizado.Companion.comValorOperacionalTotal
import br.com.mauroscl.parsing.NegocioRealizado.Companion.comValorOperacionalUnitario
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class NegocioRealizadoTest {
    @Test
    fun deveCalcularValorOperacionalTotalQuandoCriarComValorUnitario() {
        val negocioRealizado = comValorOperacionalUnitario(
            "WINQ20", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 2, BigDecimal.valueOf(100000)
        )

        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("200000")
        assertThat (negocioRealizado.valorOperacionalEmMoeda).isEqualByComparingTo("40000")
    }

    @Test
    fun emComprasDeveAdicionarCustosAoValorTotalParaChegarAoCustoFinal() {
        val negocio = comValorOperacionalTotal(
            "MGLU3", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 100, BigDecimal.valueOf(1000)
        )
        negocio.adicionarCustos(BigDecimal.valueOf(7.5), BigDecimal.valueOf(2.5), BigDecimal.ONE)
        assertThat (negocio.taxaOperacional).isEqualByComparingTo("7.50")
        assertThat (negocio.outrosCustos).isEqualByComparingTo("2.50")
        assertThat (negocio.valorImpostos).isEqualByComparingTo("1.00")
        assertThat (negocio.valorLiquidacao).isEqualByComparingTo("1011.00")
        println(negocio.toString())
    }

    @Test
    fun emVendasDeveSubtrairCustosAoValorTotalParaChegarAoCustoFinal() {
        val negocio = comValorOperacionalTotal(
            "MGLU3", TipoNegociacao.VENDA, PrazoNegociacao.POSICAO, 100, BigDecimal.valueOf(1000)
        )

        negocio.adicionarCustos(BigDecimal.valueOf(7.5), BigDecimal.valueOf(2.5), BigDecimal.ONE)
        assertThat (negocio.taxaOperacional).isEqualByComparingTo("7.50")
        assertThat(negocio.outrosCustos).isEqualByComparingTo("2.50")
        assertThat(negocio.valorImpostos).isEqualByComparingTo("1.00")
        assertThat(negocio.valorLiquidacao).isEqualByComparingTo("989.00")
    }
}
