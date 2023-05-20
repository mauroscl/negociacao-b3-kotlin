package br.com.mauroscl.service

import br.com.mauroscl.parsing.NotaNegociacao

interface IProcessamentoNotaService {
    fun processar(nota: NotaNegociacao)
}