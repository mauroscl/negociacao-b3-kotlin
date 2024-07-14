package br.com.mauroscl.infra

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isFalse
import br.com.mauroscl.parsing.OperacaoEmprestimo
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

@QuarkusTest
class OperacaoEmprestimoRepositoryTest{

    @Inject
    private lateinit var repository: OperacaoEmprestimoRepository

    @Test
    fun `deve obter apenas as operacoes nao contabilizadas`() {
        val op1 = OperacaoEmprestimo("PETR4", "Tomador", LocalDate.of(2024, 7, 1), BigDecimal.TEN, 200, 200, 0)
        op1.marcarComoContabilizado();
        val op2 = OperacaoEmprestimo("PETR4", "Tomador", LocalDate.of(2024, 7, 6), BigDecimal(5.50), 300, 300, 0)
        repository.persist(op1, op2)
        val naoContabilizados = repository.obterNaoContabilizados("PETR4")
        assertThat(naoContabilizados).hasSize(1)
        assertThat(naoContabilizados[0].contabilizado).isFalse()
        repository.deleteAll();
    }
}