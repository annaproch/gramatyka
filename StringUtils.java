package gramatyka;

import java.util.HashSet;
import java.util.Set;

public class StringUtils {
    public static boolean allCharactersLowerCase(String string) {
        return string.matches("^[a-z]*$");
    }

    public static boolean allCharactersUpperCase(String string) {
        return string.matches("^[A-Z]*$");
    }

    public static boolean allCharactersUnique(String string) {
        HashSet<Character> set = new HashSet<>();
        for (char c: string.toCharArray()) set.add(c);
        return set.size() == string.length();
    }

    public static boolean stringContainsCharFromSet(String string, Set<Character> set) {
        for (Character c : string.toCharArray()) {
            if (set.contains(c)) {
                return true;
            }
        }
        return false;
    }
}
