package br.com.mauroscl.infra

import br.com.mauroscl.domain.model.Feriado
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate

@ApplicationScoped
class FeriadoRepository: PanacheMongoRepository<Feriado> {
    fun obterDesde(dataInicial: LocalDate): List<Feriado> = find("data >= ?1", dataInicial).list()
}
