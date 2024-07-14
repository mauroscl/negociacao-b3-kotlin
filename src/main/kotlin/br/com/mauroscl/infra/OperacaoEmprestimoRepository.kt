package br.com.mauroscl.infra

import br.com.mauroscl.parsing.OperacaoEmprestimo
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OperacaoEmprestimoRepository: PanacheMongoRepository<OperacaoEmprestimo> {
    fun obterNaoContabilizados(codigo: String): List<OperacaoEmprestimo> {
        return list("papel = ?1 and contabilizado = ?2 and lado = ?3", codigo, false, "Tomador")
    }
}