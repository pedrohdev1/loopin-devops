# Loopin Android — Sprint 2 FIAP

Aplicativo Android nativo em Kotlin + Jetpack Compose, conectado ao backend Spring Boot/MongoDB do Loopin. Esta é a primeira base funcional do front-end: autenticação real, navegação por perfil e telas conectadas às APIs de ideias, estratégias, projetos, relatórios e usuários.

## Tecnologia escolhida

- Kotlin e Jetpack Compose + Material 3.
- Uma aplicação Android nativa (não é um site dentro de WebView).
- Retrofit + OkHttp para consumir a API REST.
- Preferences DataStore para persistir a sessão entre aberturas do aplicativo.
- Organização inicial em `data/` (modelos, cliente API, sessão e repositório) e `ui/` (telas e tema).

## Abrir no Android Studio

1. Extraia esta pasta do ZIP.
2. Abra o Android Studio e selecione **Open**.
3. Escolha a pasta `Loopin-Android` — a pasta que contém `settings.gradle.kts`.
4. Aguarde a sincronização do Gradle. Na primeira vez, o Android Studio poderá precisar baixar Gradle e dependências; mantenha a conexão com a internet disponível.
5. Instale o Android SDK Platform 36 pelo SDK Manager se for solicitado.
6. Inicie o backend Loopin no computador e confirme que `http://localhost:8080/health` responde `status: UP`.
7. Crie/inicie um emulador Android no Device Manager e aperte **Run**.

O projeto foi preparado para usar JDK 17+ e Gradle 8.14.3. No Android Studio, prefira o JDK embutido indicado pela própria instalação, se estiver disponível.

## Configurar a URL do backend

Por padrão, `app/build.gradle.kts` usa `http://10.0.2.2:8080/`, que é o endereço do computador host visto pelo Android Emulator oficial. A documentação oficial do Android explica esse endereço: https://developer.android.com/studio/run/emulator-networking-address.

Para alterar a URL, adicione esta linha ao arquivo `local.properties` na raiz (preserve a linha `sdk.dir` que o Android Studio colocar ali):

```properties
loopin.apiBaseUrl=http://10.0.2.2:8080/
```

Em um aparelho físico, substitua por um endereço LAN acessível pelo celular, por exemplo `http://192.168.0.10:8080/` (use o IP real do seu computador). O celular e o computador precisam estar na mesma rede e o firewall deve permitir a conexão.

**Importante:** o HTTP sem TLS e `usesCleartextTraffic="true"` são apenas para desenvolvimento local. Antes de publicar ou distribuir fora da atividade, use HTTPS e desative o tráfego HTTP não criptografado.

## Backend necessário

O aplicativo espera o backend da Sprint 2 rodando na porta 8080 e com a configuração MongoDB corrigida. Ele consome diretamente os contratos documentados no backend:

- `POST /auth/login`, `POST /usuarios`
- `GET /estrategias`, `GET /estrategias/vigente`, `GET /estrategias/historico`, `POST/PUT /estrategias`
- `GET /ideias/minhas`, `GET /ideias`, `POST /ideias`, `PATCH /ideias/{id}/avaliacao`, `POST /ia/ideias/{id}/pontuar`
- `GET/POST /projetos`, `POST /projetos/{id}/resultados`
- `GET /relatorios/dashboard`
- `GET /usuarios`, `POST /usuarios/administracao`

O app **não** contém chaves de IA nem credenciais privilegiadas. O recurso de IA é chamado pelo backend; a chave do OpenRouter deve permanecer somente nas variáveis de ambiente do backend.

## Funcionalidades conectadas nesta base

- Login real e cadastro público de operador.
- Persistência do JWT e encerramento de sessão.
- Navegação e opções de tela específicas para `OPERADOR`, `GESTOR` e `LIDER`.
- Início com estratégia vigente e resumos conforme o perfil.
- Operador: lista as próprias ideias e cadastra uma nova.
- Gestor: consulta ideias, solicita pontuação da IA, aprova/rejeita ideias, consulta projetos, cria projeto vinculado à estratégia vigente/ideia aprovada e registra resultados financeiros/progresso.
- Líder: dashboard real, consulta/criação/edição de estratégias com histórico e administração de usuários.
- Mensagens de carregamento, erros HTTP e listas vazias.

O front esconde as opções fora do perfil, mas o controle de acesso real continua no backend. A sessão local usa DataStore no sandbox privado do aplicativo; para produção, avalie armazenamento criptográfico de tokens, HTTPS e regras de expiração/renovação.

## Build e APK

No Windows, com uma instalação de Java/JDK válida e conexão com a internet:

```powershell
.\gradlew.bat assembleDebug
```

O APK de debug, se o build passar, será criado em `app/build/outputs/apk/debug/app-debug.apk`.

Também pode usar Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

## Limitações desta entrega inicial

- Não consegui compilar este projeto Android neste ambiente, porque ele não tem Android SDK/Gradle instalado e a rede do ambiente não permite baixar dependências. Abra no Android Studio para realizar a sincronização e o primeiro build.
- O contrato dos endpoints foi conferido contra a documentação e os controladores do ZIP `Loopin-Backend-Sprint2-FIAP-final.zip`, mas a integração do front ainda deve ser testada no seu emulador com o backend ligado.
- Esta entrega é a base inicial funcional do front. A validação visual em tamanhos de tela diferentes, os testes de UI e a geração de APK ainda precisam ser feitos no Android Studio.
