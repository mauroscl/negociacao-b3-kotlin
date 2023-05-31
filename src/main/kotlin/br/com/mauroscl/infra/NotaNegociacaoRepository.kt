package br.com.mauroscl.infra

import br.com.mauroscl.parsing.NotaNegociacao
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class NotaNegociacaoRepository: PanacheMongoRepository<NotaNegociacao>{
    fun findByData(data: LocalDate) = find("data", data).singleResult()
}