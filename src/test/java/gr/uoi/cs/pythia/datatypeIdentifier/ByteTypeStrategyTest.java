package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteTypeStrategyTest {
    private ByteTypeStrategy strategy;

    @Before
    public void setUp() {
        strategy = new ByteTypeStrategy();
    }
    /**
     * Test case to verify the identification and scoring of valid byte values.
     * <p>
     * This test checks various valid byte values, including zero, the maximum
     * and minimum byte values, as well as typical positive and negative byte
     * values. The scoring is based on a predefined scale, where a higher
     * value indicates a stronger indication that the value is of byte type.
     */
    @Test
    public void testValidBytes() {
        testRow("0", 1); // "0" is a valid byte
        testRow("127", 2); // Maximum allowed byte
        testRow(" -128  ", 3); // Minimum allowed byte (with whitespace)
        testRow("45 ", 4); // A typical positive byte (with whitespace)
        testRow(" -12 ", 5); // A typical negative byte (with whitespace)
    }
    /**
     * Test case to verify the identification and scoring of invalid byte values.
     * <p>
     * This test checks various invalid values, such as empty strings,
     * non-numeric strings, values outside the byte range (above 127 and below
     * -128), and decimal numbers. Invalid values are expected to receive a
     * score of 0.
     */
    @Test
    public void testInvalidBytes() {
        testRow("", 0); // Empty value
        testRow("abc", 0); // Non-numeric string
        testRow("128", 0); // Out of range (beyond byte max)
        testRow("-129", 0); // Out of range (below byte min)
        testRow("12.5", 0); // Decimal number
    }
    /**
     * Test case to verify the scoring of null and empty values.
     * <p>
     * This test checks the scoring of null values, empty strings, and strings
     * containing only whitespace. All these cases are expected to receive a
     * score of 0.
     */
    @Test
    public void testNullValue() {
        testRow(null, 0); // Null value
        testRow("", 0);    // Empty string
        testRow(" ", 0);    // Whitespace string
    }

    private void testRow(String input, int expectedScore) {
        Row row = RowFactory.create(input);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.score);
    }
}
