package br.com.mauroscl.parsing

import java.time.LocalDate

class NotaEmprestimo(val dataLiquidacao: LocalDate, val operacoes: List<Operacao>) {
}