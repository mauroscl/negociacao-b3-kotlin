package br.com.mauroscl.model

import br.com.mauroscl.parsing.PrazoNegociacao
import java.math.BigDecimal
import java.time.LocalDate

class FechamentoPosicao(
    val data: LocalDate,
    val prazo: PrazoNegociacao,
    val titulo: String,
    val quantidade: Int,
    val precoMedioCompra: BigDecimal,
    val precoMedioVenda: BigDecimal
) {
    private val quantidadeAsBigDecimal = BigDecimal(this.quantidade)
    val resultado: BigDecimal = precoMedioVenda.subtract(precoMedioCompra).multiply(quantidadeAsBigDecimal)
}