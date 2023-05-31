package br.com.mauroscl.service

import br.com.mauroscl.parsing.NotaNegociacao
import java.time.LocalDate

interface IProcessamentoNotaService {
    fun processar(nota: NotaNegociacao)
    fun processar(data: LocalDate)
}