package br.com.mauroscl.parsing

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripperByArea
import java.awt.geom.Rectangle2D
import java.io.File
import java.io.IOException
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PdfLoader(@Inject var notaNegociacaoParser: NotaNegociacaoParser) {
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
                return notaNegociacaoParser.parse(paginas)
            }
        } catch (e: IOException) {
            throw RuntimeException("Erro ao carregar arquivo", e)
        }
    } /*
    public void parseSingle() {
        PDDocument document;
        PDFTextStripper stripper;
        try {
            document = PDDocument.load(new File("D:\\Users\\mauro\\Documents\\Economia\\notas de corretagem\\rico\\2020\\202003\\Nota de Corretagem 2020-03-20.pdf"));
            stripper = new PDFTextStripper();

            stripper.setSortByPosition(true);
            String notaEmTexto = stripper.getText(document);
            System.out.println(notaEmTexto);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar arquivo", e);
        }
    }
*/
}
