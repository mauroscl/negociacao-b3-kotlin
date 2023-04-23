package br.com.mauroscl.parsing

import br.com.mauroscl.parsing.ExpressoesRegulares.NUMERO_INTEIRO_REGEX
import java.math.BigDecimal
import java.util.*

class NegocioMercadoVistaParser {
    fun parsearLinha(negociacao: String): NegocioRealizado {
        val subStringAteTitulo = obterPrimeiraParte(negociacao)
        val subStringComecandoObservacao =
            negociacao.substring(subStringAteTitulo.length).trim { it <= ' ' }
        val primeiraParteMap = separarPrimeiraParte(subStringAteTitulo)
        val segundaParteMap = separarSegundaParte(subStringComecandoObservacao)
        return NegocioRealizado.comValorOperacionalTotal(
            primeiraParteMap["TITULO"]!!,
            TipoNegociacao.criarPorCodigo(primeiraParteMap["TIPONEGOCIACAO"]),
            PrazoNegociacao.valueOf(segundaParteMap["PRAZO"]!!),
            Integer.valueOf(segundaParteMap["QUANTIDADE"]),
            BigDecimal(segundaParteMap["VALORTOTAL"])
        )
    }

    private fun obterPrimeiraParte(linha: String): String {
        /*
        * Esta expressão regular delimita a string da observação em diante.
        * Podemos ter ou não um espaço seguido do caracter de observação. Por isso essa parte (\s.)?
        * Depois disso temos um espaço e a quantidade, que pode ter ou não separador de milhares
        * O \s\d que vem após o NUMERO_INTEIRO_REGEX é para garantia que o que vem depois é um espaço e o Preço / Ajuste.
        * Esta expressão adicional evitar erros de parse em titulos que iniciam com um número como 3R PETROLEUM
        * */
        return linha.split("(\\s.)?\\s$NUMERO_INTEIRO_REGEX\\s\\d".toRegex(), limit = 2)
            .toTypedArray()[0]
    }

    private fun separarPrimeiraParte(primeiraMetade: String): Map<String, String> {
        val retorno = HashMap<String, String>()
        retorno["TIPONEGOCIACAO"] = primeiraMetade.substring(10, 11)
        retorno["TITULO"] = primeiraMetade.substring(18).trim { it <= ' ' }
            .replace("\\s{2,}".toRegex(), " ")
        return retorno
    }

    private fun separarSegundaParte(segundaMetade: String): Map<String, String?> {
        var segundaMetadaSeparadaEmColunas =
            segundaMetade.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        if (segundaMetadaSeparadaEmColunas.size < 4 || segundaMetadaSeparadaEmColunas.size > 5) {
            throw FormatoInformacaoDesconhecidoException("Quantidade de informações inválidas")
        }
        var prazo = "POSICAO"
        if (segundaMetadaSeparadaEmColunas.size == 5) {
            if (DAYTRADE_CODIGO == segundaMetadaSeparadaEmColunas[0]) {
                prazo = "DAYTRADE"
            }
            segundaMetadaSeparadaEmColunas =
                Arrays.copyOfRange(segundaMetadaSeparadaEmColunas, 1, 4)
        }
        val retorno = HashMap<String, String?>()
        retorno["PRAZO"] = prazo
        retorno["QUANTIDADE"] = NumeroFormatador.converterDePortuguesParaIngles(
            segundaMetadaSeparadaEmColunas[0]
        )
        retorno["VALORUNITARIO"] = NumeroFormatador.converterDePortuguesParaIngles(
            segundaMetadaSeparadaEmColunas[1]
        )
        retorno["VALORTOTAL"] = NumeroFormatador.converterDePortuguesParaIngles(
            segundaMetadaSeparadaEmColunas[2]
        )
        return retorno
    }

    companion object {
        private const val DAYTRADE_CODIGO = "D"
    }
}
