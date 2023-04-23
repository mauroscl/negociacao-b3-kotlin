package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ResumoFinanceiroTest {
    @Test
    fun deveCalcularCustos() {
        val resumoFinanceiro = ResumoFinanceiro(
            BigDecimal.valueOf(-1254),
            BigDecimal.valueOf(-1276.49),
            BigDecimal.valueOf(15.4),
            true,
            BigDecimal.ONE,
            BigDecimal.valueOf(0.5),
            BigDecimal.ONE
        )
        assertThat(resumoFinanceiro.valorOperacoes).isEqualByComparingTo("-1254")
        assertThat(resumoFinanceiro.valorNota).isEqualByComparingTo("-1276.49")
        assertThat(resumoFinanceiro.taxaOperacional).isEqualByComparingTo("15.40")
        assertThat(resumoFinanceiro.taxaTotal).isEqualByComparingTo("22.49")
        assertThat(resumoFinanceiro.custoParaRatear).isEqualByComparingTo("4.59")
    }
}