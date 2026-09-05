# Security Policy

## Segredos

Nunca envie ao repositório:

- `.env`;
- senhas reais do PostgreSQL/admin;
- chaves JWT;
- tokens ou credenciais privadas.

O projeto mantém apenas `.env.example` com valores de exemplo.

## Produção

Antes de publicar uma instância pública:

- use HTTPS;
- restrinja CORS ao domínio do frontend;
- configure segredos no provedor de hospedagem;
- desative o bootstrap de admin;
- troque `ddl-auto=update` por migrations versionadas;
- configure observabilidade, rate limiting e políticas de rotação/revogação de tokens conforme a necessidade.
