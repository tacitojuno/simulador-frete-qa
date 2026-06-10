# language: pt
Funcionalidade: Simulação de Frete
  Como um usuário do sistema de entregas
  Eu quero simular o frete de uma encomenda
  Para saber o custo e o prazo antes de confirmar o envio

  Cenário: CT001 - Calcular frete econômico com sucesso
    Dado que eu tenho um usuário autenticado no sistema
    Quando eu solicito uma simulação para um item "COMUM" com peso de 10,0 kg
    E a origem é "Campina Grande" e o destino é "Recife"
    E a modalidade escolhida é "ECONOMICO"
    Então a simulação deve ser criada com sucesso
    E o custo total do frete deve ser 50,0
    E o prazo estimado deve ser de 6 dias

  Cenário: CT002 - Simulação com Item Perigoso e frete Expresso
    Dado que eu tenho um usuário autenticado no sistema
    Quando eu solicito uma simulação para um item "PERIGOSO" com peso de 10,0 kg
    E a origem é "Campina Grande" e o destino é "João Pessoa"
    E a modalidade escolhida é "EXPRESSO"
    Então a simulação deve ser criada com sucesso
    E o custo total do frete deve ser 77,025
    E o prazo estimado deve ser de 3 dias

  Cenário: CT003 - Impedir simulação com peso negativo
    Dado que eu tenho um usuário autenticado no sistema
    Quando eu solicito uma simulação para um item "COMUM" com peso de -5,0 kg
    E a origem é "Campina Grande" e o destino é "Recife"
    E a modalidade escolhida é "ECONOMICO"
    Então o sistema deve falhar com a mensagem de erro "Erro: O peso deve ser maior que zero"

  Cenário: CT004 - Impedir simulação para cidade não atendida
    Dado que eu tenho um usuário autenticado no sistema
    Quando eu solicito uma simulação para um item "COMUM" com peso de 10,0 kg
    E a origem é "Campina Grande" e o destino é "Gotham City"
    E a modalidade escolhida é "ECONOMICO"
    Então o sistema deve falhar com a mensagem de erro "Rota não atendida pela transportadora."