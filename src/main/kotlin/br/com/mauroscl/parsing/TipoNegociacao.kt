package br.com.mauroscl.parsing

enum class TipoNegociacao(val codigo: String) {
    COMPRA("C"),
    VENDA("V");

    companion object {
        fun criarPorCodigo(codigo: String): TipoNegociacao {
            return values()
                .firstOrNull { it.codigo == codigo }
                ?: throw RuntimeException("Não foi possível indentificar o tipo de negocição para o código $codigo")
        }
    }
}
