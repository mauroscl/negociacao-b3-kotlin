package br.com.mauroscl.infra

import br.com.mauroscl.model.Saldo
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SaldoRepository: PanacheMongoRepository<Saldo> {

    fun obterPorTitulo(titulo: String): Saldo? {
        return find("titulo", titulo).firstResult()
    }

}