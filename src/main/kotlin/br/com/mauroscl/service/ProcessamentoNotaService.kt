package br.com.mauroscl.service

import br.com.mauroscl.infra.FechamentoPosicaoRepository
import br.com.mauroscl.infra.NotaNegociacaoRepository
import br.com.mauroscl.infra.SaldoRepository
import br.com.mauroscl.domain.service.FechamentoPosicaoService
import br.com.mauroscl.domain.model.Saldo
import br.com.mauroscl.parsing.NotaNegociacao
import br.com.mauroscl.parsing.PrazoNegociacao
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDate

@ApplicationScoped
class ProcessamentoNotaService (
    private val notaNegociacaoRepository: NotaNegociacaoRepository,
    private val fechamentoPosicaoRepository: FechamentoPosicaoRepository,
    private val saldoRepository: SaldoRepository
) : IProcessamentoNotaService {
    override fun processar(nota: NotaNegociacao) = processarNota(nota)

    @Transactional
    override fun processar(data: LocalDate) {
        this.notaNegociacaoRepository.findByData(data)
            ?.also { processarNota(it) }
            ?: throw NoSuchElementException("Nenhuma nota de negociação encontrada na data $data")
    }

    private fun processarNota(nota: NotaNegociacao) {
        nota.paginas.forEach { pagina ->

            val dayTrades = pagina.obterNegocios(PrazoNegociacao.DAYTRADE)

            if (dayTrades.isNotEmpty()) {
                val fechamentos = FechamentoPosicaoService.fecharDayTrades(nota.data, dayTrades)
                fechamentoPosicaoRepository.persist(fechamentos)
            }

            pagina.obterNegocios(PrazoNegociacao.POSICAO)
                .forEach { negocio ->
                    val saldoAtual = saldoRepository.obterPorTitulo(negocio.titulo)
                        ?: Saldo.zerado(negocio.titulo)

                    val fechamentoPosicao = FechamentoPosicaoService.avaliar(nota.data, negocio, saldoAtual, pagina.mercado)

                    SaldoService.atualizarSaldo(saldoAtual, negocio, fechamentoPosicao)

                    fechamentoPosicao?.also { fechamentoPosicaoRepository.persist(it) }

                    saldoRepository.persistOrUpdate(saldoAtual)
                }
        }
    }
}