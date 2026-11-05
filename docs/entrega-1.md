# Entrega 1 — especificação e análise léxica

Breno Luiz Silva do Carmo · 6º período de Engenharia · Professor Fabrício Barros

## Contrato

Fonte: PDF do trabalho, seções 5, 5.1, 5.2, 6.1, 21, 23.1 e 26.1. A gramática é
`src/main/antlr4/br/edu/mordor/MordorLexer.g4`. O Maven gera o lexer Java; `Frontend` conduz
a análise e transforma tokens de erro em diagnósticos. As regras não são copiadas da LUMA.

O arquivo deve ser UTF-8 válido. O decoder rejeita bytes inválidos, em vez de substituir caracteres.
Não há normalização do arquivo inteiro. Palavras e identificadores são comparados em NFC;
strings mantêm os pontos de código originais. As três palavras acentuadas aceitam explicitamente
suas formas NFC e NFD na gramática portátil: `lûg`, `durbatulûk` e `búrz`.
O front-end normaliza o texto dos tokens de palavra/identificador, preservando os índices originais.
Assim, um nome decomposto e seu equivalente composto têm o mesmo texto de token.

`lug`, `durbatuluk` e `burz` são IDs, nunca aliases de palavra reservada. `burz` não é LOW nem VOID;
`durb_burz` é VOID. O parser rejeita um alias quando a posição exigir uma palavra reservada.
As gramáticas não contêm ações Java, permitindo carregamento em outras ferramentas ANTLR.

## Tokens

| Categoria | Regra |
|---|---|
| Palavras | As [59 entradas normativas](palavras-reservadas.md), antes de ID; `FLOAT` é `ashum_nazg` |
| Identificador | Começa com letra Unicode ou `_`; continua com letras, marcas, algarismos Unicode ou `_` |
| Reservado à implementação | Prefixo `__` gera diagnóstico léxico; não é nome de usuário |
| Decimal | `0` ou `[1-9][0-9]*` |
| Binário | `0b[01]+`, prefixo minúsculo |
| Hexadecimal | `0x[0-9a-fA-F]+`, prefixo minúsculo |
| Float | Parte inteira decimal válida seguida de `.` e dígitos (expoente opcional), ou de expoente obrigatório |
| Expoente | `[eE][+-]?[0-9]+` |
| String | Aspas duplas, sem quebra física; escapes `\n`, `\r`, `\t`, `\"`, `\\` |
| Operadores | `+ - * / % = == != < <= > >=`, além das palavras lógicas e operações por bit |
| Delimitadores | `: ; , ( ) { } [ ]` |
| Comentários | `//` até fim de linha; `/* ... */` até primeiro fechamento, sem aninhamento |
| Espaços | Espaço, tabulação, CR e LF; descartados junto aos comentários |

Os sinais de um número negativo são tokens separados. `-2.5` é MINUS e FLOAT_LITERAL;
`2.5e-2` é um único FLOAT_LITERAL. Não há caracteres entre aspas simples, sufixos `f`, ponto
flutuante hexadecimal, ponto de acesso a campos, `&&` ou `||`. O marcador `ishi` é IN.

## Candidatos inválidos e prioridade

ANTLR escolhe a maior correspondência; em empate, vence a primeira regra. As regras numéricas
válidas aparecem antes de INVALID_NUMBER, que captura um candidato iniciado por dígito (ou
ponto seguido de dígito), com letras, marcas, números, sublinhados, pontos e sinal de expoente.
Com isso, `0b102`, `0xFG`, `007`, `1e`, `1.2.3`, `1_000` e `2contador` são rejeitados inteiros,
sem fragmentação que esconda o defeito. Pontuação de expressão encerra o candidato.

Strings válidas têm prioridade sobre INVALID_STRING. As regras especiais detectam escape não
permitido, falta de fechamento, comentário não fechado e identificador reservado. ERROR_CHAR
captura os demais caracteres, como `@`. Esses tokens são uma estratégia explícita de diagnóstico:
um lexer ANTLR isolado os emite, mas é o front-end que declara a falha e retorna código 1.
No Lab, observar os tipos INVALID_* / ERROR_CHAR, pois a interface pode apenas exibi-los como tokens.

Comentários não afetam strings e aspas em comentários não iniciam strings. A linha e coluna se
referem ao início do token ou candidato defeituoso. O EOF também é listado na saída de tokens.

## Reprodução e verificação

Na raiz: `mvn clean verify`; depois `java -jar target/mordor-compiler-0.2.0.jar --tokens arquivo.mordor`.
Exemplo de começo da sequência do programa mínimo:

```text
1:1  HARDWARE  "lûg"
1:5  IN        "ishi"
1:10 LBRACE    "{"
1:11 RBRACE    "}"
```

As sequências completas dos sete programas, com posições, estão em `docs/evidencias/validos/`.
Os vinte testes de erro isolam candidatos, strings, comentário, caractere inválido e nomes reservados.
Os testes JUnit também cobrem as 59 palavras em NFC/NFD, prefixos de identificador, aliases,
preservação de strings, escapes e posições. Os objetivos, resultados esperados e observados e
primeiras posições constam no [relatório gerado](evidencias/resultados.md).
