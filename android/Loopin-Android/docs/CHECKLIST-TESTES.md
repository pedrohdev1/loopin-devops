# Checklist de validação do Android

Marque os itens somente após execução real no Android Studio/emulador.

- [ ] Gradle Sync termina sem erro.
- [ ] `assembleDebug` termina com sucesso e gera `app-debug.apk`.
- [ ] Backend está acessível pelo endereço configurado no emulador.
- [ ] Login de OPERADOR abre a navegação de operador.
- [ ] Cadastro público cria somente OPERADOR e permite login posterior.
- [ ] Operador consulta apenas as próprias ideias e consegue cadastrar uma nova.
- [ ] Gestor consulta ideias, chama IA, aprova/rejeita e vê resposta de erro quando IA indisponível.
- [ ] Gestor cria projeto vinculado à estratégia vigente e, opcionalmente, a ideia aprovada.
- [ ] Gestor registra resultado e a lista reflete investimento/lucro/progresso atualizados.
- [ ] Líder visualiza indicadores do dashboard.
- [ ] Líder cria/edita estratégia e vê o histórico.
- [ ] Líder lista/cria usuários.
- [ ] Operador não vê menus administrativos e o backend continua recusando operações proibidas.
- [ ] Sair apaga a sessão local; voltar a abrir o app mostra login.
- [ ] Erros de rede e HTTP são compreensíveis para o usuário.
- [ ] Interface verificada em ao menos um emulador pequeno e um grande.

**Status inicial:** a integração do front foi codificada, mas os itens deste checklist ainda precisam ser executados no Android Studio do usuário. Não considere isso uma validação já feita.
