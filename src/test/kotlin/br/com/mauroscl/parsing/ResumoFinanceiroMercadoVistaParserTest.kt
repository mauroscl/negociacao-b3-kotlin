package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.math.BigDecimal

internal class ResumoFinanceiroMercadoVistaParserTest {
    @Test
    @Throws(IOException::class)
    fun deveParsearResumoFinanceiro() {
        val dataLoader = TestInputDataLoader()
        val conteudo = dataLoader.loadContent("ResumoFinanceiroVistaComIrrf.txt")
        val parser = ResumoFinanceiroMercadoVistaParser()
        val resumoFinanceiro = parser.parse(conteudo)
        assertThat(resumoFinanceiro.valorOperacoes).isEqualByComparingTo("-1254")
        assertThat(resumoFinanceiro.valorNota).isEqualByComparingTo("-1276.49")
        assertThat(resumoFinanceiro.taxaOperacional).isEqualByComparingTo("15.40")
        assertThat(resumoFinanceiro.valorIrrfNormal).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(resumoFinanceiro.irrfNormalParticipaDoCusto).isFalse()
        assertThat(resumoFinanceiro.valorIrrfDayTrade).isEqualByComparingTo("1.50")
        assertThat(resumoFinanceiro.valorImpostos).isEqualByComparingTo("1.64")
    }

    @Test
    @Throws(IOException::class)
    fun deveRetornarResumoFinanceiroSemIrrfDayTrade() {
        val dataLoader = TestInputDataLoader()
        val conteudo = dataLoader.loadContent("ResumoFinanceiroVistaSemIrrfDayTrade.txt")
        val parser = ResumoFinanceiroMercadoVistaParser()
        val resumoFinanceiro = parser.parse(conteudo)
        assertThat(resumoFinanceiro.valorIrrfNormal).isEqualByComparingTo("20")
        assertThat(resumoFinanceiro.irrfNormalParticipaDoCusto).isTrue()
        assertThat(resumoFinanceiro.valorIrrfDayTrade).isEqualByComparingTo(BigDecimal.ZERO)
    }
}