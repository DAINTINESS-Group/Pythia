package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LongTypeStrategyTest {
    private LongTypeStrategy strategy;

    @Before
    public void setUp() {
        strategy = new LongTypeStrategy();
    }

    @Test
    public void testValidLongValues() {
        // Valid long numbers
        testRow("0", 1);                    // Zero
        testRow("123", 2);                  // Positive long number
        testRow("-123", 3);                 // Negative long number
        testRow("+123", 4);                 // Positive long number with explicit sign
        testRow("9223372036854775807", 5);  // Maximum allowed long value
        testRow("-9223372036854775808", 6); // Minimum allowed long value
    }

    @Test
    public void testInvalidLongValues() {
        // Invalid long numbers
        testRow("9223372036854775808", 0);      // Out of range (greater than max allowed value)
        testRow("-9223372036854775809", 0);     // Out of range (less than min allowed value)
        testRow("123.45", 0);                  // Decimal number
        testRow("abc", 0);                     // Non-numeric string
        testRow("1e3", 0);                     // Scientific notation (not a long integer)
        testRow("", 0);                        // Empty string
        testRow(" ", 0);                       // String with whitespace only
        testRow("123456789012345678901", 0);    // Very large number exceeding long range
    }

    private void testRow(String input, int expectedScore) {
        Row row = RowFactory.create(input);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.score);
    }
}
