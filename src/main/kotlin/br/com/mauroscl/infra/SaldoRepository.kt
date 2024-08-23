package br.com.mauroscl.infra

import br.com.mauroscl.domain.model.Saldo
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SaldoRepository: PanacheMongoRepository<Saldo> {

    fun obterPorTitulo(titulo: String): Saldo? {
        return find("titulo", titulo).firstResult()
    }

}