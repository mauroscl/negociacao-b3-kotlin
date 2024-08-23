package br.com.mauroscl.parsing

object ExpressoesRegulares {
    const val NUMERO_DECIMAL_REGEX = "\\d{1,3}(\\.\\d{3})*,\\d+"
    const val NUMERO_INTEIRO_REGEX = "\\d{1,3}(\\.?\\d{3})*" //separador de milhares é opcional
}
