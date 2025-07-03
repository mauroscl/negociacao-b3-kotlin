package br.com.mauroscl.domain.service

import br.com.mauroscl.domain.model.Feriado
import java.time.DayOfWeek
import java.time.LocalDate

class LiquidacaoService {
    companion object {
        fun calcularData(dataNegociacao: LocalDate, feriados: List<Feriado>): LocalDate {
            var diasUteis = 0
            var dataLiquidacao = dataNegociacao
            while (diasUteis < QUANTIDADE_DIAS_UTEIS_PARA_LIQUIDACAO){
                dataLiquidacao = dataLiquidacao.plusDays(1)
                if (isDiaUtil(dataLiquidacao, feriados)){
                    diasUteis++;
                }
            }
            return dataLiquidacao
        }

        private fun isDiaUtil(data: LocalDate, feriados: List<Feriado>) = !WEEKEND_DAYS.contains(data.dayOfWeek) &&
        !feriados.any {it.data == data}

        private val WEEKEND_DAYS = listOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY)
        private const val QUANTIDADE_DIAS_UTEIS_PARA_LIQUIDACAO = 2
    }
}