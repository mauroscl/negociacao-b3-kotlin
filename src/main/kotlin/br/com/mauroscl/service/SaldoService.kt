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
        var saldo = saldoRepository.obterPorTitulo(negocioRealizado.titulo)
        if (saldo == null) {
            saldo = Saldo(
                negocioRealizado.titulo,
                negocioRealizado.quantidadeComSinal,
                negocioRealizado.valorLiquidacaoUnitario
            )
        } else {

            val fechamentoPosicao = FechamentoPosicaoService.avaliar(data, negocioRealizado, saldo)

            if (fechamentoPosicao == null) {
                saldo.aumentarPosicao(negocioRealizado)
            } else {
                saldo.diminuirPosicao(fechamentoPosicao, negocioRealizado)
                fechamentoPosicaoRepository.persist(fechamentoPosicao)
            }
        }

        saldoRepository.persistOrUpdate(saldo)
    }

}

