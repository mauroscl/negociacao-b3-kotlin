package br.com.mauroscl.parsing

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NotaEmprestimoParser {

    private val ESPACO = " "
    fun parse(paginas: Collection<String>): NotaEmprestimo {
        if (paginas.size > 1) {
            throw RuntimeException("Parser não está preparado para processar mais de uma página")
        }
        val pagina = paginas.first()
        val linhas = pagina.split(System.lineSeparator())
        val segundaLinha = linhas[1]
        val campos = segundaLinha.split(ESPACO)
        val dataLiquidacaoString = campos[3]
        val dataLiquidacao = LocalDate.parse(dataLiquidacaoString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val indiceInicio = pagina.indexOf("Operações") + 10
        val indiceFim = pagina.indexOf("Resumo Financeiro") - 1
        val operacoesString = pagina.substring(indiceInicio, indiceFim)
        val operacoes = operacoesString.split("Lado: ")
            .drop(1)
            .map { operacaoString ->
                    val lado = operacaoString.substringBefore(ESPACO)
                    val papel = operacaoString.substringAfter("Papel: ").substringBefore(ESPACO)
                    val valor = operacaoString.substringAfter("Valor Líquido ").replace(System.lineSeparator(), "")
                    Operacao(papel, lado, BigDecimal(NumeroFormatador.converterDePortuguesParaIngles(valor)) )
            }
        return NotaEmprestimo(dataLiquidacao, operacoes)
    }
}