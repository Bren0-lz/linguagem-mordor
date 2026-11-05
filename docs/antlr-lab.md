# Como inspecionar as árvores e fazer as capturas

Acesse [ANTLR Lab](https://lab.antlr.org/) em um navegador. A interface pode variar; procure
o editor de gramáticas, a entrada de teste e a visualização de árvore. As orientações abaixo
não são evidência de uma sessão executada; você deve fazer e registrar essa sessão.

## Preparar os arquivos

As gramáticas originais estão em `src/main/antlr4/br/edu/mordor/`. São portáteis, sem ações Java.
Se a interface permitir vários arquivos/gramáticas, carregue `MordorLexer.g4` e `MordorParser.g4`,
mantendo os nomes. Escolha `program` como regra inicial e cole um programa de `exemplos/validos`.

Se a interface oferecer somente um editor de gramática, use a versão combinada já disponível
em [lab/Mordor.g4](lab/Mordor.g4). No GitHub, abra esse arquivo e clique em **Raw** para copiar
todo o conteúdo. Para regenerá-lo após uma alteração nas gramáticas originais:

```sh
python3 scripts/preparar-lab.py
```

Cole o conteúdo de `docs/lab/Mordor.g4` (ou da cópia em `target/lab/Mordor.g4`) no editor. Esse arquivo é derivado automaticamente
das mesmas regras e não substitui as gramáticas separadas da entrega. A versão combinada remove
apenas os cabeçalhos e `tokenVocab`, reunindo parser e lexer. A regra inicial continua sendo `program`.

## Registrar a inspeção

1. Carregue as regras e aguarde a geração sem erros da gramática.
2. Cole `01_minimo.mordor` na entrada. Escolha `program`, execute e abra a árvore.
3. Verifique a raiz `program`, os três blocos e EOF. Registre uma captura mostrando entrada,
   regra escolhida e árvore, sem esconder eventuais diagnósticos.
4. Repita para os demais seis programas válidos. Use zoom, expanda os nós ou faça mais de uma
   captura quando a árvore não couber. Destaque ao menos uma expressão, uma função e os laços.
5. Carregue `exemplos/erros-sintaticos/02_sem_ishi.mordor` e registre o erro em `ash_durb {}`.
   O Lab pode usar colunas iniciadas em zero; a CLI do projeto usa colunas iniciadas em um.
6. Inspecione a tokenização de `0b102`: deve ser INVALID_NUMBER inteiro. Esses tokens são
   diagnosticados pelo front-end Java; a interface do Lab pode somente exibir o token.

Salve imagens PNG reais em `docs/capturas-lab/`, com nomes como `01_minimo.png`,
`02_funcoes_float.png` e `erro_sem_ishi.png`. Acrescente uma nota com a data, programa e
construções inspecionadas. Não renomeie árvores locais como capturas do Lab.

Para conferir as mesmas entradas localmente, use `--tree` ou consulte `docs/evidencias/validos`.
É normal que a aparência gráfica varie; as regras e a estrutura reconhecida devem corresponder.

As formas NFC/NFD das palavras acentuadas são reconhecidas pela gramática. O texto normalizado
de IDs é produzido no front-end Java: a interface do Lab pode exibir a grafia original decomposta.
Strings devem permanecer inalteradas em ambos. Não há análise semântica no Lab nem nesta entrega.
