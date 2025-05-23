package hexlet.code;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchEngine {
    public static List<String> search(List<Map<String, String>> docs, String text) {
        var searchingWords = Arrays.stream(text.split(" ")).map(SearchEngine::trim).toList();
        System.out.println("WORDS: " + searchingWords);
        var index = index(docs);
        System.out.println("INDEX: " + index);
        var reversedIndex = reverseIndex(index);
        System.out.println("REVERSED: " + reversedIndex);
        var scoredResultMap = idfFt(index, reversedIndex, searchingWords);
        System.out.println("RES: " + scoredResultMap);

        return scoredResultMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private static String trim(String token) {
        return Pattern.compile("\\w+")
                .matcher(token)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.joining());
    }

    private static Map<String, List<String>> index(List<Map<String, String>> docs) {
        return docs.stream().flatMap(item ->
                        Arrays.stream(item.get("text").split(" "))
                                .map(SearchEngine::trim)
                                .map(word -> Map.entry(item.get("id"), word)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    private static Map<String, List<String>> reverseIndex(Map<String, List<String>> index) {
        return index.entrySet().stream()
                .flatMap(mapListEntry -> mapListEntry.getValue().stream()
                        .map(word -> Map.entry(word, mapListEntry.getKey())))
                .filter(entry -> !entry.getKey().isEmpty())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    private static Map<String, Double> idfFt(
            Map<String, List<String>> docs,
            Map<String, List<String>> reverseIndex,
            List<String> words
    ) {
        return words.stream()
                .flatMap(word -> idfFt(docs, reverseIndex, word).stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingDouble(Map.Entry::getValue)));
    }

    private static List<Map.Entry<String, Float>> idfFt(
            Map<String, List<String>> docs,
            Map<String, List<String>> reverseIndex,
            String word
    ) {
        var idf = idf(reverseIndex, word);
        if (idf == 0) {
            return List.of();
        }
        return reverseIndex.get(word).stream()
                .map(doc -> Map.entry(doc, tf(docs.get(doc), word) / idf))
                .toList();
    }

    private static float tf(List<String> doc, String word) {
        return (float) doc.stream().filter(word::equals).count() / doc.size();
    }

    private static float idf(Map<String, List<String>> reversedIndex, String word) {
        var docsCount = reversedIndex.size();
        if (reversedIndex.containsKey(word)) {
            var termCount = reversedIndex.get(word).size();
            return (float) (Math.log(1 + (docsCount - termCount + 1) / (termCount + 0.5)) / Math.log(2));
        } else {
            return 0;
        }
    }
}