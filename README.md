# SO

Classes utilizadas SO, Escalonador e BCP.

Escalonador vai cuidar da inserção e remoção dos processos nas filas de pronto e bloqueado (funções originalmente atribuídas ao Dispachador), além da seleção do próximo processo a ser executado.

SO vai cuidar do resto;

BCP vai representar o processo a ser carregado e armazenado ao longo do algoritmo

Pontos faltando:
  - Organização dos métodos de inserção e remoção no Escalonador (falta alguns detalhes)
  - Gerencia da fila de bloqueados (contagem de quantos processos passaram pela execução para determinar quando liberar o processo bloqueado). (falta confirmar o funcionamento)
  - Filas de prontos devem referenciar a Tabela de Processos por índice e "não conter os processos (BCP) (Ignorado)
  - O set de status dos processos deve ser gerenciado pelo SO ou pelo Escalonador?
 
  - Print nos logFiles
  - Contagem das estatísticas pedidas pelo professor (Médias, etc) (fórmula elaborada apenas)
  - Definir os outros 10 valores de Quantum (quantas instruções 1Quantum equivale)
  - Formar 2 gráficos com as estatísticas pedidas pelo professor e o quantum definido.
  - Criar o relatório com o resultado desses valores a fim de determinar o quantum ideal.
