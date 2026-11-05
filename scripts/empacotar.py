#!/usr/bin/env python3
"""Empacota fontes e evidências existentes; execute verify e gerar-evidencias antes."""
from pathlib import Path
import zipfile

root = Path(__file__).resolve().parent.parent
jar = root/'target/mordor-compiler-0.2.0.jar'
lab = root/'target/lab/Mordor.g4'
for required in [jar, lab, root/'docs/evidencias/resultados.md']:
    if not required.is_file():
        raise SystemExit(f'Arquivo ausente: {required}; siga o README antes de empacotar.')
out = root/'target/mordor-entrega-2.zip'
with zipfile.ZipFile(out, 'w', zipfile.ZIP_DEFLATED) as archive:
    for name in ['README.md', 'LICENSE', '.gitignore', 'pom.xml']:
        archive.write(root/name, name)
    for name in ['src', 'docs', 'exemplos', 'scripts']:
        for file in sorted((root/name).rglob('*')):
            if file.is_file() and '__pycache__' not in file.parts:
                archive.write(file, file.relative_to(root))
    archive.write(jar, jar.relative_to(root))
    archive.write(lab, lab.relative_to(root))
print(out)
