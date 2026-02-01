# NimbusFeedback
Plataforma serverless para coleta de avaliações e geração de relatórios em AWS.

O repositório é multi-módulo (Maven) com dois serviços:
- Feedback: API HTTP (Lambda + API Gateway) para receber avaliações e publicar notificações de urgência.
- Report: Lambda agendada para gerar relatórios em CSV a partir dos dados e gravar no S3.

## Stack
- Quarkus (Java)
- AWS Lambda
- S3
- SNS
- AWS Serveless Application Model (SAM)
- Dynamo
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

## Deploy automático (GitHub Actions)
A cada push na branch main:
- Compila o projeto
- Gera os targets necessários para a lambda function
- Instala o SAM CLI
- Configura AWS 
- Realiza o SAM deploy