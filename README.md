# Mordor — entregas 1 e 2

**Autor:** Breno Luiz Silva do Carmo

**Turma:** 6º período de Engenharia

**Professor:** Fabrício Barros

Reconhecedor de Mordor em Java e ANTLR 4, conforme as seções 23.1 e 23.2 do documento
*Trabalho Prático Final de Mordor — Língua Negra de Mordor com paralelo Arduino*.
A LUMA foi consultada como referência didática; as regras de Mordor foram escritas para este projeto.

O programa transforma fonte UTF-8 em tokens e reconhece programas completos, exportando a árvore
sintática concreta (CST). **Não verifica semântica, não executa Mordor nem gera Arduino C++.**
Por exemplo, `ashuk valor = 256;` passa por estas duas fases: a faixa é assunto da entrega 4.

## Compilar e testar

Requisitos: Java 21 com compilador e Maven 3.9+. Python 3 é necessário somente para regenerar
o relatório de evidências. A primeira compilação precisa de acesso ao Maven Central.

Na raiz deste repositório:

```sh
mvn clean verify
java -jar target/mordor-compiler-0.2.0.jar --tokens exemplos/validos/04_recursos.mordor
java -jar target/mordor-compiler-0.2.0.jar --parse exemplos/validos/03_controle.mordor
java -jar target/mordor-compiler-0.2.0.jar --tree exemplos/validos/02_funcoes_float.mordor
python3 scripts/gerar-evidencias.py
```

`mvn generate-sources` gera os analisadores em `target/generated-sources/antlr4`; `mvn test` executa
os testes; `mvn package` produz o JAR com o runtime ANTLR incluído. A entrada é um caminho de arquivo,
não um recurso fixo embutido no programa.

Saída: 0 para reconhecimento bem-sucedido; 1 para erro léxico/sintático; 2 para uso incorreto, falha
de leitura ou UTF-8 inválido. Os diagnósticos vão para stderr e incluem arquivo, linha, coluna,
fase e causa. As posições da CLI começam em 1; colunas contam pontos de código Unicode do fonte
original, incluindo marcas combinantes, e cada tabulação conta uma posição. Mensagens sintáticas
internas do ANTLR são mantidas em inglês. Erros léxicos impedem a execução do parser.

## Material das entregas

- [Especificação léxica](docs/entrega-1.md) e [tabela dos 59 tokens reservados](docs/palavras-reservadas.md).
- [Especificação sintática, regras e precedência](docs/entrega-2.md).
- [Guia de inspeção e capturas no ANTLR Lab](docs/antlr-lab.md).
- [Resultados observados](docs/evidencias/resultados.md), com tokens, árvores dos sete programas válidos
  e diagnósticos dos vinte erros léxicos e vinte e quatro erros sintáticos.
- Testes automatizados em `src/test/java/br/edu/mordor/RecognitionTest.java`.

As evidências locais foram geradas pelo executável. **As capturas reais do ANTLR Lab ainda precisam
ser feitas seguindo o guia**; não são substituídas pelas árvores textuais. Veja também as decisões
e limites de escopo nas duas especificações. Não há requisito de placa física para estas entregas.

Para preparar um ZIP de fontes, documentação, evidências e executável (incluindo eventuais capturas
que você adicionar), execute após compilar e gerar as evidências:

```sh
python3 scripts/preparar-lab.py
python3 scripts/empacotar.py
```

O pacote fica em `target/mordor-entrega-2.zip`. Antes de submeter, acrescente as capturas do Lab
e empacote novamente. O material local foi validado com 63 testes JUnit sem falhas e os 51 arquivos
de entrada exercitados pela CLI. A versão combinada para inspeção também foi gerada e compilada
localmente, produzindo as mesmas sete árvores; isso não valida a interface web do Lab.

## Estrutura

```text
src/main/antlr4/br/edu/mordor/   MordorLexer.g4 e MordorParser.g4
src/main/java/br/edu/mordor/     Frontend e interface de linha de comando
src/test/java/br/edu/mordor/     testes de reconhecimento e regressão
exemplos/                      entradas válidas e erros isolados
docs/                          relatório, evidências e instruções do Lab
scripts/                       geração reproduzível das evidências
```

O `pom.xml` fixa ANTLR 4.13.2 e os plugins de compilação e teste. Usa `source/target 21` porque
o Java reduzido do ambiente de trabalho não inclui o arquivo `ct.sym` necessário para `--release`.
Em uma instalação comum de JDK 21, os mesmos comandos funcionam.

Em redes com proxy, configure o proxy nas configurações locais do Maven (`settings.xml`);
Maven não utiliza automaticamente `HTTPS_PROXY`. Preserve a verificação TLS e não salve
credenciais no repositório.
