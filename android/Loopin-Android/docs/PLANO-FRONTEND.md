# Definição do Front-end Loopin

## Decisões aprovadas

- **Plataforma:** Android nativo.
- **Linguagem/UI:** Kotlin + Jetpack Compose + Material 3.
- **Arquitetura:** UI → repositório → Retrofit/OkHttp → API Spring Boot → MongoDB.
- **Sessão:** JWT em Preferences DataStore, com cabeçalho `Authorization: Bearer ...` aplicado pelo cliente HTTP.
- **URL local do emulador:** `http://10.0.2.2:8080/`; configurável por `loopin.apiBaseUrl` no `local.properties`.
- **Identidade visual:** verde floresta `#245B45`, verde ciclo `#A8C66C`, fundo natural `#F5F7EF`, texto `#24352C`, cartões brancos e bordas discretas.
- **Tom de interface:** claro, profissional, sustentável e objetivo. O app concentra dados úteis e evita telas densas.

## Navegação por perfil

### OPERADOR
- Início: estratégia vigente e resumo das próprias ideias.
- Minhas ideias: lista privada e cadastro de uma ideia.
- Estratégia: consulta da estratégia vigente e do histórico visível.
- Perfil: dados da sessão e sair.

### GESTOR
- Início: quantidade de ideias, pendências e projetos.
- Ideias: lista, pontuação IA, aprovação/rejeição e comentário.
- Projetos: lista, criação de projeto com estratégia vigente e ideia aprovada opcional, registro de resultado.
- Perfil: dados da sessão e sair.

### LIDER
- Dashboard: indicadores de projeto/ROI/investimento/lucro/progresso.
- Estratégias: consulta, criação, edição e histórico.
- Usuários: listagem e criação de contas com perfil autorizado.
- Perfil: dados da sessão e sair.

## Princípios de UX

1. Toda ação assíncrona deve ter estado de carregamento e mensagem de erro legível.
2. Formulários validam campos básicos antes de enviar; validação de negócio continua no backend.
3. Status e prioridades são apresentados como etiquetas, e progresso dos projetos como barra e percentual.
4. Pontuação da IA é uma recomendação; o gestor decide aprovar/rejeitar.
5. As opções variam por perfil para simplificar a navegação; não se usa a interface como única proteção de acesso.
6. Tokens e chaves não devem ser escritos em logs nem incluídos no código-fonte.

## Evolução prevista

- Completar detalhes/edição/exclusão de ideias e projetos onde a regra de negócio permitir.
- Melhorar filtros e pesquisa, confirmação antes de excluir/rejeitar e apresentação detalhada do histórico.
- Incluir paginação quando houver volume alto.
- Executar testes de UI para login, falhas de rede e fluxos de cada perfil.
- Usar HTTPS e armazenamento criptográfico de sessão antes de qualquer distribuição fora do ambiente local.
