package br.com.mauroscl.domain.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty

data class Ativo @BsonCreator constructor (@BsonProperty("codigo") val codigo: String,@BsonProperty("nome") val nome: String)
