package br.com.mauroscl.parsing

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.time.LocalDate

class NotaNegociacao @BsonCreator  internal constructor(
    @BsonProperty("data") val data: LocalDate) {
    @BsonProperty("paginas")
    var paginas: MutableList<Pagina>

    init {
        paginas = ArrayList()
    }

    fun adicionarPagina(pagina: Pagina) {
        paginas.add(pagina)
    }

    fun unificarPaginas() {
        val paginasUnificadas = ArrayList<Pagina>()
        var paginaSemResumoFinanceiro: Pagina? = null
        for (pagina in paginas) {
            if (paginaSemResumoFinanceiro != null) {
                pagina.adicionarNegocios(paginaSemResumoFinanceiro.negocios)
            }
            paginaSemResumoFinanceiro = if (pagina.temResumoFinanceiro()) {
                paginasUnificadas.add(pagina)
                null
            } else {
                pagina
            }
        }
        for (paginaUnificada in paginasUnificadas) {
            paginaUnificada.agruparNegocios()
            paginaUnificada.ratearCustos()
        }
        paginas = paginasUnificadas
    }
}
