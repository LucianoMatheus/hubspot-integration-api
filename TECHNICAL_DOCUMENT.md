# Documentação Técnica – API de Integração com HubSpot (hubspot-integration-api)

## Visão Geral

Esta API foi desenvolvida em Spring Boot com o objetivo de integrar sistemas internos com o HubSpot, utilizando autenticação via OAuth 2.0, criação de contatos e recebimento de notificações via Webhooks.

A aplicação permite a modularização e é extensível para futuras necessidades de integração com outros endpoints e recursos da API do HubSpot.

---

## Decisões Técnicas e Justificativas

### 1. **Framework: Spring Boot**
- Escolhido por ser mais robusto, ter mais simplicidade na configuração de APIs REST e nas configurações, forte integração com bibliotecas mais atuais e uma forte documentação técnica.
- Permitiu estruturar facilmente endpoints REST e aplicar boas práticas como separação de responsabilidades (Controller, Service, Model, Exception, Etc).

### 2. **Integração com OAuth 2.0**
- A autenticação com o HubSpot é baseada em OAuth 2.0, um padrão amplamente utilizado para autorização de aplicações de terceiros.
- A API implementa um fluxo simplificado com geração de URL de autorização, callback para captura do `code` e troca por `access_token`.

### 3. **Criação de Contatos no HubSpot**
- Utilizado a API oficial do HubSpot com o endpoint `https://api.hubapi.com/crm/v3/objects/contacts`.
- O `access_token` obtido no fluxo OAuth é utilizado para autenticar as chamadas de criação de contato.

### 4. **Tratamento Global de Exceções**
- Implementado com a notação `@ControllerAdvice`, padronizando a forma como os erros são retornados para o cliente.
- Exceções específicas foram criadas para lidar com falhas de comunicação com o HubSpot (como por exemplo a, `HubspotAuthorizationException`).

---

## Uso da biblioteca Resilience4j:

### Por que usar a biblioteca Resilience4j?

A API da HubSpot possui **limitações de taxa de requisição (rate limit)** e **pode sofrer indisponibilidades momentâneas**. Para garantir resiliência da aplicação e melhor experiência do usuário, utilizei a biblioteca [Resilience4j](https://resilience4j.readme.io/), que fornece:

- **Rate Limiter**: Para evitar exceder os limites da HubSpot, protegendo a aplicação contra bloqueios temporários.
- **Retry**: Tenta novas tentativas de requisição automaticamente em caso de falhas como por exemplo, o timeout.

### Como foi implementado:

- Foi criada uma classe `ResilienceConfig` com a notação @Bean do `Retry` e do `RateLimiter`, configurando limites de chamadas e tentativas.
- No método de criação de contato, foi utilizado o padrão `decorateSupplier` para juntar a lógica de retry e do rate limit.
- Em caso de falha após todas as tentativas, a exceção personalizada é lançada com a mensagem padrão.

---

## Possíveis Melhorias Futuras

1. **Persistência de Access Tokens**
   - Salvar e gerenciar `access_token` e `refresh_token` em um banco de dados.
   - Automatizar o fluxo de renovação usando o `refresh_token`.

2. **Cache para escopos e configurações**
   - Cache das configurações do HubSpot, tokens e escopos usando a própria biblioteca do Spring, a Spring Cache ou outra similar.

3. **Publicação com HTTPS**
   - Para Webhooks funcionarem corretamente em ambiente real, é necessário expor a aplicação em HTTPS público (como por exemplo: deploy via AWS/Heroku ou similar).

4. **Validações mais robustas**
   - Aplicar validações em nível de DTOs mais robustas com as notaçoes do `javax.validation` (como por exemplo: @Email, @NotBlank, etc).

5. **Testes Unitários**
   - Criar testes unitários para a classe do Service.
   - Testes de integração para os fluxos OAuth e criação de contatos.
