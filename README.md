# NimbusFeedback
Software para gestão de feedbacks de aulas

## Stack
- Quarkus (Java)
- AWS Lambda (API Gateway proxy via quarkus-amazon-lambda-rest)
- Maven
- GitHub Actions

## Endpoint
POST /avaliacao
Body:
{
  "descricao": "string",
  "nota": 0
}

Resposta (201):
{
  "id": "uuid",
  "descricao": "string",
  "nota": 0,
  "urgencia": "CRITICA|ALTA|MEDIA|BAIXA",
  "dataEnvio": "2024-01-01T12:00:00Z"
}

Validações:
- descricao: obrigatória
- nota: inteiro de 0 a 10

## Execução local
1) Requisitos: Java 21+, Maven 3.9+
2) Subir em modo dev:
   mvn quarkus:dev
3) Testar:
   curl -X POST http://localhost:8080/avaliacao \
     -H 'Content-Type: application/json' \
     -d '{"descricao":"Ótima aula","nota":9}'

## Empacotamento para Lambda
Gere o pacote com:
mvn clean package -DskipTests

O Quarkus produz automaticamente o artefato:
target/function.zip

Handler (na Lambda):
io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler

Runtime da Lambda:
Java 21 (Corretto)

## Deploy automático (GitHub Actions)
A cada push na branch main:
- Compila o projeto
- Gera target/function.zip
- Executa update-function-code na função informada

Configurar secrets do repositório:
- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS_REGION (ex.: us-east-1)
- LAMBDA_FUNCTION_NAME (nome da função criada previamente)

## Criação da função (uma vez, via console/CLI)
- Runtime: Java 21
- Architecture: x86_64 ou arm64
- Handler: io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler
- Memória: 512 MB (sugestão)
- Timeout: 15s (sugestão)
- Integração: API Gateway HTTP/API ou Lambda Function URL conforme estratégia

Após criada, os próximos commits na main atualizam o código automaticamente.
