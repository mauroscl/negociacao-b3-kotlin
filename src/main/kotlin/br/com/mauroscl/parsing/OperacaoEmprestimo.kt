package br.com.mauroscl.parsing

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.LocalDate

class OperacaoEmprestimo @BsonCreator internal constructor(
    @BsonProperty("papel") val papel: String,
    @BsonProperty("lado") val lado: String,
    @BsonProperty("dataLiquidacao") val dataLiquidacao: LocalDate,
    @BsonProperty("valorLiquido") val valorLiquido: BigDecimal,
    @BsonProperty("quantidadeOriginal") val quantidadeOriginal: Int,
    @BsonProperty("quantidadeLiquidacao") val quantidadeLiquidacao: Int,
    @BsonProperty("quantidadeAtual") val quantidadeAtual: Int
){
    @BsonProperty("contabilizado") var contabilizado: Boolean = false
    var id: ObjectId? = null //necessário para funcionar o update no mongo

    fun marcarComoContabilizado() {
        this.contabilizado = true;
    }
}