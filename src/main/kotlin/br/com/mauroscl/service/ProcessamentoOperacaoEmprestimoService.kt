package br.com.mauroscl.service

import br.com.mauroscl.infra.OperacaoEmprestimoRepository
import br.com.mauroscl.parsing.OperacaoEmprestimoParser
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@ApplicationScoped
class ProcessamentoOperacaoEmprestimoService(private val operacaoEmprestimoRepository: OperacaoEmprestimoRepository) :
    IProcessamentoOperacaoEmprestimoService {
    @Transactional
    override fun processar(paginas: Collection<String>) {
        val operacoesEmprestimo = OperacaoEmprestimoParser.parse(paginas)
        if (operacoesEmprestimo.isEmpty()) throw RuntimeException("Não foram encontradas notas de empréstimo")
        operacoesEmprestimo.forEach { operacoes ->
            this.operacaoEmprestimoRepository.persist(operacoes)
        }
    }
}