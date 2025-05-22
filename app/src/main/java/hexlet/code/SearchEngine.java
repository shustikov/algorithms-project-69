package hexlet.code;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchEngine {
    private static final ToIntFunction<Map.Entry<?, List<Integer>>> bySize = entry -> entry.getValue().size();
    private static final ToIntFunction<Map.Entry<?, List<Integer>>> byMax = entry -> Collections.max(entry.getValue());


    public static List<String> search(List<Map<String, String>> docs, String text) {
        var searchingWords = Arrays.stream(text.split(" ")).map(SearchEngine::trim).toList();

        return docs.stream()
                .map(doc -> Map.entry(doc, Arrays.stream(doc.get("text").split(" "))
                        .map(SearchEngine::trim)
                        .sorted()
                        .toList())
                )
                .map(entry -> Map.entry(
                        entry.getKey(),
                        countWordsFromSearchInDoc(entry.getValue(), searchingWords))
                )
                .filter(entry -> !entry.getValue().isEmpty())
                .sorted(Comparator.comparingInt(bySize).reversed().thenComparing(Comparator.comparingInt(byMax).reversed()))
                .peek(mapListEntry -> System.out.println(mapListEntry.getValue()))
                .map(Map.Entry::getKey)
                .map(doc -> doc.get("id"))
                .toList();
    }

    private static String trim(String token) {
        return Pattern.compile("\\w+")
                .matcher(token)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.joining());
    }


    private static List<Integer> countWordsFromSearchInDoc(List<String> doc, List<String> searchingWords) {
        return searchingWords.stream()
                .map(word -> countWordInDoc(doc, word)).filter(i -> i > 0)
                .toList();
    }

    private static Integer countWordInDoc(List<String> doc, String word) {
        return Math.toIntExact(doc.stream().filter(docWord -> docWord.equals(word)).count());
    }
}