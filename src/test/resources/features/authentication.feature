# language: pt
Funcionalidade: Autenticação de usuários
  Para acessar os recursos da Loopin
  Como usuário da plataforma
  Quero me autenticar com minhas credenciais

  Cenário: Registrar operador e autenticar com sucesso
    Quando cadastro um novo operador
    Então a API responde com usuário criado e perfil "OPERADOR"
    Quando faço login com as credenciais cadastradas
    Então recebo um token Bearer válido no formato esperado
    E consigo consultar meu próprio perfil autenticado

  Cenário: Recusar login com senha incorreta
    Dado um e-mail sem usuário cadastrado
    Quando tento autenticar com uma senha inválida
    Então a API responde com status 401
