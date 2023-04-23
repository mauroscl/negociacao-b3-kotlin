package br.com.mauroscl.parsing

object NumeroFormatador {
    fun converterDePortuguesParaIngles(numeroEmPortugues: String): String {
        return numeroEmPortugues
            .replace(".", "")
            .replace(",", ".")
    }
}
