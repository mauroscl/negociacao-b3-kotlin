package br.com.mauroscl.service

import br.com.mauroscl.infra.FechamentoPosicaoRepository
import br.com.mauroscl.infra.SaldoRepository
import br.com.mauroscl.model.FechamentoPosicaoService
import br.com.mauroscl.model.Saldo
import br.com.mauroscl.parsing.NegocioRealizado
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
internal class SaldoService(
    @Inject var saldoRepository: SaldoRepository,
    @Inject var fechamentoPosicaoRepository: FechamentoPosicaoRepository
) : ISaldoService {

    override fun atualizarSaldo(data: LocalDate, negocioRealizado: NegocioRealizado) {
        //não faz sentido ter saldo para day trade.
        val saldo = saldoRepository.obterPorTitulo(negocioRealizado.titulo)
            ?: Saldo.zerado(negocioRealizado.titulo)

        val fechamentoPosicao = FechamentoPosicaoService.avaliar(data, negocioRealizado, saldo)

        if (fechamentoPosicao == null) {
            saldo.aumentarPosicao(negocioRealizado)
        } else {
            saldo.diminuirPosicao(negocioRealizado)
            fechamentoPosicaoRepository.persist(fechamentoPosicao)
        }

        saldoRepository.persistOrUpdate(saldo)
    }

}

