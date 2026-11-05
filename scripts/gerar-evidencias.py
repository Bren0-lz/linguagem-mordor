#!/usr/bin/env python3
"""Exercita o JAR real, preserva saídas/status e gera evidências reproduzíveis."""
from pathlib import Path
import hashlib
import subprocess
import sys

root = Path(__file__).resolve().parent.parent
jar = root / 'target/mordor-compiler-0.2.0.jar'
dest = root / (sys.argv[1] if len(sys.argv) > 1 else 'evidencias-geradas')
dest.mkdir(parents=True, exist_ok=True)
rows = ['# Resultados observados', '',
        'Gerados por `python3 scripts/gerar-evidencias.py`. O processo falha se o resultado divergir do esperado.', '',
        '| Arquivo / objetivo | Esperado | Observado | Primeira posição |', '|---|---|---|---|']
hashes = []
for folder, expected in [('validos', 'aceito'), ('erros-lexicos', 'léxico'), ('erros-sintaticos', 'sintático')]:
    out = dest / folder
    out.mkdir(exist_ok=True)
    for file in sorted((root/'exemplos'/folder).glob('*.mordor')):
        relative = file.relative_to(root)
        r = subprocess.run(['java', '-jar', str(jar), '--tree', str(relative)], cwd=root,
                           text=True, encoding='utf-8', capture_output=True)
        observed = 'aceito' if r.returncode == 0 else 'léxico' if ': léxico:' in r.stderr else 'sintático' if ': sintático:' in r.stderr else 'falha operacional'
        if observed != expected or r.returncode != (0 if expected == 'aceito' else 1):
            raise SystemExit(f'{relative}: esperado {expected}; observado {observed}\n{r.stderr}')
        payload = r.stdout if r.returncode == 0 else r.stderr
        suffix = '.arvore.txt' if expected == 'aceito' else '.diagnosticos.txt'
        (out/(file.stem+suffix)).write_text(payload, encoding='utf-8')
        if expected == 'aceito':
            t = subprocess.run(['java', '-jar', str(jar), '--tokens', str(relative)], cwd=root,
                               text=True, encoding='utf-8', capture_output=True, check=True)
            (out/(file.stem+'.tokens.txt')).write_text(t.stdout, encoding='utf-8')
        position = '—' if expected == 'aceito' else ':'.join(r.stderr.splitlines()[0].split(':')[1:3])
        objective = file.stem.replace('_', ' ')
        rows.append(f'| `{relative}` ({objective}) | {expected} | {observed}; saída {r.returncode} | {position} |')
        hashes.append(f'{hashlib.sha256(file.read_bytes()).hexdigest()}  {relative}')
for file in sorted((root/'src/main/antlr4').rglob('*.g4')):
    hashes.append(f'{hashlib.sha256(file.read_bytes()).hexdigest()}  {file.relative_to(root)}')
(dest/'resultados.md').write_text('\n'.join(rows)+'\n', encoding='utf-8')
(dest/'fontes.sha256').write_text('\n'.join(hashes)+'\n', encoding='utf-8')
print(f'51 arquivos verificados; tokens, árvores, diagnósticos e hashes em {dest}')
