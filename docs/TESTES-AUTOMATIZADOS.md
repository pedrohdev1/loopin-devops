# Testes automatizados da API

A suíte valida as APIs do Loopin com cenários BDD em Gherkin, chamadas HTTP com Rest Assured e contratos JSON Schema. A lista de rotas está em [ENDPOINTS.md](ENDPOINTS.md).

## Pré-requisitos

- Java 17.
- Docker em execução.
- Porta local `27017` livre para o MongoDB.
- Acesso à internet na primeira execução, para baixar dependências do Maven e a imagem do MongoDB.

Não é necessário iniciar o backend manualmente: os testes Spring iniciam a aplicação em uma porta aleatória.

## Executar localmente

Inicie um MongoDB 7 isolado na porta esperada pelo perfil de testes:

```sh
docker run --rm -d --name loopin-test-mongo -p 27017:27017 mongo:7.0
```

No Windows PowerShell:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress test
```

No Linux ou macOS:

```sh
./mvnw --batch-mode --no-transfer-progress test
```

Ao terminar, encerre o container:

```sh
docker stop loopin-test-mongo
```

O comando Maven retorna código diferente de zero quando algum teste falha. Os relatórios ficam em `target/surefire-reports/`.

## Isolamento e integrações

Os testes ativam o perfil Spring `test`, configurado em `src/test/resources/application-test.yaml`, e usam somente o banco `loopin_test` em `127.0.0.1:27017`. Antes de cada cenário Cucumber, as coleções desse banco de teste são limpas. Não altere a URI para apontar a uma base com dados que devam ser preservados.

Os cenários que exercitam a integração de IA usam um servidor HTTP local de teste. Não é necessária chave da OpenRouter nem chamada a um serviço externo.

## GitHub Actions

O workflow **Loopin CI/CD** executa os testes em Java 17 com MongoDB 7 para pushes em `main`, pull requests direcionados a `main` e execução manual. O passo de teste usa:

```sh
bash ./mvnw --batch-mode --no-transfer-progress test
```

O workflow publica o artefato `loopin-test-reports` por 7 dias. Para consultá-lo, abra a execução em **Actions > Loopin CI/CD** e baixe o artefato. Em pushes para `main`, os jobs de deploy existentes também podem executar depois dos testes quando a variável `ENABLE_DEPLOY` estiver habilitada.
