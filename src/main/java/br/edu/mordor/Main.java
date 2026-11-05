package br.edu.mordor;

import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.nio.file.*;
import org.antlr.v4.runtime.Token;

public final class Main {
    private Main() {}
    public static void main(String[] args) {
        System.exit(run(args));
    }
    static int run(String[] args) {
        if (args.length != 2 || !(args[0].equals("--tokens") || args[0].equals("--parse") || args[0].equals("--tree"))) {
            System.err.println("Uso: java -jar target/mordor-compiler-0.2.0.jar --tokens|--parse|--tree arquivo.mordor");
            return 2;
        }
        try {
            String source = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(Files.readAllBytes(Path.of(args[1])))).toString();
            Frontend.Result result = Frontend.analyze(source, args[1], !args[0].equals("--tokens"));
            if (args[0].equals("--tokens")) {
                for (Token t : result.tokens()) {
                    String name = t.getType() == Token.EOF ? "EOF" : MordorLexer.VOCABULARY.getSymbolicName(t.getType());
                    System.out.printf("%d:%d\t%s\t%s%n", t.getLine(), t.getCharPositionInLine()+1, name, Frontend.quote(t.getText()));
                }
            }
            result.diagnostics().forEach(d -> System.err.println(d));
            if (!result.accepted()) return 1;
            if (args[0].equals("--tree")) System.out.println(result.treeText());
            else if (args[0].equals("--parse")) System.out.println("Programa reconhecido (análises léxica e sintática; sem análise semântica).");
            return 0;
        } catch (CharacterCodingException e) {
            System.err.println(args[1] + ": erro de entrada: arquivo não é UTF-8 válido");
            return 2;
        } catch (java.io.IOException e) {
            System.err.println(args[1] + ": erro de leitura: " + e.getMessage());
            return 2;
        }
    }
}
