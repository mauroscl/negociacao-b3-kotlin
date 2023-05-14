package br.com.mauroscl.service

import br.com.mauroscl.infra.FechamentoPosicaoRepository
import br.com.mauroscl.infra.NotaNegociacaoRepository
import br.com.mauroscl.infra.SaldoRepository
import br.com.mauroscl.model.FechamentoPosicaoService
import br.com.mauroscl.model.Saldo
import br.com.mauroscl.parsing.NotaNegociacao
import br.com.mauroscl.parsing.PrazoNegociacao
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProcessamentoNotaService (
    private val notaNegociacaoRepository: NotaNegociacaoRepository,
    private val fechamentoPosicaoRepository: FechamentoPosicaoRepository,
    private val saldoRepository: SaldoRepository
) : IProcessamentoNotaService {
//    @Transactional
    override fun processar(nota: NotaNegociacao) {
        nota.paginas.forEach { pagina ->

            val dayTrades = pagina.negocios
                .filter { negocio -> negocio.prazo == PrazoNegociacao.DAYTRADE }

            if (dayTrades.isNotEmpty()) {
                val fechamentos = FechamentoPosicaoService.fecharDayTrades(nota.data, dayTrades)
                fechamentoPosicaoRepository.persist(fechamentos)
            }

            pagina.negocios
                .filter { negocio -> negocio.prazo == PrazoNegociacao.POSICAO }
                .forEach { negocio ->
                    val saldoAtual = saldoRepository.obterPorTitulo(negocio.titulo)
                        ?: Saldo.zerado(negocio.titulo)

                    val fechamentoPosicao = FechamentoPosicaoService.avaliar(nota.data, negocio, saldoAtual)

                    SaldoService.atualizarSaldo(saldoAtual, negocio, fechamentoPosicao)

                    fechamentoPosicao?.also { fechamentoPosicaoRepository.persist(it) }

                    saldoRepository.persistOrUpdate(saldoAtual)
                }
        }

        notaNegociacaoRepository.persist(nota)
    }
}