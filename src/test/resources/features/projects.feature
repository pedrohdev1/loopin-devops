# language: pt
Funcionalidade: Projetos e resultados ESG
  Como gestor
  Quero acompanhar projetos e seus resultados mensuráveis
  Para verificar o progresso das iniciativas sustentáveis

  Cenário: Criar, consultar, atualizar e excluir um projeto com resultados
    Dado um gestor e uma estratégia vigente preparados para testar projetos
    Quando tento criar um projeto com status não suportado
    Então a criação do projeto inválido responde com erro estruturado
    Quando crio um projeto vinculado à estratégia vigente
    Então o projeto é criado conforme o contrato
    Quando consulto todos os projetos como gestor
    Então a lista de projetos segue o contrato
    Quando consulto o projeto pelo identificador
    Então a consulta do projeto retorna seus dados
    Quando atualizo o progresso operacional do projeto
    Então a atualização preserva os vínculos e ajusta o status
    Quando consulto os resultados iniciais do projeto
    Então a lista inicial de resultados está vazia e contratada
    Quando registro um resultado incremental do projeto
    Então o projeto retorna os totais incrementados
    Quando consulto os resultados registrados do projeto
    Então o resultado registrado segue o contrato
    Quando excluo o projeto concluído nos testes
    Então a exclusão do projeto retorna sem conteúdo
    Quando consulto o projeto após a exclusão
    Então o projeto excluído responde com não encontrado estruturado
