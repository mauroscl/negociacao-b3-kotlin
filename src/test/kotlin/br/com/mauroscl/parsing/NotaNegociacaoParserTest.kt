package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualByComparingTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NotaNegociacaoParserTest {

    private val parser = NotaNegociacaoParser(IdentificadorMercado())

    @Test
    fun `deve obter data da nota`() {
        val dataLoader = TestInputDataLoader()
        val conteudo = dataLoader.loadContent("NegociacaoVista.txt")

        val nota = parser.parse(listOf(conteudo))
        assertThat(nota.data).isEqualByComparingTo((LocalDate.parse("2020-03-02")))
        assertThat(nota.paginas).hasSize(1)
    }
}