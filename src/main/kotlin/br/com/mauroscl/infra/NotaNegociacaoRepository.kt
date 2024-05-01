package br.com.mauroscl.infra

import br.com.mauroscl.parsing.NotaNegociacao
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate

@ApplicationScoped
class NotaNegociacaoRepository: PanacheMongoRepository<NotaNegociacao>{
    fun findByData(data: LocalDate) = find("data", data).singleResult()
}