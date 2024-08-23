package br.com.mauroscl.domain.service

import java.time.DayOfWeek
import java.time.LocalDate

class LiquidacaoService {
    companion object {
        fun calcularData(dataBase: LocalDate): LocalDate {
            var diasUteis = 0
            var dataLiquidacao = dataBase
            while (diasUteis < 3){
                dataLiquidacao = dataLiquidacao.plusDays(1)
                if (!WEEKEND_DAYS.contains(dataLiquidacao.dayOfWeek)){
                    diasUteis++;
                }
            }
            return dataLiquidacao
        }

        private val WEEKEND_DAYS = listOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY)
    }
}