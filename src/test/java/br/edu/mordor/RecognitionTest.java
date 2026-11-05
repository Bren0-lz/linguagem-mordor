package br.edu.mordor;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DynamicTest;
import org.antlr.v4.runtime.Token;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class RecognitionTest {
    @TestFactory Stream<DynamicTest> fixtures() throws Exception {
        List<DynamicTest> tests = new ArrayList<>();
        for (String folder : List.of("validos", "erros-lexicos", "erros-sintaticos")) {
            try (var files = Files.list(Path.of("exemplos", folder))) {
                for (Path file : files.sorted().toList()) {
                    tests.add(DynamicTest.dynamicTest(folder + "/" + file.getFileName(), () -> {
                        String source = Files.readString(file);
                        var result = Frontend.analyze(source, file.toString(), true);
                        if (folder.equals("validos")) {
                            assertTrue(result.accepted(), result.diagnostics().toString());
                            assertNotNull(result.tree());
                            assertTrue(result.treeText().contains("<EOF>"));
                        } else {
                            assertFalse(result.accepted(), "entrada inválida foi aceita");
                            String phase = folder.equals("erros-lexicos") ? "léxico" : "sintático";
                            assertTrue(result.diagnostics().stream().allMatch(d -> d.phase().equals(phase)), result.diagnostics().toString());
                        }
                    }));
                }
            }
        }
        return tests.stream();
    }
    @Test void allReservedWordsInNfcAndNfdAndIdentifierPrefixes() {
        String[] words = ("lûg ash_durb durbatulûk krimp durb thrakul ashum ashuk ashburz burzash nazgum nazg_ul "
+ "ashuk_burz durb_burz ash burzum burz_ash búrz gimb gimb_burz gimbul gimb_ash durb_gimb durb_ash durb_at "
+ "durb_nazg krimp_durb thrak_durb agh agh_ash agh_burz thrak ul thrak_burzash thrak_ashuk krimp_nazg "
+ "gimb_burzash gimb_ashum thrak_nazg thrak_nazgul thrak_ash ash_nazg gimbatul gimb_nazgum nazg nazg_ashum "
+ "gimbat gimbat_ash thrakat gimbat_ashum thrakat_ashuk aghuk aghuk_ash aghuk_burz burzuk thrakuk gimbuk ashum_nazg ishi").split(" ");
        int count = 0;
        for (int type=1; type<=MordorLexer.IN; type++) {
            String word = words[type-1];
            for (var form : List.of(Normalizer.Form.NFC, Normalizer.Form.NFD)) {
                String input = Normalizer.normalize(word, form);
                var r = Frontend.analyze(input, "unicode", false);
                assertTrue(r.accepted(), r.diagnostics().toString());
                assertEquals(type, r.tokens().getFirst().getType(), input);
                assertEquals(word, r.tokens().getFirst().getText());
                assertEquals(MordorLexer.ID, Frontend.analyze(input+"_teste", "prefix", false).tokens().getFirst().getType());
            }
            count++;
        }
        assertEquals(59, count);
    }
    @Test void nfdPreservesStringsAndOriginalPositions() {
        String decomposed = "lu\u0302g";
        String source = decomposed + " \"" + decomposed + "\" búrz";
        var r = Frontend.analyze(source, "unicode", false);
        assertTrue(r.accepted());
        assertEquals("\""+decomposed+"\"", r.tokens().get(1).getText());
        assertEquals(5, r.tokens().get(1).getCharPositionInLine());
        assertEquals("búrz", r.tokens().get(2).getText());
    }
    @Test void asciiAliasesRemainIdentifiers() {
        var r = Frontend.analyze("lug durbatuluk burz", "aliases", false);
        assertTrue(r.accepted());
        assertTrue(r.tokens().stream().filter(t -> t.getType()!=Token.EOF).allMatch(t -> t.getType()==MordorLexer.ID));
    }
    @Test void numericCandidatesStayWholeAndSignsSeparate() {
        for (String candidate : List.of("0b102", "0xFG", "0x", "007", "1.2.3", "2contador", "1e", "1e+", "1e-", "0XFF", "01.5", ".5", "5.")) {
            var r = Frontend.analyze(candidate, "number", false);
            assertEquals(1, r.diagnostics().size(), candidate);
            assertEquals(candidate, r.tokens().getFirst().getText());
            assertEquals(2, r.tokens().size(), candidate);
        }
        var r = Frontend.analyze("-2.5 + 1e-3 - 0b10", "number", false);
        assertTrue(r.accepted());
        assertEquals(List.of(MordorLexer.MINUS, MordorLexer.FLOAT_LITERAL, MordorLexer.PLUS,
            MordorLexer.FLOAT_LITERAL, MordorLexer.MINUS, MordorLexer.INT_LITERAL, Token.EOF),
            r.tokens().stream().map(Token::getType).toList());
    }
    @Test void commentsAndEscapes() {
        var r = Frontend.analyze("/* \" */ // comentario\n\"// /* \\n\\r\\t\\\"\\\\\"", "comments", false);
        assertTrue(r.accepted(), r.diagnostics().toString());
        assertEquals(2, r.tokens().size());
        assertEquals(2, r.tokens().getFirst().getLine());
        assertEquals(MordorLexer.STRING_LITERAL, r.tokens().getFirst().getType());
    }
    @Test void blockCommentAlwaysStopsAtFirstClosingEvenWithManyStars() {
        for (String comment : List.of("/**/", "/***/", "/****/", "/* texto ***/", "/* /* interno */")) {
            var r = Frontend.analyze(comment+" ash", "comment", false);
            assertTrue(r.accepted(), r.diagnostics().toString());
            assertEquals(MordorLexer.TRUE, r.tokens().getFirst().getType());
            assertEquals(2, r.tokens().size());
        }
        for (String comment : List.of("/*", "/**", "/***", "/* texto * texto")) {
            var r = Frontend.analyze(comment, "comment", false);
            assertEquals(MordorLexer.INVALID_COMMENT, r.tokens().getFirst().getType());
            assertEquals(1, r.diagnostics().size());
        }
    }
    @Test void normalizedIdentifiersKeepOriginalTextInsideStrings() {
        var r = Frontend.analyze("açã\u006f ac\u0327a\u0303o \"ac\u0327a\u0303o\"", "nfc", false);
        assertTrue(r.accepted());
        assertEquals(r.tokens().get(0).getText(), r.tokens().get(1).getText());
        assertEquals("\"ac\u0327a\u0303o\"", r.tokens().get(2).getText());
    }
    @Test void unclosedStringWithTrailingBackslashIsOneInvalidCandidate() {
        var r = Frontend.analyze("\"abc\\", "string", false);
        assertEquals(1, r.diagnostics().size());
        assertEquals(MordorLexer.INVALID_STRING, r.tokens().getFirst().getType());
        assertEquals("\"abc\\", r.tokens().getFirst().getText());
    }
    @Test void diagnosticsUseOneBasedPosition() {
        var r = Frontend.analyze("lûg ishi {}\nash_durb ishi {}\ndurbatulûk ishi {\n  @\n}", "pos.mordor", true);
        assertEquals(new Frontend.Diagnostic("pos.mordor", 4, 3, "léxico", "caractere inválido: \"@\""), r.diagnostics().getFirst());
        var s = Frontend.analyze("lûg ishi {}\nash_durb {}\ndurbatulûk ishi {}", "pos.mordor", true);
        assertEquals(2, s.diagnostics().getFirst().line());
        assertEquals(10, s.diagnostics().getFirst().column());
    }
    @Test void precedenceAndAssociativity() {
        var r = Frontend.analyze("lûg ishi {} ash_durb ishi {} durbatulûk ishi { ashburz x = -1 + 2 * 3 < 8 agh ash agh_ash burzum; }", "precedencia", true);
        assertTrue(r.accepted(), r.diagnostics().toString());
        var expression = ((MordorParser.ProgramContext)r.tree()).block(1).localDeclaration(0).expression();
        var or = expression.orExpression();
        assertEquals(2, or.andExpression().size());
        var and = or.andExpression(0);
        assertEquals(2, and.comparisonExpression().size());
        var comp = and.comparisonExpression(0);
        assertEquals(2, comp.additiveExpression().size());
        var sum = comp.additiveExpression(0);
        assertEquals(2, sum.multiplicativeExpression().size());
        assertEquals(2, sum.multiplicativeExpression(1).unaryExpression().size());
        assertEquals("-1", sum.multiplicativeExpression(0).getText());
    }
    @Test void semanticErrorsAreNotSyntaxErrors() {
        for (String body : List.of("ashuk x = 256;", "gimb 1 ishi {}", "krimp_durb;", "ashuk<7> x = ash;", "x = 1;", "[ashuk; 3] xs = [1, 2];")) {
            var r = Frontend.analyze("lûg ishi {} ash_durb ishi {} durbatulûk ishi {"+body+"}", "semantica", true);
            assertTrue(r.accepted(), r.diagnostics().toString());
        }
    }
    @Test void utf8AndUsageErrorsHaveNonzeroExitStatus() throws Exception {
        assertEquals(2, Main.run(new String[]{}));
        Path file = Files.createTempFile("mordor-invalid-utf8", ".mordor");
        try {
            Files.write(file, new byte[]{(byte)0xc3, (byte)0x28});
            assertEquals(2, Main.run(new String[]{"--parse", file.toString()}));
        } finally { Files.delete(file); }
    }
}
