package br.com.mauroscl.parsing

import java.util.*

enum class TipoNegociacao(val codigo: String) {
    COMPRA("C"),
    VENDA("V");

    companion object {
        fun criarPorCodigo(codigo: String?): TipoNegociacao {
            return Arrays.stream(values())
                .filter { value: TipoNegociacao -> value.codigo == codigo }
                .findFirst()
                .orElseThrow { RuntimeException("Não foi possível indentificar o tipo de negocição para o código $codigo") }
        }
    }
}
