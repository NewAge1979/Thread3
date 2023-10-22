public class WordUtils {
    public static boolean isPalindrome(String value) {
        return value != null && !value.isEmpty() && new StringBuffer(value).reverse().toString().equalsIgnoreCase(value);
    }

    public static boolean isConsistOfOneLetter(String value) {
        return value != null && !value.isEmpty() && value.chars().allMatch(x -> x == value.charAt(0));
    }

    public static boolean isSorted(String value) {
        String sorted = value.chars()
                .sorted()
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return value != null && !value.isEmpty() && sorted.equalsIgnoreCase(value);
    }
}