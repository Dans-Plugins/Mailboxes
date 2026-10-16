package dansplugins.mailboxes.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ArgumentParserTest {

    private ArgumentParser argumentParser;

    @Before
    public void setUp() {
        argumentParser = new ArgumentParser();
    }

    @Test
    public void testDropFirstArgumentRemovesOnlyTheFirstElement() {
        String[] result = argumentParser.dropFirstArgument(new String[]{"send", "Steve", "\"hi\""});

        assertArrayEquals(new String[]{"Steve", "\"hi\""}, result);
    }

    @Test
    public void testDropFirstArgumentOnSingleElementReturnsEmptyArray() {
        String[] result = argumentParser.dropFirstArgument(new String[]{"help"});

        assertEquals(0, result.length);
    }

    @Test
    public void testDropFirstArgumentDoesNotModifyInput() {
        String[] args = {"open", "5"};

        argumentParser.dropFirstArgument(args);

        assertArrayEquals(new String[]{"open", "5"}, args);
    }

    @Test(expected = NegativeArraySizeException.class)
    public void testDropFirstArgumentOnEmptyArrayThrows() {
        // Current behavior: callers must not pass an empty array. CommandInterpreter
        // returns early when args.length == 0, so this is never reached in practice.
        argumentParser.dropFirstArgument(new String[0]);
    }

    @Test
    public void testQuotedArgumentSpanningSeveralWordsIsJoinedWithSpaces() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(
                new String[]{"Steve", "\"hello", "there", "friend\""});

        assertEquals(Collections.singletonList("hello there friend"), result);
    }

    @Test
    public void testMultipleQuotedArgumentsAreReturnedInOrder() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(
                new String[]{"\"first", "one\"", "unquoted", "\"second\""});

        assertEquals(Arrays.asList("first one", "second"), result);
    }

    @Test
    public void testNoQuotesReturnsEmptyList() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(
                new String[]{"Steve", "hello"});

        assertTrue(result.isEmpty());
    }

    @Test
    public void testUnterminatedQuoteIsIgnored() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(
                new String[]{"\"closed\"", "\"never", "closed"});

        assertEquals(Collections.singletonList("closed"), result);
    }

    @Test
    public void testEmptyQuotesYieldEmptyString() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(new String[]{"\"\""});

        assertEquals(Collections.singletonList(""), result);
    }

    @Test
    public void testSingleQuotesAreNotTreatedAsDelimiters() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(
                new String[]{"'hello", "there'"});

        assertTrue(result.isEmpty());
    }
}
