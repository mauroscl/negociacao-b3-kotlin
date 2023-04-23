package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import br.com.mauroscl.parsing.NegocioRealizado.Companion.comValorOperacionalTotal
import br.com.mauroscl.parsing.NegocioRealizado.Companion.comValorOperacionalUnitario
import br.com.mauroscl.parsing.Pagina.Companion.comResumoFinanceiro
import br.com.mauroscl.parsing.Pagina.Companion.semResumoFinanceiro
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class NotaNegociacaoTest {
    @Test
    fun deveUnificarPaginaSemResumoFinanceiroComPaginaQueTiverResumoFinanceiro() {
        val paginaVistaSemResumoFinanceiro = semResumoFinanceiro(Mercado.AVISTA)
        val n1 = comValorOperacionalTotal(
            "BIDI4", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 100, BigDecimal.valueOf(2000)
        )
        paginaVistaSemResumoFinanceiro.adicionarNegocios(listOf(n1))
        val resumoFinanceiroVista = ResumoFinanceiro(
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(980),
            BigDecimal.valueOf(7.50),
            true, BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ONE
        )
        val paginaVistaComResumoFinanceiro =
            comResumoFinanceiro(Mercado.AVISTA, resumoFinanceiroVista)
        val n2 = comValorOperacionalTotal(
            "TRPL4", TipoNegociacao.VENDA, PrazoNegociacao.POSICAO, 200, BigDecimal.valueOf(5600)
        )
        paginaVistaComResumoFinanceiro.adicionarNegocios(listOf(n2))
        val resumoFinanceiroFuturo = ResumoFinanceiro(
            BigDecimal.valueOf(1020),
            BigDecimal.valueOf(990),
            BigDecimal.valueOf(19),
            true, BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ONE
        )
        val paginaFuturo = comResumoFinanceiro(Mercado.FUTURO, resumoFinanceiroFuturo)
        val n3 = comValorOperacionalUnitario(
            "WINM20", TipoNegociacao.VENDA, PrazoNegociacao.POSICAO, 2, BigDecimal.valueOf(95000)
        )
        paginaFuturo.adicionarNegocios(listOf(n3))
        val nota = NotaNegociacao()
        nota.adicionarPagina(paginaVistaSemResumoFinanceiro)
        nota.adicionarPagina(paginaVistaComResumoFinanceiro)
        nota.adicionarPagina(paginaFuturo)
        nota.unificarPaginas()
        val paginasUnificadas = nota.paginas
        assertThat(paginasUnificadas).hasSize(2)
        val paginaUnificadaVista = paginasUnificadas[0]
        assertThat(paginaUnificadaVista.negocios).hasSize(2)
        val paginaUnificadaFuturo = paginasUnificadas[1]
        assertThat(paginaUnificadaFuturo).isEqualTo(paginaFuturo)
    }
}
