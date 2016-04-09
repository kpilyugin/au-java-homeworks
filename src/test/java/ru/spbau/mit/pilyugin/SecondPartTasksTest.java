package ru.spbau.mit.pilyugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() throws IOException {
        assertEquals(Collections.emptyList(),
                SecondPartTasks.findQuotes(Collections.emptyList(), ""));

        List<String> paths = Arrays.asList(
                createTempFileWithContent("Hello", "world!"),
                createTempFileWithContent("a", "b", "c", "d"),
                createTempFileWithContent("a1", "a2", "a3")
        );

        assertEquals(Collections.singletonList("Hello"),
                SecondPartTasks.findQuotes(paths, "ello"));

        assertEquals(Collections.emptyList(),
                SecondPartTasks.findQuotes(paths, "abcd"));

        assertEquals(Collections.singletonList("a2"),
                SecondPartTasks.findQuotes(paths, "2"));
    }

    @Test(expected = RuntimeException.class)
    public void testFindQuotesMissingFile() throws IOException {
        assertEquals(Collections.emptyList(),
                SecondPartTasks.findQuotes(Collections.singletonList("noSuchFile.txt"), ""));
    }

    @Test
    public void testPiDividedBy4() {
        assertEquals(Math.PI / 4, SecondPartTasks.piDividedBy4(), 1e-3);
    }

    @Test
    public void testFindPrinter() {
        assertEquals(null, SecondPartTasks.findPrinter(Collections.emptyMap()));

        Map<String, List<String>> compositions = ImmutableMap.<String, List<String>>builder()
                .put("Shakespeare", Collections.singletonList("To be or not to be"))
                .put("Orwell", Arrays.asList("Minitrue", "Minipax", "Miniluv", "Miniplenty"))
                .put("Bradbury", Arrays.asList(
                        "You don't have to burn books to destroy a culture. ",
                        "Just get people to stop reading them"))
                .build();

        assertEquals("Bradbury", SecondPartTasks.findPrinter(compositions));
    }

    @Test
    public void testCalculateGlobalOrder() {
        assertEquals(Collections.emptyMap(),
                SecondPartTasks.calculateGlobalOrder(Collections.emptyList()));

        String mouse = "Mouse";
        String keyboard = "Keyboard";
        List<Map<String, Integer>> orders = ImmutableList.of(
                ImmutableMap.of(mouse, 3, keyboard, 4),
                ImmutableMap.of(keyboard, 2),
                ImmutableMap.of(mouse, 5)
        );
        assertEquals(ImmutableMap.of(mouse, 8, keyboard, 6),
            SecondPartTasks.calculateGlobalOrder(orders));
    }

    private static String createTempFileWithContent(String... content) throws IOException {
        File file = File.createTempFile("test" + Arrays.hashCode(content), null);
        file.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(file)) {
            for (String str : content) {
                writer.println(str);
            }
        }
        return file.getPath();
    }
}