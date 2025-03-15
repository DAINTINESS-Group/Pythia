package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BooleanTypeStrategyTest {
    private BooleanTypeStrategy strategy;

    @Before
    public void setUp() {
        strategy = new BooleanTypeStrategy();
    }

    /**
     * Test case to verify the scoring of valid true values.
     * <p>
     * This test checks various string representations of "true" and ensures
     * they are scored appropriately. Different variations in capitalization
     * and alternative representations (e.g., "1", "yes", "on") are tested. The scoring
     * is based on a predefined scale, where a higher value indicates a stronger
     * indication of a true value.
     */
    @Test
    public void testValidTrueValues() {
        assertScore("true", 1);
        assertScore("1", 2);
        assertScore("yes", 3);
        assertScore("on", 4);
        assertScore("enabled", 5);
        assertScore("ok", 6);
        assertScore("TRUE", 7);
        assertScore("YES", 8);
    }

    /**
     * Test case to verify the scoring of valid false values.
     * <p>
     * This test checks various string representations of "false" and ensures
     * they are scored appropriately. Different variations in capitalization
     * and alternative representations (e.g., "0", "no", "off") are tested. As with
     * true values, the scoring is based on a predefined scale.
     */
    @Test
    public void testValidFalseValues() {
        assertScore("false", 1);
        assertScore("0", 2);
        assertScore("no", 3);
        assertScore("off", 4);
        assertScore("disabled", 5);
        assertScore("not ok", 6);
        assertScore("FALSE", 7);
        assertScore("NO", 8);
    }

    /**
     * Test case to verify the scoring of invalid values.
     * <p>
     * This test checks various strings that are not valid boolean representations
     * and ensures they are scored as 0. Examples include "maybe", "123", "hello",
     * empty strings, "null", and misspelled variations of true/false keywords.
     */
    @Test
    public void testInvalidValues() {
        assertScore("maybe", 0);
        assertScore("123", 0);
        assertScore("hello", 0);
        assertScore("", 0);
        assertScore("null", 0);
        assertScore("yess", 0);
        assertScore("enable", 0);
    }

    /**
     * Test case to verify the scoring of null and empty values.
     * <p>
     * This test ensures that null, empty strings, and strings containing only
     * whitespace are scored as 0.
     */
    @Test
    public void testNullValue() {
        assertScore(null, 0);
        assertScore("", 0);
        assertScore(" ", 0);

    }

    private void assertScore(String input, int expectedScore) {
        Row row = RowFactory.create(input);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.score);
    }
}