package br.com.mauroscl.domain.service

import assertk.assertThat
import assertk.assertions.*
import br.com.mauroscl.domain.model.Ativo
import br.com.mauroscl.domain.model.Saldo
import br.com.mauroscl.domain.model.Sentido
import br.com.mauroscl.infra.AtivoRepository
import br.com.mauroscl.infra.OperacaoEmprestimoRepository
import br.com.mauroscl.parsing.*
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

@QuarkusTest
class FechamentoPosicaoServiceTest {

    @Inject
    private lateinit var operacaoEmprestimoRepository: OperacaoEmprestimoRepository

    @Inject
    private lateinit var ativoRepository: AtivoRepository

    @Inject
    private lateinit var fechamentoPosicaoService: FechamentoPosicaoService

    @AfterEach
    fun limparBanco(){
        ativoRepository.deleteAll()
        operacaoEmprestimoRepository.deleteAll();
    }

    @Test
    fun `nao deve fechar posicao quando saldo estiver zerado`() {
        val saldo = Saldo("PETROBRAS", 0, BigDecimal.ZERO)
        val negocioRealizado = NegocioRealizado.comValorOperacionalTotal(
            "PETROBRAS",
            TipoNegociacao.COMPRA,
            PrazoNegociacao.POSICAO,
            1,
            BigDecimal(50)
        )

        val fechamentoPosicao =
            fechamentoPosicaoService.avaliar(LocalDate.now(), negocioRealizado, saldo, Mercado.AVISTA)

        assertThat(fechamentoPosicao).isNull()
    }

    @Test
    fun `nao deve fechar posicao quando saldo for positivo e operacao for compra`() {
        val saldo = Saldo("PETROBRAS", 100, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalTotal(
            "PETROBRAS",
            TipoNegociacao.COMPRA,
            PrazoNegociacao.POSICAO,
            100,
            BigDecimal(50)
        )

        val fechamentoPosicao =
            fechamentoPosicaoService.avaliar(LocalDate.now(), negocioRealizado, saldo, Mercado.AVISTA)

        assertThat(fechamentoPosicao).isNull()
    }

    @Test
    fun `nao deve fechar posicao quando saldo for negativo e operacao for venda`() {
        val saldo = Saldo("PETROBRAS", -100, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalTotal(
            "PETROBRAS",
            TipoNegociacao.VENDA,
            PrazoNegociacao.POSICAO,
            100,
            BigDecimal(50)
        )

        val fechamentoPosicao =
            fechamentoPosicaoService.avaliar(LocalDate.now(), negocioRealizado, saldo, Mercado.AVISTA)

        assertThat(fechamentoPosicao).isNull()
    }

    @Test
    fun `deve fechar posicao parcial`() {
        val saldo = Saldo("PETROBRAS", 200, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalUnitario(
            "PETROBRAS",
            TipoNegociacao.VENDA,
            PrazoNegociacao.POSICAO,
            100,
            BigDecimal(60)
        )
        negocioRealizado.adicionarCustos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        assertThat(fechamentoPosicaoService.avaliar(LocalDate.of(2024, 6, 15), negocioRealizado, saldo, Mercado.AVISTA))
            .isNotNull()
            .given {
                assertThat(it.quantidade).isEqualTo(100)
                assertThat(it.precoMedioCompra).isEqualByComparingTo("50")
                assertThat(it.precoMedioVenda).isEqualByComparingTo("60")
                assertThat(it.resultado).isEqualByComparingTo("1000")
                assertThat(it.sentido).isEqualTo(Sentido.LONG)
            }
    }

    @Test
    fun `deve fechar posicao quando quantidade maior que saldo`() {
        val saldo = Saldo("PETROBRAS", 200, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalUnitario(
            "PETROBRAS",
            TipoNegociacao.VENDA,
            PrazoNegociacao.POSICAO,
            300,
            BigDecimal(60)
        )
        negocioRealizado.adicionarCustos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        val fechamentoPosicao =
            fechamentoPosicaoService.avaliar(LocalDate.now(), negocioRealizado, saldo, Mercado.AVISTA)

        assertThat(fechamentoPosicao).isNotNull()
            .given {
                assertThat(it.quantidade).isEqualTo(200)
                assertThat(it.precoMedioCompra).isEqualByComparingTo("50")
                assertThat(it.precoMedioVenda).isEqualByComparingTo("60")
                assertThat(it.resultado).isEqualByComparingTo("2000")
            }
    }

    @Test
    fun `deve fechar posicao de prejuizo`() {
        val saldo = Saldo("PETROBRAS", 100, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalUnitario(
            "PETROBRAS",
            TipoNegociacao.VENDA,
            PrazoNegociacao.POSICAO,
            100,
            BigDecimal(40)
        )
        negocioRealizado.adicionarCustos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        val fechamentoPosicao =
            fechamentoPosicaoService.avaliar(LocalDate.now(), negocioRealizado, saldo, Mercado.AVISTA)

        assertThat(fechamentoPosicao).isNotNull()
            .given {
                assertThat(it.quantidade).isEqualTo(100)
                assertThat(it.precoMedioCompra).isEqualByComparingTo("50")
                assertThat(it.precoMedioVenda).isEqualByComparingTo("40")
                assertThat(it.resultado).isEqualByComparingTo("-1000")
            }
    }

    @Test
    fun `deve fechar posicao de venda`() {
        ativoRepository.persist(Ativo("PETR4", "PETROBRAS"))
        operacaoEmprestimoRepository.persist(
            OperacaoEmprestimo("PETR4", "Tomador", LocalDate.of(2024, 7, 1), BigDecimal(-30), 100, 100, 0),
            OperacaoEmprestimo("PETR4", "Tomador", LocalDate.of(2024, 7, 3), BigDecimal(-15), 100, 100, 0)
        )
        val saldo = Saldo("PETROBRAS", -100, BigDecimal(50))
        val negocioRealizado = NegocioRealizado.comValorOperacionalUnitario(
            "PETROBRAS",
            TipoNegociacao.COMPRA,
            PrazoNegociacao.POSICAO,
            100,
            BigDecimal(40)
        )
        negocioRealizado.adicionarCustos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)

        val fechamentoPosicao =
            fechamentoPosicaoService.avaliar(LocalDate.of(2024, 7, 1), negocioRealizado, saldo, Mercado.AVISTA)

        assertThat(fechamentoPosicao).isNotNull()
            .given {
                assertThat(it.quantidade).isEqualTo(100)
                assertThat(it.precoMedioCompra).isEqualByComparingTo("40")
                assertThat(it.precoMedioVenda).isEqualByComparingTo("50")
                assertThat(it.resultado).isEqualByComparingTo("955")
                assertThat(it.custoAluguel).isEqualByComparingTo("-45")
                assertThat(it.sentido).isEqualTo(Sentido.SHORT)
            }
        assertThat(operacaoEmprestimoRepository.findAll().list()).each { it.given { operacao -> assertThat(operacao.contabilizado).isTrue() } }

    }


    @Test
    fun deveGerarFechamentoDayTrades() {
        val compra = NegocioRealizado.comValorOperacionalUnitario(
            "WIN J22",
            TipoNegociacao.COMPRA,
            PrazoNegociacao.DAYTRADE,
            6,
            BigDecimal(121645)
        )
        compra.adicionarCustos(BigDecimal(1.5), BigDecimal.ZERO, BigDecimal.ZERO)

        val venda = NegocioRealizado.comValorOperacionalUnitario(
            "WIN J22",
            TipoNegociacao.VENDA,
            PrazoNegociacao.DAYTRADE,
            6,
            BigDecimal(121390)
        )
        venda.adicionarCustos(BigDecimal(1.5), BigDecimal.ZERO, BigDecimal.ZERO)

        val negocios = listOf(compra, venda)

        val posicoesFechadas = fechamentoPosicaoService.fecharDayTrades(LocalDate.now(), negocios)

        assertThat(posicoesFechadas).hasSize(1)

        val fechamentoAtual = posicoesFechadas[0]
        assertThat(fechamentoAtual.data).isEqualTo(LocalDate.now())
        assertThat(fechamentoAtual.titulo).isEqualTo("WIN J22")
        assertThat(fechamentoAtual.prazo).isEqualTo(PrazoNegociacao.DAYTRADE)
        assertThat(fechamentoAtual.quantidade).isEqualTo(6)
        assertThat(fechamentoAtual.precoMedioCompra).isEqualByComparingTo("24329.25")
        assertThat(fechamentoAtual.precoMedioVenda).isEqualByComparingTo("24277.75")
        assertThat(fechamentoAtual.resultado).isEqualByComparingTo("-309")
        assertThat(fechamentoAtual.sentido).isEqualTo(Sentido.LONG)
    }

}