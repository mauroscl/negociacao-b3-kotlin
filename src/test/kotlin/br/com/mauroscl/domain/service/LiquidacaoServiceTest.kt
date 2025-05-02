package br.com.mauroscl.domain.service

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import br.com.mauroscl.domain.model.Feriado
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LiquidacaoServiceTest {

    private companion object {
        private val FERIADOS =  listOf(Feriado(LocalDate.of(2025, 4,18), "Paixão de Cristo"),
            Feriado(LocalDate.of(2025, 4,21), "Tiradentes"),
            Feriado(LocalDate.of(2025, 5,1), "Dia do Trabalho"))
    }

    @Test
    fun deveCalcularComFeriados() {
        assertThat(LiquidacaoService.calcularData(LocalDate.of(2025, 4,16), FERIADOS))
            .isEqualByComparingTo(LocalDate.of(2025,4,22))
    }

    @Test
    fun deveCalcularApenasComFinalDeSemana() {
        assertThat(LiquidacaoService.calcularData(LocalDate.of(2025, 4,3), FERIADOS))
            .isEqualByComparingTo(LocalDate.of(2025,4,7))
    }

    @Test
    fun deveCalcularNoMeioDaSemana() {
        assertThat(LiquidacaoService.calcularData(LocalDate.of(2025, 4,28), FERIADOS))
            .isEqualByComparingTo(LocalDate.of(2025,4,30))
    }
}