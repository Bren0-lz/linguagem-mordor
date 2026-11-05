#!/usr/bin/env python3
"""Exporta as regras existentes como gramática combinada para inspeção no Lab."""
from pathlib import Path
import re

root = Path(__file__).resolve().parent.parent
src = root/'src/main/antlr4/br/edu/mordor'
lexer = (src/'MordorLexer.g4').read_text(encoding='utf-8')
parser = (src/'MordorParser.g4').read_text(encoding='utf-8')
lexer = re.sub(r'lexer grammar MordorLexer;\s*', '', lexer, count=1)
parser = re.sub(r'parser grammar MordorParser;\s*', '', parser, count=1)
parser = re.sub(r'options\s*\{\s*tokenVocab=MordorLexer;\s*\}\s*', '', parser, count=1)
for out in [root/'target/lab/Mordor.g4', root/'docs/lab/Mordor.g4']:
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text('grammar Mordor;\n\n'+parser+'\n'+lexer, encoding='utf-8')
    print(out)
