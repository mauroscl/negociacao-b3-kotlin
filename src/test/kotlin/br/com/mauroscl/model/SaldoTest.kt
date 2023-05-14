package br.com.mauroscl.model

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import assertk.assertions.isZero
import br.com.mauroscl.parsing.NegocioRealizado
import br.com.mauroscl.parsing.PrazoNegociacao
import br.com.mauroscl.parsing.TipoNegociacao
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class SaldoTest {

    @Test
    fun `deve aumentar posicao`() {
        val saldo = Saldo("PETROBRAS", 100, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalUnitario("PETROBRAS", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 100, BigDecimal(40))
        negocioRealizado.adicionarCustos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        saldo.aumentarPosicao(negocioRealizado)

        assertThat(saldo.quantidade).isEqualTo(200)
        assertThat(saldo.precoMedio).isEqualByComparingTo("45")
        assertThat(saldo.valorTotal).isEqualByComparingTo("9000")
    }

    @Test
    fun `deve iniciar posicao`() {
        val saldo = Saldo.zerado("PETROBRAS")
        val negocioRealizado = NegocioRealizado.comValorOperacionalTotal("PETROBRAS", TipoNegociacao.COMPRA, PrazoNegociacao.POSICAO, 4300, BigDecimal(24768))
        negocioRealizado.adicionarCustos(BigDecimal("7.43"), BigDecimal.ZERO, BigDecimal.ZERO)

        saldo.aumentarPosicao(negocioRealizado)

        assertThat(saldo.quantidade).isEqualTo(4300)
        assertThat(saldo.precoMedio).isEqualByComparingTo("5.761727907")
        assertThat(saldo.valorTotal).isEqualByComparingTo("24775.43")
    }


    @Test
    fun `deve diminuir posicao`() {
        val saldo = Saldo("PETROBRAS", 200, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalUnitario("PETROBRAS", TipoNegociacao.VENDA, PrazoNegociacao.POSICAO, 100, BigDecimal(40))
        negocioRealizado.adicionarCustos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        saldo.diminuirPosicao(negocioRealizado)

        assertThat(saldo.quantidade).isEqualTo(100)
        assertThat(saldo.precoMedio).isEqualByComparingTo("50")
        assertThat(saldo.valorTotal).isEqualByComparingTo("5000")
    }
    @Test
    fun `deve zerar posicao`() {
        val saldo = Saldo("PETROBRAS", 200, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalUnitario("PETROBRAS", TipoNegociacao.VENDA, PrazoNegociacao.POSICAO, 200, BigDecimal(40))
        negocioRealizado.adicionarCustos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        saldo.diminuirPosicao(negocioRealizado)

        assertThat(saldo.quantidade).isZero()
        assertThat(saldo.precoMedio).isZero()
        assertThat(saldo.valorTotal).isZero()
    }

    @Test
    fun `deve inverter posicao`() {
        val saldo = Saldo("PETROBRAS", 200, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalUnitario("PETROBRAS", TipoNegociacao.VENDA, PrazoNegociacao.POSICAO, 300, BigDecimal(40))
        negocioRealizado.adicionarCustos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        saldo.diminuirPosicao(negocioRealizado)

        assertThat(saldo.quantidade).isEqualTo(-100)
        assertThat(saldo.precoMedio).isEqualByComparingTo("40")
        assertThat(saldo.valorTotal).isEqualByComparingTo("-4000")
    }

}