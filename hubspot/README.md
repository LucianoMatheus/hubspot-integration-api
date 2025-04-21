# API de Integração com o HubSpot

Esta é uma API desenvolvida em Spring Boot para integração com o HubSpot via OAuth 2.0. Ela permite autenticação via OAuth, criação de contatos e recebimento de notificações via webhooks. Também foi implementado controle de rate limit usando a biblioteca Resilience4j.

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Conta de desenvolvedor e de teste no HubSpot com um aplicativo criado ([HubSpot Developer Portal](https://app.hubspot.com))
- IDE (IntelliJ, VSCode, Eclipse ou Spring Tools Suite)
- Postman (opcional para testes da API)

---

## Configuração

### 1. Propriedades no arquivo `application.properties`

spring.application.name=hubspot-integration-api
hubspot.client-id=SEU_CLIENT_ID
hubspot.client-secret=SEU_CLIENT_SECRET
hubspot.redirect-uri=http://localhost:8080/api/oauth/callback
hubspot.webhooks-uri=http://localhost:8080/api/webhooks
hubspot.scopes=oauth crm.objects.contacts.write
hubspot.name=hubspot


> O `redirect-uri` deve ser exatamente a mesma configurada no aplicativo do HubSpot.

---

### 2. Dependências principais (no seu `pom.xml`)

<dependencies>
  <!-- Spring Boot -->
  <dependency>...</dependency>

  <!-- Web -->
  <dependency>...</dependency>

  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
  </dependency>

  <!-- Resilience4j Limiter -->
  <dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-ratelimiter</artifactId>
    <version>1.7.0</version>
  </dependency>

  <!-- Resilience4j Retry -->
  <dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-retry</artifactId>
    <version>1.7.0</version>
  </dependency>

  <!-- Jackson / JSON -->
  <dependency>...</dependency>
</dependencies>


---

## Executando a aplicação

1. Clone este repositório.
2. Configure o `application.properties` com as informações do seu aplicativo no HubSpot.
3. Execute a aplicação via IDE ou terminal:

bash
./mvnw spring-boot:run


---

## Fluxo de Autenticação com HubSpot (OAuth 2.0)

### 1. Obter URL de autorização

GET http://localhost:8080/api/oauth/url


Acesse a URL retornada no navegador, selecione sua conta de teste no HubSpot e clique em “Escolher Conta”.

---

### 2. Gerar `access_token`

POST http://localhost:8080/api/oauth/callback?code=SEU_CODE


O HubSpot irá redirecionar automaticamente para a URL de callback e retornar um JSON com o token. Copie o valor da propriedade `access_token`.

---

## Criar um contato no HubSpot

### Endpoint


POST http://localhost:8080/api/contact


---

### Headers


Content-Type: application/json
Authorization: SEU_ACCESS_TOKEN_GERADO

---

### Body (JSON)

json
{
  "email": "test@test.com",
  "firstname": "Test Name",
  "lastname": "Test Last Name",
  "phone": "(62) 9989-8999"
}


---

## Webhook Endpoint

Para configurar o recebimento de Webhooks pelo HubSpot, o endpoint deve estar publicado com HTTPS. Entretanto, você pode usar o Postman para simular notificações:

### Endpoint


POST http://localhost:8080/api/webhooks


---

### Body (JSON)

[
  {
    "eventId": 1234567890,
    "subscriptionId": 987654321,
    "portalId": 123456,
    "appId": 11199401,
    "occurredAt": 1681241234567,
    "subscriptionType": "contact.creation",
    "attemptNumber": 0,
    "objectId": 101,
    "propertyName": "Contact Test 1",
    "propertyValue": "CT123456",
    "changeSource": "CRM",
    "eventType": "creation"
  },
  {
    "eventId": 1234567891,
    "subscriptionId": 987654322,
    "portalId": 123457,
    "appId": 11199401,
    "occurredAt": 1681241234578,
    "subscriptionType": "contact.creation",
    "attemptNumber": 0,
    "objectId": 101,
    "propertyName": "Contact Test 2",
    "propertyValue": "CT123457",
    "changeSource": "CRM",
    "eventType": "creation"
  }
]

---

## Tratamento de Erros

A API possui tratamento global de exceções usando `@ControllerAdvice`. Os erros são retornados no seguinte formato:


{
  "message": "Descrição do erro",
  "status": 400,
  "date": "2025-04-21T16:45:00.000000"
}

---

## Rate Limit

A criação de contatos utiliza a biblioteca Resilience4j para:

- Retentativas automáticas de criação de contato (Retry)
- Controle de chamadas por segundo (Rate Limiter)

As configurações estão centralizadas na classe `ResilienceConfig.java`.

---

## Testes com Postman

1. Obtenha o token conforme instruções anteriores.
2. Configure um novo request no Postman:

- Método: `POST`
- URL: `http://localhost:8080/api/contact`
- Headers:


Content-Type: application/json
Authorization: SEU_ACCESS_TOKEN_GERADO


- Body:

{
  "email": "test@test.com",
  "firstname": "Test Name",
  "lastname": "Test Last Name",
  "phone": "(62) 9989-8999"
}

---

## Referências

- [Documentação oficial do HubSpot](https://developers.hubspot.com/docs/api/overview)
- [OAuth 2.0 com HubSpot](https://developers.hubspot.com/docs/api/working-with-oauth)
- [Webhooks do HubSpot](https://developers.hubspot.com/docs/guides/api/app-management/webhooks)
- [Resilience4j](https://resilience4j.readme.io/)

---

## Contato

Para dúvidas ou sugestões, entre em contato com o desenvolvedor responsável:

**E-mail:** lucianomatheus@gmail.com