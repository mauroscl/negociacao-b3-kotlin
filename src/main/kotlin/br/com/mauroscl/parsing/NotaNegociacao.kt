package br.com.mauroscl.parsing

class NotaNegociacao internal constructor() {
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
