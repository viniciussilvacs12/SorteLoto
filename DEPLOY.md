# SorteLoto v2.0 — checklist de deploy

A v2.0 está estruturada para containerização, mas publicar na internet exige configuração do ambiente alvo.

## 1. Segredos e variáveis
Não grave credenciais reais no Git. Configure no provedor:
- `GOOGLE_CLIENT_ID`
- credenciais PostgreSQL
- chave JWT forte e exclusiva
- usuário/senha ADMIN de produção, se o bootstrap for mantido

## 2. Google Login
No Google Cloud, adicione o domínio HTTPS de produção em **Authorized JavaScript origins** para o mesmo OAuth Client Web ou crie um cliente específico de produção.

## 3. Banco
Use PostgreSQL persistente/gerenciado e backup periódico. O container de banco do `docker-compose.yml` é adequado ao desenvolvimento, não substitui uma estratégia de produção.

## 4. HTTPS e proxy
Coloque o serviço atrás de HTTPS. O frontend Nginx continua encaminhando `/api/` para o backend no ambiente Compose.

## 5. CORS
Antes de publicar, substitua os origins localhost de `SecurityConfig` pelo(s) domínio(s) real(is) da aplicação.

## 6. Integração de resultados
A sincronização tem timeout, retry, validação e preserva dados existentes, mas a disponibilidade da fonte externa não é controlada pelo SorteLoto. Monitore falhas e mantenha a importação manual como contingência.

## 7. Verificações antes do release
- login local;
- login Google;
- ADMIN;
- sincronização Mega-Sena;
- sincronização Lotofácil;
- histórico/paginação/pesquisa;
- salvar/favoritar/conferir/excluir jogos;
- estatísticas;
- backtest;
- testes em largura mobile;
- backup/restauração PostgreSQL.
