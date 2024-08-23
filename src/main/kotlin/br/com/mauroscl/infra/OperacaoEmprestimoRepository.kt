package br.com.mauroscl.infra

import br.com.mauroscl.parsing.OperacaoEmprestimo
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate

@ApplicationScoped
class OperacaoEmprestimoRepository: PanacheMongoRepository<OperacaoEmprestimo> {
    fun obterNaoContabilizados(codigo: String, dataLimite: LocalDate): List<OperacaoEmprestimo> {
        return list("papel = ?1 and contabilizado = ?2 and lado = ?3 and dataLiquidacao <= ?4", codigo, false, "Tomador", dataLimite)
    }
}