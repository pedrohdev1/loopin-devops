# language: pt
Funcionalidade: Gestão de ideias ESG e apoio à avaliação
  Como operador ou gestor
  Quero registrar, consultar e avaliar ideias
  Para acompanhar propostas de melhoria sustentável com rastreabilidade

  Cenário: Criar, atualizar, avaliar e excluir ideias
    Dado operador, gestor e estratégia vigente preparados para testar ideias
    Quando tento cadastrar uma ideia sem título
    Então a validação da ideia responde com erro estruturado
    Quando crio uma ideia para avaliação
    Então a ideia é criada conforme o contrato
    Quando consulto minhas ideias
    Então minhas ideias seguem o contrato e incluem a nova ideia
    Quando consulto a ideia pelo identificador
    Então a consulta da ideia retorna seu contrato
    Quando atualizo a ideia ainda pendente
    Então a ideia atualizada preserva seu estado pendente
    Quando o gestor lista ideias para avaliação
    Então a lista de gestão contém a ideia
    Quando o gestor envia uma avaliação com status inválido
    Então a avaliação inválida responde com erro estruturado
    Quando o gestor aprova a ideia com prioridade alta
    Então a avaliação atualizada segue o contrato
    Quando o gestor solicita pontuação auxiliar da ideia
    Então a recomendação de IA respeita o contrato JSON
    Quando crio uma segunda ideia pendente
    Então a segunda ideia é criada
    Quando excluo a ideia que continua pendente
    Então a exclusão da ideia retorna sem conteúdo
    Quando consulto a segunda ideia após excluí-la
    Então a ideia excluída responde com não encontrado estruturado
