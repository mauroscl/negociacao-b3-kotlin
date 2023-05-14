package br.com.mauroscl.model

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import br.com.mauroscl.parsing.NegocioRealizado
import br.com.mauroscl.parsing.PrazoNegociacao
import br.com.mauroscl.parsing.TipoNegociacao
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class FechamentoPosicaoServiceTest {
    @Test
    fun deveGerarFechamentoDayTrades() {
        val compra = NegocioRealizado.comValorOperacionalUnitario(
            "WIN J22",
            TipoNegociacao.COMPRA,
            PrazoNegociacao.DAYTRADE,
            6,
            BigDecimal(121645)
        )
        compra.adicionarCustos(BigDecimal(1.5), BigDecimal.ZERO, BigDecimal.ZERO)

        val venda = NegocioRealizado.comValorOperacionalUnitario(
            "WIN J22",
            TipoNegociacao.VENDA,
            PrazoNegociacao.DAYTRADE,
            6,
            BigDecimal(121390)
        )
        venda.adicionarCustos(BigDecimal(1.5), BigDecimal.ZERO, BigDecimal.ZERO)

        val negocios = listOf(compra, venda)

        val posicoesFechadas = FechamentoPosicaoService.fecharDayTrades(LocalDate.now(), negocios)

        assertThat(posicoesFechadas).hasSize(1)

        val fechamentoAtual = posicoesFechadas[0]
        assertThat(fechamentoAtual.data).isEqualTo(LocalDate.now())
        assertThat(fechamentoAtual.titulo).isEqualTo("WIN J22")
        assertThat(fechamentoAtual.prazo).isEqualTo(PrazoNegociacao.DAYTRADE)
        assertThat(fechamentoAtual.quantidade).isEqualTo(6)
        assertThat(fechamentoAtual.precoMedioCompra).isEqualByComparingTo("24329.25")
        assertThat(fechamentoAtual.precoMedioVenda).isEqualByComparingTo("24277.75")
        assertThat(fechamentoAtual.resultado).isEqualByComparingTo("-309")
    }
}