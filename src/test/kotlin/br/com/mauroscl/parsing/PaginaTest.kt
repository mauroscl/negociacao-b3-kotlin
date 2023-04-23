package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import br.com.mauroscl.parsing.NegocioRealizado.Companion.comValorOperacionalTotal
import br.com.mauroscl.parsing.Pagina.Companion.comResumoFinanceiro
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class PaginaTest {
    @Test
    fun deveAgruparNegociacoesPorTipoPrazoTitulo() {
        val negociacoesDetalhadas = listOf(
            comValorOperacionalTotal(
                "BIDI4",
                TipoNegociacao.COMPRA,
                PrazoNegociacao.DAYTRADE,
                200,
                BigDecimal.valueOf(2000)
            ),  // 10
            comValorOperacionalTotal(
                "BIDI4",
                TipoNegociacao.COMPRA,
                PrazoNegociacao.DAYTRADE,
                300,
                BigDecimal.valueOf(3030)
            ),  // 10.10
            comValorOperacionalTotal(
                "BIDI4",
                TipoNegociacao.VENDA,
                PrazoNegociacao.DAYTRADE,
                100,
                BigDecimal.valueOf(1100)
            ),  // 11
            comValorOperacionalTotal(
                "BIDI4",
                TipoNegociacao.VENDA,
                PrazoNegociacao.DAYTRADE,
                400,
                BigDecimal.valueOf(4600)
            ),  // 11.5
            comValorOperacionalTotal(
                "BIDI4",
                TipoNegociacao.COMPRA,
                PrazoNegociacao.POSICAO,
                200,
                BigDecimal.valueOf(4500)
            ),  // 11.25
            comValorOperacionalTotal(
                "BIDI4",
                TipoNegociacao.COMPRA,
                PrazoNegociacao.POSICAO,
                100,
                BigDecimal.valueOf(1130)
            )
        ) // 11.30
        val pagina = comResumoFinanceiro(
            Mercado.AVISTA,
            ResumoFinanceiro(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(950),
                BigDecimal.valueOf(30),
                true, BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ONE
            )
        )
        pagina.adicionarNegocios(negociacoesDetalhadas)
        pagina.agruparNegocios()
        val negociacoesAgrupadas: List<NegocioRealizado> = pagina.negocios
        assertThat(negociacoesAgrupadas).hasSize(3)
        val compraDayTrade = negociacoesAgrupadas.first { n: NegocioRealizado ->
            n.obterChave() == NegociacaoChave(
                "BIDI4",
                TipoNegociacao.COMPRA,
                PrazoNegociacao.DAYTRADE
            )
        }
        assertThat(compraDayTrade.quantidade).isEqualTo(500)
        assertThat(compraDayTrade.valorOperacional).isEqualByComparingTo("5030")

        val vendaDayTrade = negociacoesAgrupadas.first { n: NegocioRealizado ->
            n.obterChave() == NegociacaoChave(
                "BIDI4", TipoNegociacao.VENDA, PrazoNegociacao.DAYTRADE
            )
        }

        assertThat(vendaDayTrade.quantidade).isEqualTo(500)
        assertThat(vendaDayTrade.valorOperacional).isEqualByComparingTo("5700")

        val compraPosicao = negociacoesAgrupadas
            .first { n: NegocioRealizado ->
                n.obterChave() == NegociacaoChave(
                    "BIDI4",
                    TipoNegociacao.COMPRA,
                    PrazoNegociacao.POSICAO
                )
            }

        assertThat(compraPosicao.quantidade).isEqualTo(300)
        assertThat(compraPosicao.valorOperacional).isEqualByComparingTo("5630")
    }

    /*
  Teste baseado na nota de 20/03/2020
   */
    @Test
    fun deveRatearCustosEntreOsNegociosRealizados() {
        val n1 = comValorOperacionalTotal(
            "BBAS3", TipoNegociacao.VENDA, PrazoNegociacao.POSICAO, 400, BigDecimal.valueOf(10920)
        )
        val n2 = comValorOperacionalTotal(
            "BRFS3", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 100, BigDecimal.valueOf(1543)
        )
        val n3 = comValorOperacionalTotal(
            "BRFS3", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 200, BigDecimal.valueOf(3086)
        )
        val n4 = comValorOperacionalTotal(
            "EQTL3", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 400, BigDecimal.valueOf(7356)
        )
        val n5 = comValorOperacionalTotal(
            "EQTL3", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 100, BigDecimal.valueOf(1839)
        )
        val resumoFinanceiro = ResumoFinanceiro(
            BigDecimal.valueOf(-2904),
            BigDecimal.valueOf(-2937.16),
            BigDecimal.valueOf(23.1),
            false, BigDecimal.valueOf(0.54),
            BigDecimal.ZERO,
            BigDecimal.valueOf(2.46)
        )
        val pagina = comResumoFinanceiro(Mercado.AVISTA, resumoFinanceiro)
        pagina.adicionarNegocios(listOf(n1, n2, n3, n4, n5))
        val notaNegociacao = NotaNegociacao()
        notaNegociacao.adicionarPagina(pagina)
        notaNegociacao.unificarPaginas()
        val paginaComCustos: Pagina = notaNegociacao.paginas[0]
        assertThat(paginaComCustos.negocios).hasSize(3)
        val nc1 = paginaComCustos.negocios[0]
        assertThat(nc1.titulo).isEqualTo("BBAS3")
        assertThat(nc1.valorOperacional).isEqualByComparingTo("10920")
        assertThat(nc1.taxaOperacional).isEqualByComparingTo("7.7")
        assertThat(nc1.valorImpostos).isEqualByComparingTo("0.82")
        assertThat(nc1.outrosCustos).isEqualByComparingTo("3.35")
        assertThat(nc1.valorLiquidacao).isEqualByComparingTo("10908.13")
        assertThat(nc1.valorLiquidacaoUnitario).isEqualByComparingTo("27.270325")
        val nc2 = paginaComCustos.negocios[1]
        assertThat(nc2.titulo).isEqualTo("BRFS3")
        assertThat(nc2.valorOperacional).isEqualByComparingTo("4629")
        assertThat(nc2.taxaOperacional).isEqualByComparingTo("7.7")
        assertThat(nc2.valorImpostos).isEqualByComparingTo("0.82")
        assertThat(nc2.outrosCustos).isEqualByComparingTo("1.42")
        assertThat(nc2.valorLiquidacao).isEqualByComparingTo("4638.94")
        assertThat(nc2.valorLiquidacaoUnitario).isEqualByComparingTo("15.4631333333")
        val nc3 = paginaComCustos.negocios[2]
        assertThat(nc3.titulo).isEqualTo("EQTL3")
        assertThat(nc3.valorOperacional).isEqualByComparingTo("9195")
        assertThat(nc3.taxaOperacional).isEqualByComparingTo("7.7")
        assertThat(nc3.valorImpostos).isEqualByComparingTo("0.82")
        assertThat(nc3.outrosCustos).isEqualByComparingTo("2.82")
        assertThat(nc3.valorLiquidacao).isEqualByComparingTo("9206.34")
        assertThat(nc3.valorLiquidacaoUnitario).isEqualByComparingTo("18.41268")
    }
}
