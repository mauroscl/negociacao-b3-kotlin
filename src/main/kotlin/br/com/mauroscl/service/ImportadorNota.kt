package br.com.mauroscl.service

import br.com.mauroscl.infra.NotaNegociacaoRepository
import br.com.mauroscl.parsing.NotaNegociacao
import br.com.mauroscl.parsing.NotaNegociacaoParser
import br.com.mauroscl.parsing.PdfLoader
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ImportadorNota(
    private val pdfLoader: PdfLoader,
    private val notaNegociacaoParser: NotaNegociacaoParser,
    private val notaNegociacaoRepository: NotaNegociacaoRepository
) : IImportadorNota {

    override fun executar(arquivo: String): NotaNegociacao {
        val paginas = pdfLoader.parseByArea(arquivo)
        val notaNegociacao = notaNegociacaoParser.parse(paginas)
        notaNegociacao.unificarPaginas()
        notaNegociacaoRepository.persist(notaNegociacao)

        return notaNegociacao
    }
}