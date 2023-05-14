# Build

Executar o comando
```
./gradlew build
```
Vai ser gerado os arquivos na pasta build/quarkus-app

Para executar executar o comando
```
java -jar build/quarkus-app/quarkus-run.jar <caminho do arquivo> 
```
Exemplo:
```
 java -jar build/quarkus-app/quarkus-run.jar ~/negociacao/202204/NOTA_DE_CORRETAGEM_01_04_2022.pdf 
```
Você pode copiar os arquivos do build para outra localização.  
Tem que copiar todos os arquivos da pasta quarkus-app

Talvez precise apenas copiar o arquivo jar gerado na pasta. Isto não foi testado