package br.com.mauroscl.parsing

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NegocioMercadoVistaParserTest {

    @Test
    fun deveParsearLinhaSemObservacao() {
        val parser = NegocioMercadoVistaParser()
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
        val parser = NegocioMercadoVistaParser()
        val negocioRealizado =
            parser.parsearLinha("1-BOVESPA C VISTA LOJAS RENNER          ON NM # 100 53,74 5.374,00 D")
        assertThat (negocioRealizado.tipo).isEqualTo(TipoNegociacao.COMPRA)
        assertThat (negocioRealizado.prazo).isEqualTo(PrazoNegociacao.POSICAO)
        assertThat (negocioRealizado.titulo).isEqualTo("LOJAS RENNER ON NM")
        assertThat (negocioRealizado.quantidade).isEqualTo(100)
        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("5374")
    }

    @Test
    fun deveParsearLinhaComObservacaoDayTrade() {
        val parser = NegocioMercadoVistaParser()
        val negocioRealizado =
            parser.parsearLinha("1-BOVESPA V VISTA BANCO INTER          PN N2 D 1.100 10,11 11.121,00 C")
        assertThat (negocioRealizado.prazo).isEqualTo(PrazoNegociacao.DAYTRADE)
        assertThat (negocioRealizado.tipo).isEqualTo(TipoNegociacao.VENDA)
        assertThat (negocioRealizado.titulo).isEqualTo("BANCO INTER PN N2")
        assertThat (negocioRealizado.quantidade).isEqualTo(1100)
        assertThat (negocioRealizado.valorOperacional).isEqualByComparingTo("11121")
    }
}