package br.com.mauroscl.domain.service

import br.com.mauroscl.domain.model.FechamentoPosicao
import br.com.mauroscl.domain.model.PrecoMedio
import br.com.mauroscl.domain.model.Saldo
import br.com.mauroscl.domain.model.Sentido
import br.com.mauroscl.infra.AtivoRepository
import br.com.mauroscl.infra.OperacaoEmprestimoRepository
import br.com.mauroscl.parsing.Mercado
import br.com.mauroscl.parsing.NegocioRealizado
import br.com.mauroscl.parsing.PrazoNegociacao
import br.com.mauroscl.parsing.TipoNegociacao
import jakarta.enterprise.context.ApplicationScoped
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.math.absoluteValue
import kotlin.math.sign

@ApplicationScoped
class FechamentoPosicaoService(
    private val operacaoEmprestimoRepository: OperacaoEmprestimoRepository,
    private val ativoRepository: AtivoRepository
) {
    fun avaliar(data: LocalDate, negocio: NegocioRealizado, saldoAtual: Saldo, mercado: Mercado): FechamentoPosicao? {
        return if (gerarFechamentoPosicao(saldoAtual.quantidade, negocio.quantidadeComSinal)) {
            val quantidadeParaFechar = minOf(saldoAtual.quantidade.absoluteValue, negocio.quantidade)
            val precoMedio = obterPrecoMedio(negocio, saldoAtual)
            val sentido = Sentido.obterPorSaldo(saldoAtual.quantidade)
            var custoAluguel = BigDecimal.ZERO
            if (sentido == Sentido.SHORT && mercado == Mercado.AVISTA) {
                val ativo = ativoRepository.obterPorNome(saldoAtual.titulo)
                    ?: throw RuntimeException("Ativo não encontrado: ${saldoAtual.titulo}")
                val naoContabilizadas = operacaoEmprestimoRepository.obterNaoContabilizados(ativo.codigo)
                if (naoContabilizadas.isEmpty()) {
                    throw RuntimeException("Aluguel não encontrado para a posição de venda em ${saldoAtual.titulo}")
                }
                if (naoContabilizadas.any { it.quantidadeLiquidacao != quantidadeParaFechar }) {
                    throw RuntimeException("Quantidade para fechar e quantidade de aluguel divergentes")
                }
                custoAluguel = naoContabilizadas.sumOf { it.valorLiquido }
                naoContabilizadas.forEach {
                    it.marcarComoContabilizado()
                    operacaoEmprestimoRepository.persistOrUpdate(it)
                }
            }
            FechamentoPosicao(
                data,
                negocio.prazo,
                sentido,
                saldoAtual.titulo,
                quantidadeParaFechar,
                precoMedio.compra,
                precoMedio.venda,
                custoAluguel
            )
        } else null
    }

    fun fecharDayTrades(data: LocalDate, dayTrades: List<NegocioRealizado>): List<FechamentoPosicao> {
        return dayTrades
            .groupBy { negocio -> DayTradeChave(negocio.titulo, negocio.quantidade) }
            .map { entry ->
                val precoMedio = obterPrecoMedio(entry.value)
                val sentido = entry.value.first().tipo.sentido
                FechamentoPosicao(
                    data,
                    PrazoNegociacao.DAYTRADE,
                    sentido,
                    entry.key.titulo,
                    entry.key.quantidade,
                    precoMedio.compra,
                    precoMedio.venda
                )
            }
    }

    private fun gerarFechamentoPosicao(quantidadeAtual: Int, quantidadeNegocio: Int): Boolean {
        return quantidadeAtual.sign * quantidadeNegocio.sign < 0
    }

    private fun obterPrecoMedio(negocio: NegocioRealizado, saldoAtual: Saldo): PrecoMedio {
        return if (negocio.tipo == TipoNegociacao.COMPRA) {
            PrecoMedio(compra = negocio.valorLiquidacaoUnitario, venda = saldoAtual.precoMedio)
        } else {
            PrecoMedio(compra = saldoAtual.precoMedio, venda = negocio.valorLiquidacaoUnitario)
        }
    }

    private fun obterPrecoMedio(negocios: List<NegocioRealizado>): PrecoMedio {
        val compra = negocios.first { it.tipo == TipoNegociacao.COMPRA }
        val venda = negocios.first { it.tipo == TipoNegociacao.VENDA }
        return PrecoMedio(compra.valorLiquidacaoUnitario, venda.valorLiquidacaoUnitario)
    }

    data class DayTradeChave(val titulo: String, val quantidade: Int)


}