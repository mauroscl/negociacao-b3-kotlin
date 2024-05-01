package br.com.mauroscl.parsing

import br.com.mauroscl.infra.NotaNegociacaoRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripperByArea
import java.awt.geom.Rectangle2D
import java.io.File
import java.io.IOException

@ApplicationScoped
class PdfLoader(@Inject var notaNegociacaoParser: NotaNegociacaoParser,
                @Inject var notaNegociacaoRepository: NotaNegociacaoRepository) {
    fun parseByArea(filePath: String): NotaNegociacao {
        var stripper: PDFTextStripperByArea
        try {
            PDDocument.load(File(filePath)).use { document ->
                stripper = PDFTextStripperByArea()
                stripper.sortByPosition = true
                val paginas = ArrayList<String>()
                for (i in 0 until document.numberOfPages) {
                    val page = document.getPage(i)
                    val artBox = page.artBox
                    val rectangle2D = Rectangle2D.Float(
                        artBox.lowerLeftX,
                        artBox.lowerLeftY,
                        artBox.width,
                        artBox.height
                    )
                    val regionName = "areaTotal$i"
                    stripper.addRegion(regionName, rectangle2D)
                    stripper.extractRegions(page)
                    val textForRegion = stripper.getTextForRegion(regionName)
                    paginas.add(textForRegion)
                    stripper.removeRegion(regionName)
                }

                val notaNegociacao = notaNegociacaoParser.parse(paginas)
                notaNegociacao.unificarPaginas()
                notaNegociacaoRepository.persist(notaNegociacao)

                return notaNegociacao
            }
        } catch (e: IOException) {
            throw RuntimeException("Erro ao carregar arquivo", e)
        }
    }
}
