package br.com.mauroscl.service

import br.com.mauroscl.parsing.NotaNegociacao

fun interface IImportadorNota {
    fun executar(arquivo: String): NotaNegociacao
}