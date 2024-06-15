package br.com.mauroscl.parsing

fun interface IPaginaParser {
    fun parse(conteudo: String): Pagina
}
