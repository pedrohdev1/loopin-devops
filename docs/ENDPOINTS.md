# Especificação de Endpoints — Loopin Sprint 2

Base URL local: `http://localhost:8080`. Todas as rotas privadas recebem `Authorization: Bearer <JWT>`. Formato JSON.

## Saúde e autenticação

| Método | Rota | Acesso | Payload / resultado |
|---|---|---|---|
| GET | `/health` | Público | Status da API e timestamp. |
| POST | `/auth/login` | Público | `{ "email": "lider@loopin.local", "senha": "senha" }`; retorna JWT e dados básicos. |
| POST | `/auth/bootstrap-lider` | Token inicial | Cabeçalho `X-Bootstrap-Token`; corpo com `nome`, `email`, `senha`. Funciona apenas com `APP_BOOTSTRAP_TOKEN` configurado e antes de existir um líder. |
| POST | `/usuarios` | Público | Registra operador com `nome`, `email`, `senha`. O perfil é sempre `OPERADOR`. |

Exemplo de resposta de login:

```json
{
  "token": "<jwt>",
  "id": "<id>",
  "nome": "Líder Loopin",
  "email": "lider@loopin.local",
  "perfil": "LIDER",
  "tipoToken": "Bearer"
}
```

## Usuários

| Método | Rota | Acesso | Resumo |
|---|---|---|---|
| GET | `/usuarios/me` | Autenticado | Conta atual, sem hash da senha. |
| GET | `/usuarios` | LIDER | Lista usuários em DTO seguro. |
| GET | `/usuarios/{id}` | LIDER | Consulta usuário sem senha. |
| POST | `/usuarios/administracao` | LIDER | Cria conta com `nome`, `email`, `senha`, `perfil` (`OPERADOR`, `GESTOR` ou `LIDER`). |
| DELETE | `/usuarios/{id}` | LIDER | Exclui usuário; não permite excluir a própria conta atual. |

## Estratégias

| Método | Rota | Acesso | Resumo |
|---|---|---|---|
| GET | `/estrategias` | OPERADOR, GESTOR, LIDER | Lista estratégias. |
| GET | `/estrategias/vigente` | OPERADOR, GESTOR, LIDER | Estratégia vigente. |
| GET | `/estrategias/{id}` | OPERADOR, GESTOR, LIDER | Consulta uma estratégia. |
| GET | `/estrategias/historico` | OPERADOR, GESTOR, LIDER | Lista histórico; aceita `?estrategiaId={id}`. |
| POST | `/estrategias` | LIDER | Cria estratégia e torna-a vigente. |
| PUT | `/estrategias/{id}` | LIDER | Atualiza data/categoria/campanha/descrição e registra histórico. |
| DELETE | `/estrategias/{id}` | LIDER | Exclui apenas estratégia não vigente, sem ideias/projetos vinculados, e registra histórico. |

Payload de estratégia:

```json
{
  "data": "2026-10-11",
  "categoria": "Eficiência operacional",
  "campanha": "Ciclo sustentável 2026",
  "descricao": "Reduzir desperdícios e ampliar a reutilização"
}
```

## Ideias

| Método | Rota | Acesso | Resumo |
|---|---|---|---|
| GET | `/ideias/minhas` | OPERADOR | Lista ideias próprias. |
| GET | `/ideias` | GESTOR, LIDER | Lista ideias para consulta/gestão. |
| GET | `/ideias/{id}` | Autenticado | Operador só consulta ideia própria. |
| POST | `/ideias` | OPERADOR | Cria ideia e atribui dono/estratégia vigente no servidor. |
| PUT | `/ideias/{id}` | Dono OPERADOR | Altera título/descrição enquanto pendente. |
| DELETE | `/ideias/{id}` | Dono OPERADOR | Exclui ideia própria pendente. |
| PATCH | `/ideias/{id}/avaliacao` | GESTOR | Define `status`, `prioridade`, `comentarioGestor`. |
| POST | `/ia/ideias/{id}/pontuar` | GESTOR | Retorna pontuação, prioridade sugerida e justificativa por IA. |

Payload de ideia:

```json
{
  "titulo": "Reutilização de embalagens retornáveis",
  "descricao": "Criar fluxo interno para reaproveitar materiais e registrar a redução de descarte."
}
```

Payload de avaliação:

```json
{
  "status": "APROVADA",
  "prioridade": "ALTA",
  "comentarioGestor": "Boa relação entre impacto esperado e viabilidade."
}
```

Status aceitos: `PENDENTE`, `EM_ANALISE`, `APROVADA`, `REJEITADA`. Prioridades: `BAIXA`, `MEDIA`, `ALTA`. A decisão de aprovação permanece humana; a IA só recomenda.

## Projetos e iniciativas

| Método | Rota | Acesso | Resumo |
|---|---|---|---|
| GET | `/projetos` | GESTOR, LIDER | Lista projetos. |
| GET | `/projetos/{id}` | GESTOR, LIDER | Consulta status, progresso e resultados. |
| POST | `/projetos` | GESTOR | Cria projeto associado à estratégia vigente. Ideia vinculada, se informada, deve estar aprovada. |
| PUT | `/projetos/{id}` | GESTOR | Atualiza nome, descrição, etapa, status, prazo, produtividade e progresso; os totais financeiros e os vínculos são preservados. |
| DELETE | `/projetos/{id}` | GESTOR | Exclui projeto. |
| GET | `/projetos/{id}/resultados` | GESTOR, LIDER | Lista resultados registrados. |
| POST | `/projetos/{id}/resultados` | GESTOR | Registra evento de resultado incremental. |

Payload de projeto:

```json
{
  "nome": "Otimização do ciclo de materiais",
  "descricao": "Implantar fluxo para reduzir perdas e registrar reaproveitamento.",
  "etapa": "PILOTO",
  "status": "EM_ANDAMENTO",
  "investimento": 12000.00,
  "prazo": "2026-12-31",
  "aumentoProdutividadePercentual": 0,
  "progressoPercentual": 20,
  "estrategiaId": "<id-da-estrategia-vigente>",
  "ideiaId": "<id-de-ideia-aprovada-ou-null>"
}
```

Para `PUT /projetos/{id}`, o corpo contém os dados operacionais; os totais financeiros e os vínculos com estratégia/ideia são preservados:

```json
{
  "nome": "Otimização do ciclo de materiais",
  "descricao": "Fluxo implantado e em medição.",
  "etapa": "IMPLANTACAO",
  "status": "EM_ANDAMENTO",
  "prazo": "2027-01-31",
  "aumentoProdutividadePercentual": 8.5,
  "progressoPercentual": 55
}
```

Status aceitos: `PLANEJADO`, `EM_ANDAMENTO`, `CONCLUIDO`, `SUSPENSO`, `CANCELADO`.

Payload de resultado incremental:

```json
{
  "data": "2026-11-15",
  "descricao": "Primeira medição do piloto",
  "investimentoAdicional": 1500.00,
  "lucroObtido": 4200.00,
  "aumentoProdutividadePercentual": 8.5,
  "progressoPercentual": 55
}
```

`investimentoAdicional` é somado ao investimento do projeto e `lucroObtido` do evento é somado ao lucro agregado. Produtividade/progresso informados passam a representar a medição atual. O PUT de projeto preserva os totais financeiros; use o endpoint de resultados para registrar novos valores.

## Dashboard e relatórios

| Método | Rota | Acesso | Resumo |
|---|---|---|---|
| GET | `/relatorios/dashboard` | LIDER | Resumo geral, ROI, investimento, lucro, prazo restante médio, produtividade e agrupamentos por estratégia/projeto. |
| GET | `/relatorios/dashboard?estrategiaId={id}` | LIDER | Resumo filtrado por estratégia. |
| GET | `/relatorios/dashboard?projetoId={id}` | LIDER | Resumo filtrado por projeto. Não combine os filtros. |

ROI: `lucroObtido / investimento × 100`; se investimento for zero, retorna `0.00`.

## Códigos HTTP esperados

- `200`: consulta/alteração realizada.
- `201`: recurso criado.
- `204`: exclusão realizada.
- `400`: validação ou regra de negócio não atendida.
- `401`: credencial/token ausente ou inválido.
- `403`: perfil sem permissão.
- `404`: recurso não encontrado.
- `409`: conflito, como e-mail duplicado ou exclusão da estratégia vigente.
- `502`: erro ou resposta inválida do provedor de IA.
- `503`: integração de IA ainda não configurada.
