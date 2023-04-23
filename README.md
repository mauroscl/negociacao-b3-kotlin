# Build

Para alterar a versão do arquivo que será gerado alterar a propriedade version no arquivo build.gradle. 

Por exemplo:

```
version '1.3.0'
```

Para gerar o fat jar executável rodar o seguinte comando
```
gradlew shadowJar
```

Caso queira gerar uma versão sem alterar a propriedade version deve ser passado o parâmetro projVersion, como no exemplo a seguir:  
```
./gradlew -PprojVersion=1.3.0 shadowJar
```

Será gerado o arquivo `negociacao-b3-1.3.0-all.jar` no caminho `build/libs`

