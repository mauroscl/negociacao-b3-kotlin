package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NegocioMercadoVistaParserTest {

    private val parser = NegocioMercadoVistaParser()

    @Test
    fun deveParsearLinhaSemObservacao() {
        val negocioRealizado =
            parser.parsearLinha("1-BOVESPA C VISTA CPFL ENERGIA          ON NM 100 36,82 3.682,00 D")
        assertThat(negocioRealizado.tipo).isEqualTo(TipoNegociacao.COMPRA)
        assertThat (negocioRealizado.prazo).isEqualTo(PrazoNegociacao.POSICAO)
        assertThat (negocioRealizado.titulo).isEqualTo("CPFL ENERGIA ON NM")
        assertThat (negocioRealizado.quantidade).isEqualTo(100)
        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("3682")
    }

    @Test
    fun deveParsearLinhaComObservacaoDiferenteDayTrade() {
        val negocioRealizado =
            parser.parsearLinha("1-BOVESPA C VISTA LOJAS RENNER          ON NM # 100 53,74 5.374,00 D")
        assertThat (negocioRealizado.tipo).isEqualTo(TipoNegociacao.COMPRA)
        assertThat (negocioRealizado.prazo).isEqualTo(PrazoNegociacao.POSICAO)
        assertThat (negocioRealizado.titulo).isEqualTo("LOJAS RENNER ON NM")
        assertThat (negocioRealizado.quantidade).isEqualTo(100)
        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("5374")
    }

    @Test
    fun deveParsearLinhaComObservacaoSustenidoNumeral() {
        val negocioRealizado =
            parser.parsearLinha("1-BOVESPA C VISTA REDE D OR          ON NM #2 100 46,32 4.632,00 D")
        assertThat (negocioRealizado.tipo).isEqualTo(TipoNegociacao.COMPRA)
        assertThat (negocioRealizado.prazo).isEqualTo(PrazoNegociacao.POSICAO)
        assertThat (negocioRealizado.titulo).isEqualTo("REDE D OR ON NM")
        assertThat (negocioRealizado.quantidade).isEqualTo(100)
        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("4632")
    }


    @Test
    fun deveParsearLinhaComObservacaoDayTrade() {
        val negocioRealizado =
            parser.parsearLinha("1-BOVESPA V VISTA BANCO INTER          PN N2 D 1.100 10,11 11.121,00 C")
        assertThat (negocioRealizado.prazo).isEqualTo(PrazoNegociacao.DAYTRADE)
        assertThat (negocioRealizado.tipo).isEqualTo(TipoNegociacao.VENDA)
        assertThat (negocioRealizado.titulo).isEqualTo("BANCO INTER PN N2")
        assertThat (negocioRealizado.quantidade).isEqualTo(1100)
        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("11121")
    }

    @Test
    fun deveParsearLinhaComObservacaoDayTradeSustenido() {
        val negocioRealizado =
            parser.parsearLinha("1-BOVESPA V VISTA GETNINJAS          ON NM D# 200 2,97 594,00 C")
        assertThat (negocioRealizado.prazo).isEqualTo(PrazoNegociacao.DAYTRADE)
        assertThat (negocioRealizado.tipo).isEqualTo(TipoNegociacao.VENDA)
        assertThat (negocioRealizado.titulo).isEqualTo("GETNINJAS ON NM")
        assertThat (negocioRealizado.quantidade).isEqualTo(200)
        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("594")
    }


    @Test
    fun deveParsearLinhaDoMercadoFracionario() {
        val negocioRealizado =
            parser.parsearLinha("1-BOVESPA V FRACIONARIO CPFL ENERGIA          ON NM 7 30,78 215,46 C")
        assertThat (negocioRealizado.prazo).isEqualTo(PrazoNegociacao.POSICAO)
        assertThat (negocioRealizado.tipo).isEqualTo(TipoNegociacao.VENDA)
        assertThat (negocioRealizado.titulo).isEqualTo("CPFL ENERGIA ON NM")
        assertThat (negocioRealizado.quantidade).isEqualTo(7)
        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("215.46")

    }
}