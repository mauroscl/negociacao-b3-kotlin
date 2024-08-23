package br.com.mauroscl.domain.service

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LiquidacaoServiceTest {
    @Test
    fun deveCalcularQuandoPrimeiroDiaUtil() {
        assertThat(LiquidacaoService.calcularData(LocalDate.of(2024, 7,16)))
            .isEqualByComparingTo(LocalDate.of(2024,7,19))
    }

    @Test
    fun deveCalcularQuandoPrimeiroDiaNaoUtil() {
        assertThat(LiquidacaoService.calcularData(LocalDate.of(2024, 7,18)))
            .isEqualByComparingTo(LocalDate.of(2024,7,23))
    }

}