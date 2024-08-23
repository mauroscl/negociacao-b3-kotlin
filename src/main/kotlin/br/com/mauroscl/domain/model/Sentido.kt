package br.com.mauroscl.domain.model

enum class Sentido {
    LONG,
    SHORT;

    companion object {
        fun obterPorSaldo(saldo: Int): Sentido {
            return when {
                saldo == 0 -> throw Exception("Saldo zerado. Não é possível determinar o sinal")
                saldo < 0 -> SHORT
                else -> LONG
            }
        }
    }
}
