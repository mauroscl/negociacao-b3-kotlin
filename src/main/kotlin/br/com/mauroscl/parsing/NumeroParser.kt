package br.com.mauroscl.parsing

import java.math.BigDecimal

object NumeroParser {
    private val SINAL_MAP = mapOf(Pair( "D", "-"), Pair("C", "+"), Pair("", ""))
    fun parse(numero: String, sinal: String): BigDecimal {
        val numeroEmIngles = NumeroFormatador.converterDePortuguesParaIngles(numero)
        return BigDecimal(SINAL_MAP[sinal] + numeroEmIngles)
    }
}
