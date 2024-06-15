package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isEqualToIgnoringGivenProperties
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class NotaEmprestimoParserTest{
    private val parser = NotaEmprestimoParser();
    @Test
    fun deveParsearNota(){
        val dataLoader = TestInputDataLoader()
        val conteudo = dataLoader.loadContent("NotaEmprestimo.txt")
        val notaEmprestimo = parser.parse(listOf(conteudo))
        assertThat(notaEmprestimo.dataLiquidacao).isEqualTo(LocalDate.parse("2024-02-08"))
        assertThat(notaEmprestimo.operacoes).hasSize(2)
        assertThat(notaEmprestimo.operacoes[0]).isEqualToIgnoringGivenProperties(Operacao("BRFS3", "Tomador", BigDecimal("-80.34")))
        assertThat(notaEmprestimo.operacoes[1]).isEqualToIgnoringGivenProperties(Operacao("EMBR3", "Tomador", BigDecimal("-0.20")))
    }
}