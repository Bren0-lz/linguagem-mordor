# Resultados observados

Gerados por `python3 scripts/gerar-evidencias.py`. O processo falha se o resultado divergir do esperado.

| Arquivo / objetivo | Esperado | Observado | Primeira posição |
|---|---|---|---|
| `exemplos/validos/01_minimo.mordor` (01 minimo) | aceito | aceito; saída 0 | — |
| `exemplos/validos/02_funcoes_float.mordor` (02 funcoes float) | aceito | aceito; saída 0 | — |
| `exemplos/validos/03_controle.mordor` (03 controle) | aceito | aceito; saída 0 | — |
| `exemplos/validos/04_recursos.mordor` (04 recursos) | aceito | aceito; saída 0 | — |
| `exemplos/validos/05_tipos_expressoes.mordor` (05 tipos expressoes) | aceito | aceito; saída 0 | — |
| `exemplos/validos/06_unicode.mordor` (06 unicode) | aceito | aceito; saída 0 | — |
| `exemplos/validos/07_for_opcionais.mordor` (07 for opcionais) | aceito | aceito; saída 0 | — |
| `exemplos/erros-lexicos/01_binario.mordor` (01 binario) | léxico | léxico; saída 1 | 1:15 |
| `exemplos/erros-lexicos/02_hexadecimal.mordor` (02 hexadecimal) | léxico | léxico; saída 1 | 1:15 |
| `exemplos/erros-lexicos/03_prefixo_vazio.mordor` (03 prefixo vazio) | léxico | léxico; saída 1 | 1:15 |
| `exemplos/erros-lexicos/04_zero_inicial.mordor` (04 zero inicial) | léxico | léxico; saída 1 | 1:15 |
| `exemplos/erros-lexicos/05_pontos.mordor` (05 pontos) | léxico | léxico; saída 1 | 1:20 |
| `exemplos/erros-lexicos/06_numero_nome.mordor` (06 numero nome) | léxico | léxico; saída 1 | 1:7 |
| `exemplos/erros-lexicos/07_string_aberta.mordor` (07 string aberta) | léxico | léxico; saída 1 | 1:18 |
| `exemplos/erros-lexicos/08_escape.mordor` (08 escape) | léxico | léxico; saída 1 | 1:18 |
| `exemplos/erros-lexicos/09_comentario.mordor` (09 comentario) | léxico | léxico; saída 1 | 1:1 |
| `exemplos/erros-lexicos/10_caractere.mordor` (10 caractere) | léxico | léxico; saída 1 | 1:15 |
| `exemplos/erros-lexicos/11_expoente.mordor` (11 expoente) | léxico | léxico; saída 1 | 1:20 |
| `exemplos/erros-lexicos/12_sem_inteiro.mordor` (12 sem inteiro) | léxico | léxico; saída 1 | 1:20 |
| `exemplos/erros-lexicos/13_sem_fracao.mordor` (13 sem fracao) | léxico | léxico; saída 1 | 1:20 |
| `exemplos/erros-lexicos/14_prefixo_maiusculo.mordor` (14 prefixo maiusculo) | léxico | léxico; saída 1 | 1:15 |
| `exemplos/erros-lexicos/15_reservado.mordor` (15 reservado) | léxico | léxico; saída 1 | 1:7 |
| `exemplos/erros-lexicos/16_char.mordor` (16 char) | léxico | léxico; saída 1 | 1:15 |
| `exemplos/erros-lexicos/17_sufixo.mordor` (17 sufixo) | léxico | léxico; saída 1 | 1:20 |
| `exemplos/erros-lexicos/18_separador.mordor` (18 separador) | léxico | léxico; saída 1 | 1:15 |
| `exemplos/erros-lexicos/19_zero_float.mordor` (19 zero float) | léxico | léxico; saída 1 | 1:20 |
| `exemplos/erros-lexicos/20_quebra_string.mordor` (20 quebra string) | léxico | léxico; saída 1 | 1:18 |
| `exemplos/erros-sintaticos/01_sem_ponto_virgula.mordor` (01 sem ponto virgula) | sintático | sintático; saída 1 | 5:1 |
| `exemplos/erros-sintaticos/02_sem_ishi.mordor` (02 sem ishi) | sintático | sintático; saída 1 | 2:10 |
| `exemplos/erros-sintaticos/03_condicao_sem_bloco.mordor` (03 condicao sem bloco) | sintático | sintático; saída 1 | 4:14 |
| `exemplos/erros-sintaticos/04_for_incompleto.mordor` (04 for incompleto) | sintático | sintático; saída 1 | 4:21 |
| `exemplos/erros-sintaticos/05_do_sem_ponto_virgula.mordor` (05 do sem ponto virgula) | sintático | sintático; saída 1 | 5:1 |
| `exemplos/erros-sintaticos/06_break_com_parenteses.mordor` (06 break com parenteses) | sintático | sintático; saída 1 | 4:15 |
| `exemplos/erros-sintaticos/07_operando_ausente.mordor` (07 operando ausente) | sintático | sintático; saída 1 | 4:23 |
| `exemplos/erros-sintaticos/08_local_sem_valor.mordor` (08 local sem valor) | sintático | sintático; saída 1 | 4:16 |
| `exemplos/erros-sintaticos/09_thrak_atribuicao.mordor` (09 thrak atribuicao) | sintático | sintático; saída 1 | 4:17 |
| `exemplos/erros-sintaticos/10_parentese_aberto.mordor` (10 parentese aberto) | sintático | sintático; saída 1 | 4:25 |
| `exemplos/erros-sintaticos/11_sem_setup.mordor` (11 sem setup) | sintático | sintático; saída 1 | 2:1 |
| `exemplos/erros-sintaticos/12_comparacao_encadeada.mordor` (12 comparacao encadeada) | sintático | sintático; saída 1 | 4:27 |
| `exemplos/erros-sintaticos/13_apos_fim.mordor` (13 apos fim) | sintático | sintático; saída 1 | 4:1 |
| `exemplos/erros-sintaticos/14_funcao_sem_ishi.mordor` (14 funcao sem ishi) | sintático | sintático; saída 1 | 2:20 |
| `exemplos/erros-sintaticos/15_caso_sem_ishi.mordor` (15 caso sem ishi) | sintático | sintático; saída 1 | 4:32 |
| `exemplos/erros-sintaticos/16_laco_sem_ishi.mordor` (16 laco sem ishi) | sintático | sintático; saída 1 | 4:19 |
| `exemplos/erros-sintaticos/17_chamada_sem_thrak.mordor` (17 chamada sem thrak) | sintático | sintático; saída 1 | 4:12 |
| `exemplos/erros-sintaticos/18_duracao_expressao.mordor` (18 duracao expressao) | sintático | sintático; saída 1 | 4:29 |
| `exemplos/erros-sintaticos/19_bits_sem_largura.mordor` (19 bits sem largura) | sintático | sintático; saída 1 | 4:23 |
| `exemplos/erros-sintaticos/20_switch_sem_caso.mordor` (20 switch sem caso) | sintático | sintático; saída 1 | 4:21 |
| `exemplos/erros-sintaticos/21_alias_ascii.mordor` (21 alias ascii) | sintático | sintático; saída 1 | 1:1 |
| `exemplos/erros-sintaticos/22_bloco_solto.mordor` (22 bloco solto) | sintático | sintático; saída 1 | 4:5 |
| `exemplos/erros-sintaticos/23_for_declaracao_atualizacao.mordor` (23 for declaracao atualizacao) | sintático | sintático; saída 1 | 4:19 |
| `exemplos/erros-sintaticos/24_constante_sem_valor.mordor` (24 constante sem valor) | sintático | sintático; saída 1 | 2:19 |
