package br.com.mauroscl.service

import br.com.mauroscl.model.FechamentoPosicao
import br.com.mauroscl.model.Saldo
import br.com.mauroscl.parsing.NegocioRealizado

internal class SaldoService {

    companion object {

        fun atualizarSaldo(
            saldoAtual: Saldo,
            negocioRealizado: NegocioRealizado,
            fechamentoPosicao: FechamentoPosicao?
        ) {
            if (fechamentoPosicao == null) {
                saldoAtual.aumentarPosicao(negocioRealizado)
            } else {
                saldoAtual.diminuirPosicao(negocioRealizado)
            }
        }
    }

}

