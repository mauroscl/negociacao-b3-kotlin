package br.com.mauroscl.parsing

data class NegociacaoChave(
    val titulo: String,
    val tipo: TipoNegociacao,
    val prazo: PrazoNegociacao
)
