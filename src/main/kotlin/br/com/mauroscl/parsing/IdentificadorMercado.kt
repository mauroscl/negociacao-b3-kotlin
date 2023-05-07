package br.com.mauroscl.parsing

import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class IdentificadorMercado {
    fun obterMercado(conteudo: String): Mercado {
        if (conteudo.contains(IDENTIFICADOR_MERCADO_FUTURO)) {
            return Mercado.FUTURO
        }
        if (conteudo.contains(IDENTIFICADOR_MERCADO_AVISTA)) {
            return Mercado.AVISTA
        }
        throw FormatoInformacaoDesconhecidoException("Tipo de Nota n\u00E3o reconhecida")
    }

    companion object {
        const val IDENTIFICADOR_MERCADO_AVISTA =
            "Q Negocia\u00E7\u00E3o C/V Tipo mercado Prazo Especifica\u00E7\u00E3o do t\u00EDtulo Obs. (*) Quantidade Pre\u00E7o / Ajuste Valor Opera\u00E7\u00E3o / Ajuste D/C"
        const val IDENTIFICADOR_MERCADO_FUTURO =
            "C/V Mercadoria Vencimento Quantidade Pre\u00E7o/Ajuste Tipo Neg\u00F3cio Vlr de Opera\u00E7\u00E3o/Ajuste D/C Taxa Operacional"
    }
}
