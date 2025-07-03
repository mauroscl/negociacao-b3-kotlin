package br.com.mauroscl.domain.model

import br.com.mauroscl.parsing.PrazoNegociacao
import java.math.BigDecimal
import java.time.LocalDate

class FechamentoPosicao(
    val dataFechamento: LocalDate,
    val dataLiquidacao: LocalDate,
    val prazo: PrazoNegociacao,
    val sentido: Sentido,
    val titulo: String,
    val quantidade: Int,
    val precoMedioCompra: BigDecimal,
    val precoMedioVenda: BigDecimal,
    val custoAluguel: BigDecimal = BigDecimal.ZERO
) {
    private val quantidadeAsBigDecimal = BigDecimal(this.quantidade)
    val resultado: BigDecimal = precoMedioVenda.subtract(precoMedioCompra).multiply(quantidadeAsBigDecimal).add(custoAluguel)
}