# Entrega 2 — especificação e análise sintática

Breno Luiz Silva do Carmo · 6º período de Engenharia · Professor Fabrício Barros

## Organização e regras

Fonte: PDF do trabalho, seções 4, 6–22, 23.2 e 26.2. A gramática separada
`MordorParser.g4` usa `tokenVocab=MordorLexer`, com regra inicial **program** e EOF obrigatório.
A separação torna explícitas as duas fases, como no projeto didático LUMA. Não existe AST nesta
entrega: a saída é a árvore concreta das regras ANTLR, com delimitadores e tokens.

| Regras | Papel |
|---|---|
| program | Exatamente `lûg`, globais, funções, `ash_durb`, `durbatulûk`, nessa ordem, até EOF |
| resourceDeclaration, pin | Recursos somente em `lûg`; pino/canal é numeral; inicializador `: expressão` opcional |
| globalDeclaration | Constante com inicializador ou variável com inicializador opcional |
| localDeclaration | Declaração com inicializador obrigatório; não existe `var` |
| integerType, elementType, dataType | Larguras como numerais, float, lógico, nível, duração/instante, texto, opcional e vetor fixo |
| functionDeclaration, parameter | `durb retorno nome(parâmetros) bloco`; parâmetros de dados ou capacidade |
| block | `ishi { declarações e comandos }`; pode ser vazio; não existe bloco solto como comando |
| assignment | Atribuição a ID ou a um elemento de vetor |
| action, call | `thrak` para impressão, escrita, espera e chamada usada como comando; chamadas em expressão não usam `thrak` |
| ifStatement | Condição comum ou `gimb expressão ul nome`, com alternativa opcional e bloco obrigatório |
| switchStatement | Um ou mais casos, cada um com bloco; padrão opcional, único e final |
| whileStatement, doStatement | Teste inicial ou final; o teste final exige `;` e não recebe outro `ishi` |
| forStatement | Duas separações `;` obrigatórias, três partes opcionais; atualização não permite declaração |
| repeatStatement | Contagem expressa seguida de bloco |
| statement | Também inclui inicialização serial, interrupção, continuação e retorno opcionalmente com valor |
| primaryExpression, postfixExpression | Literais, nomes, chamadas, agrupamento, lista não vazia, conversões, leituras e índices |
| bitExpression | Largura explícita, um argumento para complemento e dois para as outras operações |

O construtor `nazgum(n,d)` exige exatamente dois numerais; larguras, pinos e comprimentos também
não aceitam expressões. No `for`, inicialização aceita variável com valor, atribuição ou `thrak chamada()`;
atualização aceita atribuição ou `thrak chamada()`. Não há `++`, `--` ou lista de atualizações.
`ash_nazg numeral;` não usa `thrak`. A posição válida da inicialização serial em relação aos demais
comandos é uma restrição semântica posterior.

Texto é reconhecido como forma de tipo; as restrições de uso (apenas constante, sem retorno ou
parâmetro textual) ficam para a análise semântica. O mesmo vale para faixa de largura, comprimento,
tipos de retorno, capacidades dos recursos e constância dos inicializadores/casos. Isso evita
confundir erros semanticamente inválidos com os dez erros negativos mínimos de reconhecimento.

## Precedência e associatividade

Da maior para a menor:

1. Primárias, chamadas e acesso a vetor.
2. `agh_burz` e `-` unários, com recursão à direita.
3. `*`, `/`, `%`.
4. `+`, `-` binários.
5. `<`, `<=`, `>`, `>=`, `==`, `!=`, todos no mesmo nível e sem encadeamento.
6. `agh`.
7. `agh_ash`.

Cada nível está em uma regra própria. Repetições de binários representam associação à esquerda
(por exemplo, `8 - 3 - 1` deve ser interpretado como `(8 - 3) - 1` em uma futura AST).
As regras em camadas produzem listas de operandos na CST; sua transformação futura deverá
dobrar essas listas da esquerda para a direita. Parênteses substituem a precedência natural.
`a < b < c` é rejeitado sintaticamente; `a < b agh b < c` é reconhecido. Operações de bits são
primárias com sintaxe de chamada própria, não operadores infixos de C++.

## Programas completos de demonstração

| Programa em exemplos/validos | Cobertura principal |
|---|---|
| 01_minimo | Três blocos obrigatórios vazios |
| 02_funcoes_float | Constante textual, função, procedimento, parâmetros, retorno, float decimal/exponencial, conversão e vetor float |
| 03_controle | For, while, do/while, repetição contada, interrupção, continuação, seleção e vetor |
| 04_recursos | Cinco capacidades, pinos, ADC, PWM, leitura/escrita, condição, alternativa e presença opcional |
| 05_tipos_expressoes | Inteiros com larguras, bits, precedência, instante/duração, vetor em parâmetro e retorno |
| 06_unicode | Palavras e identificadores Unicode e preservação de texto decomposto |
| 07_for_opcionais | Cabeçalhos vazios e com chamadas, retorno antecipado e tipo opcional |

Os 24 erros sintáticos contêm tokens léxicos válidos e são testados como tal. Incluem ausência de
`ishi` em blocos principais, função, caso e laço, falta de separadores, precedência inválida,
declaração local incompleta, chamada sem `thrak`, construtor malformado e conteúdo após EOF.

As árvores e mensagens observadas estão em [evidencias](evidencias/resultados.md). Para regenerar:

```sh
mvn clean verify
python3 scripts/gerar-evidencias.py
```

As árvores `.arvore.txt` usam a notação parentizada de `toStringTree`: o primeiro item é o nome
da regra; seus itens são filhos. `(program ...)` é a raiz e `<EOF>` confirma consumo completo.
Os testes verificam também a estrutura interna da árvore de uma expressão com vários níveis.

## Evidência externa pendente

O PDF pede capturas da inspeção das árvores no ANTLR Lab. As árvores locais comprovam a
execução do parser, mas não são capturas do Lab. Seguir [o guia](antlr-lab.md), salvar as imagens
reais e incorporá-las à entrega antes da submissão. A documentação não declara essa parte concluída.
