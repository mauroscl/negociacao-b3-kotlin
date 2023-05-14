package br.com.mauroscl.service

import br.com.mauroscl.infra.FechamentoPosicaoRepository
import br.com.mauroscl.infra.NotaNegociacaoRepository
import br.com.mauroscl.model.FechamentoPosicaoService
import br.com.mauroscl.parsing.NotaNegociacao
import br.com.mauroscl.parsing.PrazoNegociacao
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class ProcessamentoNotaService(
    @Inject var saldoService: ISaldoService,
    @Inject var notaNegociacaoRepository: NotaNegociacaoRepository,
    @Inject var fechamentoPosicaoRepository: FechamentoPosicaoRepository
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
                    saldoService.atualizarSaldo(nota.data, negocio)
                }
        }

        notaNegociacaoRepository.persist(nota)
    }
}