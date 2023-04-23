package br.com.mauroscl.parsing

import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

class TestInputDataLoader {
    @Throws(IOException::class)
    fun loadContent(fileName: String?): String {
        val classLoader = javaClass.classLoader
        println("carregando conteudo")
        val file = classLoader.getResource(fileName).file
        val contentInBytes = FileInputStream(file).readAllBytes()
        return String(contentInBytes, StandardCharsets.UTF_8)
    }
}
