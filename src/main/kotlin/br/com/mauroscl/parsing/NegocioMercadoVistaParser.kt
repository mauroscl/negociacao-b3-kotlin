package br.com.mauroscl.parsing

import br.com.mauroscl.parsing.ExpressoesRegulares.NUMERO_INTEIRO_REGEX
import java.math.BigDecimal
import java.util.*
import java.util.regex.Pattern

class NegocioMercadoVistaParser {
    fun parsearLinha(negociacao: String): NegocioRealizado {
        val subStringAteTitulo = obterPrimeiraParte(negociacao)
        val subStringComecandoObservacao =
            negociacao.substring(subStringAteTitulo.length).trim { it <= ' ' }
        val primeiraParteMap = separarPrimeiraParte(subStringAteTitulo)
        val segundaParteMap = separarSegundaParte(subStringComecandoObservacao)
        return NegocioRealizado.comValorOperacionalTotal(
            primeiraParteMap["TITULO"]!!,
            TipoNegociacao.criarPorCodigo(primeiraParteMap["TIPONEGOCIACAO"]!!),
            PrazoNegociacao.valueOf(segundaParteMap["PRAZO"]!!),
            Integer.valueOf(segundaParteMap["QUANTIDADE"]),
            BigDecimal(segundaParteMap["VALORTOTAL"])
        )
    }

    private fun obterPrimeiraParte(linha: String): String {
        /*
        * Esta expressão regular delimita a string da observação em diante.
        * Podemos ter ou não um espaço seguido do caracter de observação. Por isso essa parte \s(D#?|#\d?))?
        * As observações podem ser um "D" de daytrade ou D#. Em outros cenários é apenas "#" ou "#" e um numeral como "#2".
        * Para resolver isso coloquei que depois do espaço temos um D com um # opcional OU um # com um númeral (\d) opcional
        * Depois disso temos um espaço e a quantidade, que pode ter ou não separador de milhares
        * O \s\d que vem após o NUMERO_INTEIRO_REGEX é para garantia que o que vem depois é um espaço e o Preço / Ajuste.
        * Esta expressão adicional evitar erros de parse nos títulos que iniciam com um número como 3R PETROLEUM
        * */
        return linha.split("(\\s(D#?|#\\d?))?\\s$NUMERO_INTEIRO_REGEX\\s\\d".toRegex(), limit = 2)
            .toTypedArray()[0]
    }

    private fun separarPrimeiraParte(primeiraMetade: String): Map<String, String> {
        val matcher = PRIMEIRA_PARTE_PATTERN.matcher(primeiraMetade)
        matcher.find()
        val retorno = HashMap<String, String>()
        retorno["TIPONEGOCIACAO"] = matcher.group(1)
        retorno["TITULO"] = matcher.group(2).trim { it <= ' ' }.replace("\\s{2,}".toRegex(), " ")
        return retorno
    }

    private fun separarSegundaParte(segundaMetade: String): Map<String, String?> {
        var segundaMetadeSeparadaEmColunas =
            segundaMetade.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        if (segundaMetadeSeparadaEmColunas.size < 4 || segundaMetadeSeparadaEmColunas.size > 5) {
            throw FormatoInformacaoDesconhecidoException("Quantidade de informações inválidas")
        }
        var prazo = "POSICAO"
        if (segundaMetadeSeparadaEmColunas.size == 5) {
            if (segundaMetadeSeparadaEmColunas[0].startsWith(DAYTRADE_CODIGO)) {
                prazo = "DAYTRADE"
            }
            segundaMetadeSeparadaEmColunas = segundaMetadeSeparadaEmColunas.copyOfRange(1, 4)
        }
        val retorno = HashMap<String, String?>()
        retorno["PRAZO"] = prazo
        retorno["QUANTIDADE"] = NumeroFormatador.converterDePortuguesParaIngles(
            segundaMetadeSeparadaEmColunas[0]
        )
        retorno["VALORUNITARIO"] = NumeroFormatador.converterDePortuguesParaIngles(
            segundaMetadeSeparadaEmColunas[1]
        )
        retorno["VALORTOTAL"] = NumeroFormatador.converterDePortuguesParaIngles(
            segundaMetadeSeparadaEmColunas[2]
        )
        return retorno
    }

    companion object {
        private const val DAYTRADE_CODIGO = "D"
        private const val PRIMEIRA_PARTE_REGEX = "\\d-\\w+\\s(\\w)\\s\\w+\\s(.+)"
        private val PRIMEIRA_PARTE_PATTERN = Pattern.compile(PRIMEIRA_PARTE_REGEX)
    }
}
