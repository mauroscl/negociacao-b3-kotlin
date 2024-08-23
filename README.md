# Mongo 
Subir o banco via docker-compose
```shell
docker compose up -f ~/dev/docker/mihaylovin/docker-compose.yml                          
```
Conectar no mongo via docker 
```shell
docker exec -it mongo /bin/sh
```

Após entrar no shell do container executar o comando mongosh
```text
# mongosh
Current Mongosh Log ID:	66299a853d6d71e995c934dc
Connecting to:		mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.2.4
Using MongoDB:		7.0.8
Using Mongosh:		2.2.4

For mongosh info see: https://docs.mongodb.com/mongodb-shell/


To help improve our products, anonymous usage data is collected and sent to MongoDB periodically (https://www.mongodb.com/legal/privacy-policy).
You can opt-out by running the disableTelemetry() command.

------
   The server generated these startup warnings when booting
   2024-04-24T23:46:40.807+00:00: Using the XFS filesystem is strongly recommended with the WiredTiger storage engine. See http://dochub.mongodb.org/core/prodnotes-filesystem
   2024-04-24T23:46:41.441+00:00: Access control is not enabled for the database. Read and write access to data and configuration is unrestricted
   2024-04-24T23:46:41.442+00:00: vm.max_map_count is too low
------

```

 No monosh entrar no modo admin
```text
dbrs [direct: primary] test> use admin
switched to db admin
```
Criar o usuário admin
```text
dbrs [direct: primary] admin> var adminDB = db.getSiblingDB('admin');

dbrs [direct: primary] admin> adminDB.createUser({
...     user: 'superuser',
...     pwd: 'superuser',
...     roles: [{ role: 'root', db: 'admin' }]
... });
{
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1714002681, i: 4 }),
    signature: {
      hash: Binary.createFromBase64('AAAAAAAAAAAAAAAAAAAAAAAAAAA=', 0),
      keyId: Long('0')
    }
  },
  operationTime: Timestamp({ t: 1714002681, i: 4 })
}

```

## Fazer backup

```shell
docker exec mongo sh -c 'exec mongodump --uri="mongodb://superuser:superuser@localhost:27017/?replicaSet=dbrs" --authenticationDatabase=admin -d negociacao-b3 --archive' > /home/mauro/negociacao/dumps/negociacao-b3-202212.archive
```

## Restaurar backup 
```shell
docker exec -i mongo sh -c 'mongorestore --uri="mongodb://superuser:superuser@localhost:27017/?replicaSet=dbrs" --authenticationDatabase=admin --drop --nsFrom="negociacao-b3.*" --nsTo="negociacao-b3.*" --archive' < /home/mauro/negociacao/dumps/negociacao-b3-202212.archive
```

# Build

Executar o comando
```shell
./gradlew build
```
Vai ser gerado os arquivos na pasta build/quarkus-app

Você pode copiar os arquivos do build para outra localização.  
Tem que copiar todos os arquivos da pasta quarkus-app

Talvez precise apenas copiar o arquivo jar gerado na pasta. Isto não foi testado

# Comandos

## Processar uma nota

```shell
java -jar build/quarkus-app/quarkus-run.jar parser <caminho do arquivo> 
```
Exemplo:
```shell
java -jar build/quarkus-app/quarkus-run.jar parser ~/negociacao/202204/NOTA_DE_CORRETAGEM_01_04_2022.pdf
 
```
## Processar uma data
Para executar o comando já deve ter sido o parser na data que se quer processar
Exemplo:
```shell
 java -jar build/quarkus-app/quarkus-run.jar processar-nota <data>
```
```shell
 java -jar ~/dev/outros/negociacao-b3-kotlin/build/quarkus-app/quarkus-run.jar processar-nota 2022-08-25
```

## Processar notas de aluguel

```shell
java -jar build/quarkus-app/quarkus-run.jar parser-aluguel <caminho do arquivo> 
```
Exemplo:
```shell
java -jar build/quarkus-app/quarkus-run.jar parser-aluguel ~/negociacao/202204/NOTA_ALUGUEL.pdf
``` 
# Debug
O debug não funciona rodando o Intellij no modo DEBUG.  
Rodar em modo normal e depois ir no menu Run > Attach to process.