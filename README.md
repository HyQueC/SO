# SO

Classes utilizadas SO, Escalonador e BCP.

Escalonador vai cuidar da inserção e remoção dos processos nas filas de pronto e bloqueado (funções originalmente atribuídas ao Dispachador), além da seleção do próximo processo a ser executado.

SO vai cuidar do resto;

BCP vai representar o processo a ser carregado e armazenado ao longo do algoritmo

Pontos a ressaltar:
        Há 4 meios (2 Conjuntos) para se obter os valores das médias, todos com resultados diferentes,
        
              Utilizando o Quantum *reservado* para cada processo no cálculo da média de intruções
              OU
              Utilizando o Quantum *usado* para cada processo no mesmo cálculo
              
              E
              
              Utilizando o critério de incremento do Quantum reservado ao processo para cada vez que ele "perde a CPU", ou seja, perde crédito. (E/S - Fim do Quantum)
              OU
              Utilizando o critério de duplicação do Quantum reservado a processo para o mesmo evento.
              
              
        Até o momento, somente as combinações utilizando o Quantum usado apresentaram um ponto de encontro nos gráficos, gerados a partir das médias envolvidas com o Quantum Base no eixo das abscissas (Número de instruções a serem executadas por Quantum).
              
