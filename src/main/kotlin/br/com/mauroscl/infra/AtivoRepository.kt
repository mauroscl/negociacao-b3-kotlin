package br.com.mauroscl.infra

import br.com.mauroscl.domain.model.Ativo
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class AtivoRepository: PanacheMongoRepository<Ativo> {
    fun obterPorNome(nome: String): Ativo? = find("nome", nome).singleResult()
}