# Loopin Backend — Sprint 2 FIAP

Backend REST da aplicação Loopin, adaptado aos requisitos de gestão de estratégias, ideias de inovação, projetos, relatórios e apoio à priorização com IA.

## Tecnologias

- Java 17+
- Spring Boot / Spring Web MVC
- Spring Security com JWT stateless
- BCrypt para hash de senha
- Spring Data MongoDB
- Bean Validation
- OpenRouter Chat Completions para pontuação assistida por IA
- Maven Wrapper

## Pré-requisitos

1. Java 17 ou superior.
2. MongoDB acessível local ou remotamente.
3. Uma chave OpenRouter para usar o endpoint de IA; os outros módulos funcionam sem ela.
4. Internet na primeira compilação, caso as dependências ainda não estejam no cache Maven.

## Configuração por variáveis de ambiente

| Variável | Padrão | Uso |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/loopin` | URI do MongoDB |
| `JWT_SECRET` | Obrigatória; sem padrão no código | Segredo Base64 que assina JWT. Configure fora do código em todos os ambientes. |
| `JWT_EXPIRATION_MS` | `3600000` | Validade do token em milissegundos |
| `APP_BOOTSTRAP_TOKEN` | vazio | Token de uso único para criar o primeiro líder |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Origens web liberadas para CORS |
| `OPENROUTER_API_KEY` | vazio | Chave da API de IA |
| `OPENROUTER_BASE_URL` | `https://openrouter.ai/api/v1` | URL do OpenRouter |
| `OPENROUTER_MODEL` | `openrouter/free` | Modelo/roteador para pontuar ideias |

O backend exige `JWT_SECRET` fornecida por variável de ambiente; não há mais um segredo padrão embutido. Gere uma chave Base64 aleatória de pelo menos 32 bytes para cada ambiente e nunca comite um segredo real de JWT, inicialização ou IA no código ou no ZIP.

### MongoDB local (opcional via Docker)

Se você tiver Docker instalado, pode iniciar uma instância local de desenvolvimento a partir da raiz do projeto:

```bash
docker compose up -d mongodb
```

O serviço publica a porta `27017` e usa um volume Docker para persistir os dados. Essa configuração é somente para desenvolvimento local; não exponha esse MongoDB sem autenticação à internet. Se você já tem MongoDB instalado ou usa Atlas, configure `MONGODB_URI` para a sua instância.

### Windows / PowerShell

Na raiz do projeto, configure os valores de desenvolvimento (ajuste a URI se o MongoDB não estiver local):

```powershell
$env:MONGODB_URI = "mongodb://localhost:27017/loopin"
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$env:APP_BOOTSTRAP_TOKEN = "troque-por-um-token-de-inicializacao-longo"
$env:OPENROUTER_API_KEY = "sua-chave-openrouter"
$env:OPENROUTER_MODEL = "openrouter/free"
.\mvnw.cmd spring-boot:run
```

No IntelliJ IDEA, abra a pasta como projeto Maven, selecione JDK 17+ em **Project Structure** e informe as variáveis de ambiente na configuração de execução de `LoopinBackendApplication`.

## Primeiro acesso

O cadastro público sempre cria um usuário `OPERADOR`; o perfil informado pelo cliente é ignorado. Para cadastrar a liderança inicial:

1. Inicie a aplicação com `APP_BOOTSTRAP_TOKEN` configurado.
2. Chame `POST /auth/bootstrap-lider` com o mesmo valor no cabeçalho `X-Bootstrap-Token`.
3. Faça login em `POST /auth/login` com a conta criada.
4. Cadastre uma estratégia em `POST /estrategias`. A nova estratégia se torna vigente e a anterior, se existir, vai para o histórico.
5. Crie gestores em `POST /usuarios/administracao` usando o JWT do líder.
6. Operadores podem se cadastrar em `POST /usuarios`.

O bootstrap só funciona quando `APP_BOOTSTRAP_TOKEN` está definido e ainda não existe líder cadastrado. Depois do primeiro líder, a rota não cria outra conta de liderança.

### Exemplo: cadastrar o líder inicial

`POST http://localhost:8080/auth/bootstrap-lider`

Cabeçalhos:

```http
Content-Type: application/json
X-Bootstrap-Token: troque-por-um-token-de-inicializacao-longo
```

Corpo:

```json
{
  "nome": "Líder Loopin",
  "email": "lider@loopin.local",
  "senha": "troque-esta-senha"
}
```

### Exemplo: login

`POST http://localhost:8080/auth/login`

```json
{
  "email": "lider@loopin.local",
  "senha": "troque-esta-senha"
}
```

A resposta traz `token`, `id`, `nome`, `email`, `perfil` e `tipoToken`. Em rotas privadas, envie `Authorization: Bearer <token>`.

## Regras de acesso

- **OPERADOR:** consulta estratégias; cadastra e consulta suas próprias ideias; edita ou exclui somente ideias próprias ainda pendentes.
- **GESTOR:** consulta e avalia ideias; prioriza/aprova/rejeita; gerencia projetos; registra resultados; executa pontuação assistida por IA.
- **LIDER:** gerencia estratégias e contas de usuários; consulta projetos e histórico de estratégias; visualiza relatórios/dashboard.
- Todos os perfis autenticados podem consultar estratégias.

O perfil é lido pelo backend a partir da conta associada ao JWT. O cliente não pode escolher seu próprio nível de acesso.

## Endpoints

A especificação completa com métodos, perfis, exemplos de payload e respostas está em [`docs/ENDPOINTS.md`](docs/ENDPOINTS.md).

| Módulo | Rotas |
|---|---|
| Saúde | `GET /health` |
| Autenticação | `/auth` |
| Usuários | `/usuarios` |
| Estratégias e histórico | `/estrategias` |
| Ideias | `/ideias` |
| Projetos e resultados | `/projetos` |
| Dashboard | `/relatorios/dashboard` |
| IA | `/ia/ideias/{id}/pontuar` |

## Regras de negócio e métricas

- Existe uma estratégia vigente por vez. Criar outra encerra a anterior e registra o evento no histórico. Estratégias com ideias/projetos vinculados não podem ser excluídas, preservando referências históricas.
- Ideias novas são vinculadas automaticamente à estratégia vigente; o cliente não informa livremente o dono, status ou prioridade inicial.
- Um projeto deve referenciar a estratégia vigente. Se for vinculado a uma ideia, ela deve estar aprovada.
- Resultados são eventos incrementais anexados ao projeto: `investimentoAdicional` e `lucroObtido` do evento são somados aos totais do projeto. O PUT de projeto preserva os totais financeiros; use o endpoint de resultados para lançar novas medições.
- O ROI do dashboard é calculado como `lucro obtido / investimento total × 100`. Investimento zero retorna ROI `0.00` para evitar divisão por zero.
- A pontuação da IA é uma recomendação; aprovação e decisão final continuam com o gestor.

## Integração com IA

`POST /ia/ideias/{id}/pontuar` chama a API OpenRouter no formato Chat Completions, solicita pontuação de 0 a 100, prioridade sugerida e justificativa, salva a recomendação na ideia e a devolve na resposta. O padrão `openrouter/free` roteia para um modelo gratuito disponível no OpenRouter. A disponibilidade e os limites podem variar; configure `OPENROUTER_MODEL` sem recompilar.

Sem `OPENROUTER_API_KEY`, o endpoint retorna HTTP 503 com instruções de configuração; os outros módulos continuam funcionando. Nunca coloque a chave no código-fonte.

## Testes e build

Windows:

```powershell
.\mvnw.cmd test
.\mvnw.cmd -DskipTests package
```

Linux/macOS:

```bash
./mvnw test
./mvnw -DskipTests package
```

O teste de inicialização original é básico. Antes da apresentação, execute testes de integração com MongoDB e teste autenticação/permissões com os três perfis.
