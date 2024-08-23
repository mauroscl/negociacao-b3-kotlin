package br.com.mauroscl.parsing

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

internal class NegocioMercadoFuturoParserTest {
    @Test
    fun deveParsearNegociacaoNormal() {
        val parser = NegocioMercadoFuturoParser()
        val possivelNegocioRealizado =
            parser.parse("C WDO H20 02/03/2020 1 4.498,7000 NORMAL 1.100,00 C 1,00")

        assertThat(possivelNegocioRealizado)
            .isNotNull()
            .given { negocioRealizado ->
                assertThat(negocioRealizado.tipo).isEqualTo(TipoNegociacao.COMPRA)
                assertThat(negocioRealizado.titulo).isEqualTo("WDOH20")
                assertThat(negocioRealizado.quantidade).isEqualTo(1)
                assertThat(negocioRealizado.prazo).isEqualTo(PrazoNegociacao.POSICAO)
                assertThat(negocioRealizado.valorOperacional).isEqualByComparingTo("4498.7")
                assertThat(negocioRealizado.taxaOperacional).isEqualByComparingTo("1")
            }
    }

    @Test
    fun registroDeAjustoDePosicaoDeveSerIgnorado() {
        val parser = NegocioMercadoFuturoParser()
        val negocioRealizado = parser.parse("C WDO H20 02/03/2020 1 4.498,7000 AJUPOS 0,00 C 0,00")
        assertThat(negocioRealizado).isNull()
    }

    @Test
    fun deveReconhecerAjusteDePosicaoComQuantidadeNegativa() {
        val parser = NegocioMercadoFuturoParser()
        val negocioRealizado = parser.parse("V WDO H23 01/03/2023 -2 5.218,01 AJUPOS 301,42 D 0,00")
        assertThat(negocioRealizado).isNull()
    }


    @Test
    fun deveGerarErroAoParsearNegociacaoComQuantidadeCampoCamposInvalida() {
        val parser = NegocioMercadoFuturoParser()
        assertFailure { parser.parse("CAMPO_ADICIONAL C WDO H20 02/03/2020 1 4.498,7000 AJUPOS 0,00 C 0,00") }
            .isInstanceOf(FormatoInformacaoDesconhecidoException::class)
            .message().isEqualTo("Não foi possível detectar o tipo de negociação de mercado futuro.")
    }
}