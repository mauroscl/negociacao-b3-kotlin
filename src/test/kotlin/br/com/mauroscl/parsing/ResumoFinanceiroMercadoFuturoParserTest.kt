package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.IOException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ResumoFinanceiroMercadoFuturoParserTest {
    private var conteudo: String? = null
    @BeforeAll
    @Throws(IOException::class)
    fun carregarConteudo() {
        val dataLoader = TestInputDataLoader()
        conteudo = dataLoader.loadContent("ResumoFinanceiroFuturo.txt")
    }

    @Test
    fun deveObterResumoFinanceiro() {
        val parser = ResumoFinanceiroMercadoFuturoParser()
        val resumoFinanceiro = parser.parse(conteudo!!)
        assertThat(resumoFinanceiro.valorOperacoes).isEqualByComparingTo("-1505.49")
        assertThat(resumoFinanceiro.valorNota).isEqualByComparingTo("-1507.33")
        assertThat(resumoFinanceiro.taxaOperacional).isEqualByComparingTo("0.00")
        assertThat(resumoFinanceiro.valorIrrfNormal).isEqualByComparingTo("0.02")
        assertThat(resumoFinanceiro.valorIrrfDayTrade).isEqualByComparingTo("0.30")
        assertThat(resumoFinanceiro.valorImpostos).isEqualByComparingTo("0.50")
        assertThat(resumoFinanceiro.custoParaRatear).isEqualByComparingTo("1.04")
    }
}