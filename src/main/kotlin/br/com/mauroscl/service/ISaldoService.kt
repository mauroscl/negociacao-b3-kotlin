package br.com.mauroscl.service

import br.com.mauroscl.parsing.NegocioRealizado
import java.time.LocalDate

fun interface ISaldoService {
    fun atualizarSaldo(data: LocalDate, negocioRealizado: NegocioRealizado)
}