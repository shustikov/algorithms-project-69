package hexlet.code;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
class SearchEngineTest {
    private static final String doc1 = "I can't shoot straight unless I've had a pint!";
    private static final String doc2 = "Don't shoot shoot shoot that thing at me.";
    private static final String doc3 = "I'm your shooter.";

    List<Map<String, String>> docs = List.of(
            Map.of("id", "doc1", "text", doc1),
            Map.of("id", "doc2", "text", doc2),
            Map.of("id", "doc3", "text", doc3)
    );

    @Test
    void search() {
        var result = SearchEngine.search(docs, "shoot");

        System.out.println(result);
        assertTrue(result.contains("doc1"));
        assertTrue(result.contains("doc2"));
        assertFalse(result.contains("doc3"));
        assertEquals(0, result.indexOf("doc2"));
    }

    @Test
    void search_tokenizedInput() {
        var result = SearchEngine.search(docs, "pint");

        assertTrue(result.contains("doc1"));
        assertFalse(result.contains("doc3"));
    }

    @Test
    void search_untokenizedInput() {
        var result = SearchEngine.search(docs, "pint!");

        assertTrue(result.contains("doc1"));
        assertFalse(result.contains("doc3"));
    }

    @Test
    void search_fewWords() {
        var result = SearchEngine.search(docs, "shoot at me");

        assertTrue(result.contains("doc1"));
        assertFalse(result.contains("doc3"));
        assertEquals(0, result.indexOf("doc2"));
    }
}