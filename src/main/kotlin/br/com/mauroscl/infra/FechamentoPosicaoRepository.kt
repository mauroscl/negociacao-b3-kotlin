package br.com.mauroscl.infra

import br.com.mauroscl.model.FechamentoPosicao
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class FechamentoPosicaoRepository: PanacheMongoRepository<FechamentoPosicao>