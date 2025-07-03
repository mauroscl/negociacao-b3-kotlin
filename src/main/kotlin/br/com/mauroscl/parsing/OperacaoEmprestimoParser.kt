package br.com.mauroscl.parsing

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OperacaoEmprestimoParser {

    companion object {
        fun parse(paginas: Collection<String>): List<OperacaoEmprestimo> {
            return paginas
                .filter { it.contains("NOTA DE EMPRÉSTIMO") }
                .flatMap { pagina ->
                    val linhas = pagina.split(System.lineSeparator())
                    val segundaLinha = linhas[1]
                    val campos = segundaLinha.split(ESPACO)
                    val dataLiquidacaoString = campos[3]
                    println("Processando nota de empréstimo na data de liquidação $dataLiquidacaoString")
                    val dataLiquidacao =
                        LocalDate.parse(dataLiquidacaoString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                    val indiceInicio = pagina.indexOf("Operações") + 10
                    val indiceFim = pagina.indexOf("Resumo Financeiro").let {
                        if (it == -1) pagina.length - 1 else it - 1
                    }

                    val operacoesString = pagina.substring(indiceInicio, indiceFim)
                    operacoesString.split("Lado: ")
                        .drop(1)
                        .map { operacaoString ->
                            val lado = operacaoString.substringBefore(ESPACO)
                            val papel = operacaoString.substringAfter("Papel: ").substringBefore(ESPACO)
                            val valor =
                                operacaoString.substringAfter("Valor Líquido ").replace(System.lineSeparator(), "")
                            OperacaoEmprestimo(
                                papel,
                                lado,
                                dataLiquidacao,
                                BigDecimal(NumeroFormatador.converterDePortuguesParaIngles(valor)),
                                obterQuantidade(operacaoString, "Qtd. Original: "),
                                obterQuantidade(operacaoString, "Qtd. Liquidação: "),
                                obterQuantidade(operacaoString, "Qtd. Atual: ")
                            )
                        }
                }
        }

        private fun obterQuantidade(operacao: String, campo: String): Int {
            val quantidade = NumeroFormatador.converterDePortuguesParaIngles(
                operacao.substringAfter(campo).substringBefore(
                    ESPACO
                )
            )
            return Integer.parseInt(quantidade)
        }

        private val ESPACO = " "
    }
}