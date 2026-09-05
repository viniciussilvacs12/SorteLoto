# API REST — SorteLoto

Base local: `/api`

## Autenticação

| Método | Caminho | Uso |
|---|---|---|
| POST | `/auth/register` | Cadastro local |
| POST | `/auth/login` | Login local |
| GET | `/auth/google/config` | Informa se Google Login está habilitado |
| POST | `/auth/google` | Troca credencial Google por JWT SorteLoto |

## Jogos e análise

| Método | Caminho | Uso |
|---|---|---|
| GET | `/games/mega-sena` | Gera combinação Mega-Sena |
| GET | `/games/lotofacil` | Gera combinação Lotofácil |
| GET | `/games/history` | Histórico de jogos gerados |
| POST | `/analysis` | Análise estrutural |
| POST | `/smart-analysis` | SmartScore e análise ampliada |
| GET | `/smart-generator/{type}` | Geração orientada pelo SmartScore |
| GET | `/backtest/{type}` | Backtest estatístico |
| GET | `/strategy-comparison/{type}` | Compara estratégia e geração aleatória |

## Estatísticas e concursos

| Método | Caminho | Uso |
|---|---|---|
| GET | `/stats/{type}` | Frequências e atrasos |
| GET | `/draws/{type}` | Página de concursos |
| GET | `/draws/{type}/{contest}` | Concurso específico |
| GET | `/sync-status/{type}` | Estado da base e sincronização |

`type` aceita `MEGA_SENA` ou `LOTOFACIL`.

## Usuário autenticado

Envie `Authorization: Bearer <jwt>`.

| Método | Caminho | Uso |
|---|---|---|
| GET | `/profile/me` | Perfil atual |
| POST | `/saved-games` | Salva jogo |
| GET | `/saved-games` | Lista jogos do usuário |
| GET | `/saved-games/ranking` | Ranking SmartScore |
| PATCH | `/saved-games/{id}/favorite` | Favorita/desfavorita |
| GET | `/saved-games/{id}/check` | Confere jogo |
| DELETE | `/saved-games/{id}` | Exclui jogo |

## Administração

Requer JWT com role `ADMIN`.

| Método | Caminho | Uso |
|---|---|---|
| POST | `/import/{type}/sync` | Sincroniza concursos faltantes |
| POST | `/import/{type}/missing` | Busca faltantes |
| POST | `/import/{type}?quantity=N` | Importação manual recente |
