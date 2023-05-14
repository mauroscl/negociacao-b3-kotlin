package br.com.mauroscl.service

import br.com.mauroscl.parsing.NotaNegociacao
import javax.transaction.Transactional

interface IProcessamentoNotaService {
    @Transactional
    fun processar(nota: NotaNegociacao)
}