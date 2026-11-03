# language: pt
Funcionalidade: Relatórios ESG
  Como liderança
  Quero consultar indicadores gerais e filtrados
  Para acompanhar os resultados e o retorno dos projetos

  Cenário: Consultar dashboard geral, filtros e respostas de erro
    Dado um projeto com resultado está preparado para consultar o dashboard
    Quando consulto o dashboard geral
    Então o dashboard geral segue o contrato e resume o projeto
    Quando consulto o dashboard filtrado pela estratégia
    Então o dashboard filtrado pela estratégia contém o projeto
    Quando consulto o dashboard filtrado pelo projeto
    Então o dashboard filtrado pelo projeto contém apenas esse projeto
    Quando consulto o dashboard com filtros de estratégia e projeto ao mesmo tempo
    Então a API rejeita a combinação de filtros com erro estruturado
    Quando consulto o dashboard para um projeto inexistente
    Então o filtro inexistente retorna não encontrado estruturado
    Quando consulto o dashboard para uma estratégia inexistente
    Então o filtro de estratégia inexistente retorna não encontrado estruturado
