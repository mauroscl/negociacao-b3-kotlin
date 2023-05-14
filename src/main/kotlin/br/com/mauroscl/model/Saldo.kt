package br.com.mauroscl.model

import br.com.mauroscl.parsing.NegocioRealizado
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.math.BigDecimal

/***
 * quantidade: tem sinal
 * precoMedio: não tem sinal
 */

class Saldo @BsonCreator constructor(
    @BsonProperty("titulo") val titulo: String,
    @BsonProperty("quantidade") var quantidade: Int,
    @BsonProperty("precoMedio") var precoMedio: BigDecimal
) {

    var id: ObjectId? = null //necessário para funcionar o update no mongo
    lateinit var valorTotal: BigDecimal
    private val quantidadeAsBigDecimal get() = BigDecimal(quantidade)

    init {
        atualizarValorTotal()
    }
    fun aumentarPosicao(negocio: NegocioRealizado) {
        this.quantidade += negocio.quantidadeComSinal;
        this.precoMedio = valorTotal.add(negocio.valorLiquidacao).divide(quantidadeAsBigDecimal)
        atualizarValorTotal()
    }

    fun diminuirPosicao(fechamentoPosicao: FechamentoPosicao, negocio: NegocioRealizado) {
        if (negocio.quantidade > fechamentoPosicao.quantidade) {
            this.quantidade = negocio.quantidadeComSinal + fechamentoPosicao.quantidade
            this.precoMedio = negocio.valorLiquidacaoUnitario
        } else {
            this.quantidade -= fechamentoPosicao.quantidade
            if (this.quantidade == 0) {
                this.precoMedio = BigDecimal.ZERO
            }
        }
        atualizarValorTotal()
    }

    private fun atualizarValorTotal() {
        valorTotal = precoMedio.multiply(quantidadeAsBigDecimal)
    }

}

