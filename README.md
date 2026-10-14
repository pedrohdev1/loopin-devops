# Projeto - Loopin ESG: Cidades ESG Inteligentes

Plataforma ESG para apoiar a gestão de estratégias, ideias de inovação, projetos sustentáveis e indicadores de resultados. Este repositório reúne o backend Java Spring, o aplicativo Android, a infraestrutura Docker e a automação CI/CD.

**Integrante(s):** preencher nome(s) completo(s) antes da entrega.

## Como executar localmente com Docker

### Pré-requisitos

- Docker Desktop com Docker Compose v2.
- Git para clonar o repositório.
- Porta local 8080 livre.

### 1. Preparar as variáveis de ambiente

Na raiz do projeto, copie o arquivo de exemplo:

~~~powershell
Copy-Item .env.example .env
~~~

Gere um segredo JWT local de 32 bytes e configure-o no arquivo .env. No PowerShell, pode gerar o valor com:

~~~powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$jwt = [Convert]::ToBase64String($bytes)
$rng.Dispose()
"JWT_SECRET=$jwt"
~~~

Copie o valor gerado para JWT_SECRET no arquivo .env. Substitua também APP_BOOTSTRAP_TOKEN por um valor local longo e aleatório. Não versione o arquivo .env, senhas, tokens ou chaves de API. O arquivo .env.example contém apenas valores ilustrativos.

### 2. Construir e iniciar os serviços

~~~powershell
docker compose up --build -d
docker compose ps
~~~

O Compose inicia dois serviços: a API Spring Boot (app) e o MongoDB (mongodb). O banco fica em uma rede Docker interna e seus dados são preservados no volume nomeado loopin-devops-mongodb-data. Por padrão, a API é publicada somente em 127.0.0.1:8080.

### 3. Verificar a aplicação

~~~powershell
Invoke-RestMethod http://localhost:8080/health
docker compose logs --tail=100 app
docker compose logs --tail=100 mongodb
~~~

A resposta de saúde esperada contém status UP. Para parar os serviços:

~~~powershell
docker compose down
~~~

Para apagar também o volume local de dados (ação destrutiva, somente para ambiente descartável):

~~~powershell
docker compose down --volumes
~~~

### Primeiro acesso

A aplicação possui o endpoint POST /auth/bootstrap-lider, protegido pelo cabeçalho X-Bootstrap-Token e pela variável APP_BOOTSTRAP_TOKEN. Ele só deve ser usado para criar a primeira conta de liderança, enquanto ainda não existir um líder. Consulte docs/ENDPOINTS.md para o contrato dos endpoints. Configure uma credencial exclusiva para cada ambiente e não compartilhe tokens reais.

## Pipeline CI/CD

Ferramenta: GitHub Actions. O workflow está em .github/workflows/ci-cd.yml.

- Disparadores: push para a branch main, pull request direcionado a main e execução manual (workflow_dispatch).
- Build e testes: prepara Java 17, inicia um MongoDB efêmero para os testes e executa o Maven Wrapper com os testes existentes.
- Imagem Docker: constrói a imagem versionada pelo SHA do commit.
- Teste de orquestração: inicia os serviços do docker-compose.yml usando a imagem construída, verifica o endpoint /health da API e executa um ping no MongoDB. O Compose é encerrado no final do job.
- Deploy de staging: em push para main, quando a variável ENABLE_DEPLOY é igual a true, chama o Deploy Hook do Render e espera o endpoint /health informar o SHA do commit enviado.
- Deploy de produção: só inicia após a verificação de staging passar; chama o Deploy Hook de produção e valida o SHA esperado no endpoint /health.

### Configurações de CI/CD no GitHub

Secrets do repositório (não são valores públicos):

- RENDER_STAGING_DEPLOY_HOOK_URL
- RENDER_PRODUCTION_DEPLOY_HOOK_URL

Variables do repositório:

- ENABLE_DEPLOY=true
- RENDER_STAGING_URL=https://loopin-staging-twj3.onrender.com
- RENDER_PRODUCTION_URL=https://loopin-production.onrender.com

Não coloque os valores dos Deploy Hooks em arquivos ou documentação. Eles permitem acionar implantações e devem permanecer secretos.

### Evidências do pipeline

- Execução #5, incluindo build, testes, smoke test do Docker Compose e deploy sequencial: https://github.com/pedrohdev1/loopin-devops/actions/runs/38500981672
- Execução #4, validação inicial do build, testes e deploys: https://github.com/pedrohdev1/loopin-devops/actions/runs/38499585504
- Workflow versionado: https://github.com/pedrohdev1/loopin-devops/blob/main/.github/workflows/ci-cd.yml

## Containerização

### Dockerfile

O Dockerfile utiliza build em múltiplas etapas. Primeiro compila o projeto com Maven e Java 17; depois usa uma imagem JRE de runtime, instala curl para health check, cria um usuário sem privilégios de root e executa o JAR. A imagem declara o endpoint de saúde e usa opções de JVM compatíveis com memória limitada.

Conteúdo atual do Dockerfile:

~~~dockerfile
# Build the Spring Boot application in a dedicated stage.
FROM maven:3.9.16-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress -DskipTests package

# Keep the runtime image smaller and run as a non-root user.
FROM eclipse-temurin:17-jre
RUN apt-get update \\
    && apt-get install -y --no-install-recommends curl \\
    && rm -rf /var/lib/apt/lists/* \\
    && useradd --system --uid 10001 --create-home appuser
WORKDIR /app
COPY --from=build /workspace/target/loopin-backend-0.0.1-SNAPSHOT.jar /app/app.jar
USER 10001:10001
EXPOSE 8080
ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=45.0 -XX:+UseSerialGC"
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \\
    CMD curl --fail --silent http://127.0.0.1:${PORT}/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
~~~

### Estratégias adotadas

- Multi-stage build para separar compilação e execução.
- Runtime baseado em JRE, sem ferramentas de compilação.
- Processo em usuário não-root.
- Health check HTTP da API.
- Configuração externa por variáveis de ambiente.
- Compose com rede bridge dedicada, MongoDB sem porta publicada para o host, volume persistente e dependência condicionada à saúde do banco.
- .dockerignore para não copiar arquivos locais, segredos, artefatos de build ou o aplicativo Android para a imagem de backend.

### Comandos úteis

~~~powershell
docker build -t loopin-backend:local .
docker compose up --build -d
docker compose ps
docker compose logs -f app
docker compose down
~~~

## Prints do funcionamento

O diretório docs/evidencias contém capturas reais do painel do Render e dos logs de conexão ao MongoDB Atlas registrados durante a validação inicial. Para a validação mais recente do workflow, consulte a execução #5 do GitHub Actions, que mostra os resultados de build, testes, inicialização via Compose, staging e produção.

- Pipeline completo: https://github.com/pedrohdev1/loopin-devops/actions/runs/38500981672
- Staging: https://loopin-staging-twj3.onrender.com/health
- Produção: https://loopin-production.onrender.com/health

Os serviços gratuitos do Render podem entrar em repouso após inatividade; a primeira chamada seguinte pode levar mais tempo. O endereço /health comprova a disponibilidade básica do backend e a versão implantada. A conexão com o MongoDB foi verificada também nos logs do serviço.

## Tecnologias utilizadas

- Java 17.
- Spring Boot, Spring Web MVC, Spring Security, JWT e Bean Validation.
- Spring Data MongoDB e MongoDB.
- Maven Wrapper.
- Docker, Docker Compose e imagem Eclipse Temurin.
- GitHub e GitHub Actions.
- Render para os ambientes de staging e produção.
- MongoDB Atlas para o banco remoto.
- Kotlin, Jetpack Compose, Retrofit e Gradle no aplicativo Android.
- OpenRouter como integração opcional de pontuação assistida por IA.

## Checklist de entrega

- [x] Projeto compactado em ZIP com estrutura organizada (conferir pacote final).
- [x] Dockerfile funcional, imagem construída pelo pipeline.
- [x] docker-compose.yml com aplicação, banco, volume, variáveis de ambiente e rede.
- [x] Pipeline com build, testes automatizados e deploy sequencial em staging e produção.
- [ ] README com instruções e evidências (seção preparada; revisar o campo de integrantes antes de entregar).
- [ ] Documentação técnica com evidências (PDF ou PPT) anexada à entrega.
- [x] Deploy validado nos ambientes staging e produção pelo GitHub Actions.

O checklist deve ser atualizado para refletir a revisão final e anexado ao final do PDF ou do README, conforme a orientação da atividade.
