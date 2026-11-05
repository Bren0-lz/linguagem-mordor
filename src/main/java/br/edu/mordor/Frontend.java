package br.edu.mordor;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

/** Etapas 1 e 2: reconhecimento, sem validação semântica ou execução. */
public final class Frontend {
    private Frontend() {}
    public record Diagnostic(String file, int line, int column, String phase, String message) {
        @Override public String toString() {
            return "%s:%d:%d: %s: %s".formatted(file, line, column, phase, message);
        }
    }
    public record Result(List<Token> tokens, List<Diagnostic> diagnostics,
                         MordorParser parser, ParseTree tree) {
        public boolean accepted() { return diagnostics.isEmpty(); }
        public String treeText() { return tree == null ? "" : tree.toStringTree(parser); }
    }
    public static Result analyze(String source, String file, boolean parse) {
        List<Diagnostic> errors = new ArrayList<>();
        MordorLexer lexer = new MordorLexer(CharStreams.fromString(source, file));
        lexer.removeErrorListeners();
        lexer.addErrorListener(listener(file, "léxico", errors));
        CommonTokenStream stream = new CommonTokenStream(lexer);
        stream.fill();
        for (Token t : stream.getTokens()) {
            if (t.getType() == MordorLexer.ID || t.getType() == MordorLexer.HARDWARE
                    || t.getType() == MordorLexer.LOOP || t.getType() == MordorLexer.LOW) {
                ((CommonToken)t).setText(java.text.Normalizer.normalize(t.getText(), java.text.Normalizer.Form.NFC));
            }
            String reason = switch (t.getType()) {
                case MordorLexer.INVALID_NUMBER -> "candidato numérico inválido: " + quote(t.getText());
                case MordorLexer.INVALID_STRING -> "string sem fechamento ou com escape/quebra de linha inválido";
                case MordorLexer.INVALID_COMMENT -> "comentário de bloco sem fechamento";
                case MordorLexer.INVALID_IDENTIFIER -> "identificador iniciado por __ é reservado à implementação";
                case MordorLexer.ERROR_CHAR -> "caractere inválido: " + quote(t.getText());
                default -> null;
            };
            if (reason != null) errors.add(new Diagnostic(file, t.getLine(), t.getCharPositionInLine()+1, "léxico", reason));
        }
        MordorParser parser = null;
        ParseTree tree = null;
        if (parse && errors.isEmpty()) {
            stream.seek(0);
            parser = new MordorParser(stream);
            parser.removeErrorListeners();
            parser.addErrorListener(listener(file, "sintático", errors));
            tree = parser.program();
        }
        return new Result(List.copyOf(stream.getTokens()), List.copyOf(errors), parser, tree);
    }
    private static BaseErrorListener listener(String file, String phase, List<Diagnostic> errors) {
        return new BaseErrorListener() {
            @Override public void syntaxError(Recognizer<?, ?> recognizer, Object offending,
                    int line, int column, String message, RecognitionException e) {
                errors.add(new Diagnostic(file, line, column+1, phase, message));
            }
        };
    }
    public static String quote(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }
}
