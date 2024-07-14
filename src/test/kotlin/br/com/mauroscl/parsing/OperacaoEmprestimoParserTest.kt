package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualToIgnoringGivenProperties
import assertk.assertions.isNotNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class OperacaoEmprestimoParserTest{
    @Test
    fun deveParsearNota(){
        val dataLoader = TestInputDataLoader()
        val conteudo = dataLoader.loadContent("NotaEmprestimo.txt")
        val operacoesEncontradas = OperacaoEmprestimoParser.parse(listOf(conteudo))
        val dataLiquidacaoEsperada = LocalDate.parse("2024-02-08")
        assertThat(operacoesEncontradas).isNotNull()
            .given { operacoes ->
                assertThat(operacoes).hasSize(2)
                assertThat(operacoes[0]).isEqualToIgnoringGivenProperties(
                    OperacaoEmprestimo(
                        "BRFS3", "Tomador", dataLiquidacaoEsperada, BigDecimal("-80.34"), 1100, 1100, 0
                    )
                )
                assertThat(operacoes[1]).isEqualToIgnoringGivenProperties(
                    OperacaoEmprestimo(
                        "EMBR3", "Tomador", dataLiquidacaoEsperada, BigDecimal ("-0.20"), 700, 700, 0
                    )
                )
            }
    }
}