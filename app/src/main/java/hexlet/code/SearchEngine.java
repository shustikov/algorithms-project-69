package hexlet.code;

import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchEngine {
    public static List<String> search(List<Map<String, String>> docs, String text) {
        return docs.stream()
                .filter(doc -> doc.get("text").contains(trim(text) + " "))
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
}