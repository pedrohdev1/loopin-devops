# language: pt
Funcionalidade: Cadastro e administração de usuários
  Para proteger as contas e os dados pessoais
  Como responsável pela plataforma
  Quero validar cadastro e administração de usuários pela API

  Cenário: Cadastro público não permite elevar o próprio perfil
    Quando cadastro um usuário pela rota pública pedindo perfil de líder
    Então a API mantém o perfil público como "OPERADOR"

  Cenário: Rejeitar cadastro de e-mail duplicado
    Dado que já existe um operador cadastrado
    Quando tento cadastrar novamente o mesmo e-mail
    Então a API responde com conflito e erro estruturado

  Cenário: Bloquear consulta do perfil sem autenticação
    Quando consulto meu perfil sem token
    Então a API bloqueia a consulta com status 401 e erro de segurança

  Cenário: Líder administra contas sem expor senha
    Dado que existe um líder autenticado
    Quando o líder cria um usuário administrativo com perfil "GESTOR"
    Então a API retorna o usuário criado sem campos sensíveis
    Quando o líder lista os usuários
    Então a lista inclui o usuário criado sem expor senhas
    Quando o líder consulta o usuário criado
    Então a resposta corresponde ao contrato de usuário
    Quando o líder consulta o próprio perfil
    Então o próprio perfil segue o contrato e não contém senha
    Quando o líder exclui o usuário criado
    Então a API confirma a exclusão com status 204
    E a consulta posterior responde 404 com erro estruturado
