package br.com.mauroscl.infra

import br.com.mauroscl.domain.model.FechamentoPosicao
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class FechamentoPosicaoRepository: PanacheMongoRepository<FechamentoPosicao>