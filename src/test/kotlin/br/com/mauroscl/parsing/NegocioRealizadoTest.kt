package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import br.com.mauroscl.parsing.NegocioRealizado.Companion.comValorOperacionalTotal
import br.com.mauroscl.parsing.NegocioRealizado.Companion.comValorOperacionalUnitario
import org.aesh.command.option.Argument
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

internal class NegocioRealizadoTest {
    @Test
    fun deveCalcularValorOperacionalTotalQuandoCriarComValorUnitario() {
        val negocioRealizado = comValorOperacionalUnitario(
            "WINQ20", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 2, BigDecimal.valueOf(100000)
        )

        assertThat(negocioRealizado.valorOperacional).isEqualByComparingTo("200000")
        assertThat(negocioRealizado.valorOperacionalEmMoeda).isEqualByComparingTo("40000")
    }

    @Test
    fun emComprasDeveAdicionarCustosAoValorTotalParaChegarAoCustoFinal() {
        val negocio = comValorOperacionalTotal(
            "MGLU3", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 100, BigDecimal.valueOf(1000)
        )
        negocio.adicionarCustos(BigDecimal.valueOf(7.5), BigDecimal.valueOf(2.5), BigDecimal.ONE)
        assertThat(negocio.taxaOperacional).isEqualByComparingTo("7.50")
        assertThat(negocio.outrosCustos).isEqualByComparingTo("2.50")
        assertThat(negocio.valorImpostos).isEqualByComparingTo("1.00")
        assertThat(negocio.valorLiquidacao).isEqualByComparingTo("1011.00")
        println(negocio.toString())
    }

    @Test
    fun emVendasDeveSubtrairCustosAoValorTotalParaChegarAoCustoFinal() {
        val negocio = comValorOperacionalTotal(
            "MGLU3", TipoNegociacao.VENDA, PrazoNegociacao.POSICAO, 100, BigDecimal.valueOf(1000)
        )

        negocio.adicionarCustos(BigDecimal.valueOf(7.5), BigDecimal.valueOf(2.5), BigDecimal.ONE)
        assertThat(negocio.taxaOperacional).isEqualByComparingTo("7.50")
        assertThat(negocio.outrosCustos).isEqualByComparingTo("2.50")
        assertThat(negocio.valorImpostos).isEqualByComparingTo("1.00")
        assertThat(negocio.valorLiquidacao).isEqualByComparingTo("989.00")
    }

    @ParameterizedTest
    @MethodSource("gerarTitulos")
    fun deveAjustarNomeDoTituloRemovendoPartes(tituloInformado: String, tituloEsperado: String) {
        val negocio =
            comValorOperacionalTotal(tituloInformado, TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 1, BigDecimal.ONE)
        assertThat(negocio.titulo).isEqualTo(tituloEsperado)
    }

    private companion object {

        @JvmStatic
        fun gerarTitulos(): Stream<Arguments> = Stream.of(
            Arguments.of("SUZANO S.A. ON ED NM", "SUZANO S.A. ON NM"),
            Arguments.of("CEMIG ON EJ N1", "CEMIG ON N1"),
            Arguments.of("COCA COLA DRN ED", "COCA COLA DRN"),
            Arguments.of("INTELBRAS ON EDJ NM", "INTELBRAS ON NM"),
            Arguments.of("SUZANO S.A. ON NM", "SUZANO S.A. ON NM"),
            Arguments.of("CEMIG ON N1", "CEMIG ON N1"),
            Arguments.of("COCA COLA DRN", "COCA COLA DRN"),
            Arguments.of("INTELBRAS ON NM", "INTELBRAS ON NM")
        )

    }
}
