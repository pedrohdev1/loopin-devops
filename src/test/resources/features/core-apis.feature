# language: pt
Funcionalidade: Saúde e estratégias ESG
  Como liderança da organização
  Quero consultar e administrar estratégias vigentes
  Para manter rastreável o direcionamento ESG da aplicação

  Cenário: Verificar saúde e ciclo de vida das estratégias
    Dado uma liderança autenticada para testar estratégias
    Quando consulto a saúde da API
    Então a saúde responde conforme o contrato
    Quando crio a estratégia inicial
    Então a estratégia inicial é criada
    Quando crio uma nova estratégia vigente
    Então a nova estratégia passa a ser vigente
    Quando consulto todas as estratégias
    Então a lista de estratégias segue o contrato
    Quando consulto a estratégia vigente
    Então a estratégia mais recente aparece como vigente
    Quando consulto a estratégia vigente pelo identificador
    Então a consulta da estratégia retorna seus dados
    Quando atualizo os dados da estratégia vigente
    Então a estratégia atualizada segue o contrato
    Quando consulto o histórico geral de estratégias
    Então o histórico geral contém o ciclo de vida registrado
    Quando consulto o histórico da estratégia vigente
    Então o histórico filtrado respeita o contrato
    Quando excluo uma estratégia que deixou de ser vigente
    Então a exclusão da estratégia antiga retorna sem conteúdo
    Quando consulto a estratégia antiga excluída
    Então a estratégia excluída responde com não encontrado estruturado
    Quando tento excluir a estratégia que continua vigente
    Então a API rejeita a exclusão da estratégia vigente por conflito
