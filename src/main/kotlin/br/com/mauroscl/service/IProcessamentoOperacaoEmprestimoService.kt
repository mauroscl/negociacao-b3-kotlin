package br.com.mauroscl.service

fun interface IProcessamentoOperacaoEmprestimoService {
    fun processar(paginas: Collection<String>)
}