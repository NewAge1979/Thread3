import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class Main {
    private static final String TEMPLATE = "abc";
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 5;
    private static final int WORDS_COUNT = 100_000;
    private static AtomicInteger[] countWords;

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static AtomicInteger[] createArrayForResult() {
        AtomicInteger[] result = new AtomicInteger[MAX_LENGTH - MIN_LENGTH + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = new AtomicInteger(0);
        }
        return result;
    }

    private static ThreadGroup getTestWordGroup(String[] texts, List<Predicate<String>> listOfCriteria) {
        ThreadGroup testWordGroup = new ThreadGroup("testWordGroup");
        listOfCriteria.forEach(criteria ->
                new Thread(
                        testWordGroup,
                        () -> Arrays
                                .stream(texts)
                                .filter(criteria)
                                .forEach(x -> countWords[x.length() - MIN_LENGTH].incrementAndGet())
                ).start()
        );
        return testWordGroup;
    }

    public static void main(String[] args) {
        long startTs = System.currentTimeMillis(); // start time
        Random random = new Random();
        String[] texts = new String[WORDS_COUNT];
        for (int i = 0; i < WORDS_COUNT; i++) {
            texts[i] = generateText(TEMPLATE, MIN_LENGTH + random.nextInt((MAX_LENGTH - MIN_LENGTH) + 1));
        }
        countWords = createArrayForResult();
        List<Predicate<String>> listOfCriteria = List.of(
                WordUtils::isPalindrome,
                WordUtils::isConsistOfOneLetter,
                WordUtils::isSorted
        );
        ThreadGroup testWordGroup = getTestWordGroup(texts, listOfCriteria);
        try {
            Thread[] threads = new Thread[1];
            while (testWordGroup.activeCount() > 0) {
                testWordGroup.enumerate(threads, false);
                threads[0].join();
            }
        } catch (InterruptedException e) {
            return;
        }
        System.out.println("*".repeat(50));
        for (int i = 0; i < countWords.length; i++) {
            int count = countWords[i] == null ? 0 : countWords[i].get();
            System.out.printf("Красивых слов длиной %d: %d шт.\n", MIN_LENGTH + i, count);
        }
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("*".repeat(50));
        System.out.printf("Time: %d ms.\n", (endTs - startTs));
    }
}