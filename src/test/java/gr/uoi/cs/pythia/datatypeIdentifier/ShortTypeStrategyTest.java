package gr.uoi.cs.pythia.datatypeIdentifier;


import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShortTypeStrategyTest {

    @Test
    public void testValidShortValues() {
        ShortTypeStrategy strategy = new ShortTypeStrategy();

        // Valid short numbers
        testRow("0", 1, strategy);         // Zero
        testRow("32767", 2, strategy);     // Maximum allowed short value
        testRow("-32768", 3, strategy);    // Minimum allowed short value
        testRow("1234", 4, strategy);      // Valid number within range
        testRow("-1234", 5, strategy);     // Valid negative number
    }

    @Test
    public void testInvalidShortValues() {
        ShortTypeStrategy strategy = new ShortTypeStrategy();

        // Invalid short numbers
        testRow("32768", 0, strategy);     // Out of range (greater than max allowed value)
        testRow("-32769", 0, strategy);    // Out of range (less than min allowed value)
        testRow("abc", 0, strategy);       // Non-numeric string
        testRow("123456", 0, strategy);    // Too large number
        testRow("1e3", 0, strategy);       // Scientific notation (not a short integer)
        testRow("", 0, strategy);          // Empty string
        testRow(" ", 0, strategy);         // String with whitespace only
        testRow(" 54.76", 0, strategy);    // Decimal number
    }

    private void testRow(String value, int expectedScore, ShortTypeStrategy strategy) {
        Row row = RowFactory.create(value);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.getScore());
    }
}