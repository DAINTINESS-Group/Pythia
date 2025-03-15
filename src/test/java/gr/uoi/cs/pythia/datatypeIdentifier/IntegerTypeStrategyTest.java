package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntegerTypeStrategyTest {
    private IntegerTypeStrategy strategy;

    @Before
    public void setUp() {
        strategy = new IntegerTypeStrategy();
    }

    @Test
    public void testValidIntegers() {
        // Valid integer numbers
        testRow("0", 1);               // Zero
        testRow("123", 2);             // Positive integer
        testRow("-123", 3);            // Negative integer
        testRow("+123", 4);            // Positive integer with explicit sign
        testRow("2147483647", 5);      // Maximum allowed integer value
        testRow("-2147483648", 6);     // Minimum allowed integer value
    }

    @Test
    public void testInvalidIntegers() {
        // Invalid integer numbers
        testRow("2147483648", 0);      // Out of range (greater than max allowed value)
        testRow("-2147483649", 0);     // Out of range (less than min allowed value)
        testRow("123.45", 0);          // Decimal number
        testRow("abc", 0);             // Non-numeric string
        testRow("1e3", 0);             // Scientific notation (not an integer)
        testRow("", 0);                // Empty string
        testRow(" ", 0);               // String with whitespace only
        testRow("12345678901", 0);     // Very large number exceeding integer range
    }

    private void testRow(String input, int expectedScore) {
        Row row = RowFactory.create(input);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.score);
    }
}

