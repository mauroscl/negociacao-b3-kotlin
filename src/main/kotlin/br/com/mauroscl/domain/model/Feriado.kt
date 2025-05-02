package br.com.mauroscl.domain.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import java.time.LocalDate

data class Feriado @BsonCreator constructor (@BsonProperty("data") val data: LocalDate, @BsonProperty("descricao") val descricao: String)
