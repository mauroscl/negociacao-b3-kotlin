package br.com.mauroscl.service

import br.com.mauroscl.parsing.NotaNegociacao

interface IImportadorNota {
    fun executar(arquivo: String): NotaNegociacao
}