package br.com.mauroscl.model

import br.com.mauroscl.parsing.NegocioRealizado
import br.com.mauroscl.parsing.PrazoNegociacao
import br.com.mauroscl.parsing.TipoNegociacao
import java.time.LocalDate
import kotlin.math.absoluteValue
import kotlin.math.sign

class FechamentoPosicaoService {
    companion object {
        fun avaliar(data: LocalDate,  negocio: NegocioRealizado, saldoAtual: Saldo): FechamentoPosicao? {
            return if (gerarFechamentoPosicao(saldoAtual.quantidade, negocio.quantidadeComSinal)) {
                val quantidadeParaFechar = minOf(saldoAtual.quantidade.absoluteValue, negocio.quantidade)
                val precoMedio = obterPrecoMedio(negocio, saldoAtual)
                FechamentoPosicao(
                    data,
                    negocio.prazo,
                    saldoAtual.titulo,
                    quantidadeParaFechar,
                    precoMedio.compra,
                    precoMedio.venda
                )
            } else null
        }

        fun fecharDayTrades(data: LocalDate, dayTrades: List<NegocioRealizado>): List<FechamentoPosicao> {
            return dayTrades
                .groupBy { negocio -> DayTradeChave(negocio.titulo, negocio.quantidade ) }
                .map { entry ->
                    val precoMedio = obterPrecoMedio(entry.value)
                    FechamentoPosicao(data, PrazoNegociacao.DAYTRADE, entry.key.titulo , entry.key.quantidade, precoMedio.compra, precoMedio.venda )
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
}