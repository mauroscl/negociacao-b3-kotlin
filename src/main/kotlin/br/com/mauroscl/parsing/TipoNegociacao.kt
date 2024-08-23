package br.com.mauroscl.parsing

import br.com.mauroscl.domain.model.Sentido

enum class TipoNegociacao(val codigo: String, val sentido: Sentido, val inverterSinal: Boolean) {
    COMPRA("C", Sentido.LONG, false),
    VENDA("V", Sentido.SHORT, true);
    companion object {
        fun criarPorCodigo(codigo: String): TipoNegociacao {
            return entries
                .firstOrNull { it.codigo == codigo }
                ?: throw RuntimeException("Não foi possível identificar o tipo de negociação para o código $codigo")
        }
    }
}
