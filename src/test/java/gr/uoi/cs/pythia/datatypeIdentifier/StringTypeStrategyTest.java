package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringTypeStrategyTest {

    @Test
    public void testValidStringValues() {
        StringTypeStrategy strategy = new StringTypeStrategy();

        // Valid string values
        testRow("Hello", 1, strategy);       // Alphabetic characters
        testRow("123abc", 2, strategy);      // Alphanumeric string
        testRow("abc@123", 3, strategy);     // String with letters and symbols
        testRow("hello world!", 4, strategy); // String with words and punctuation
        testRow("   some text", 5, strategy); // String with leading spaces
    }

    @Test
    public void testInvalidStringValues() {
        StringTypeStrategy strategy = new StringTypeStrategy();

        // Invalid string values (pure numbers or decimals)
        testRow("12345", 0, strategy);       // Pure numeric string
        testRow("123.45", 0, strategy);     // Decimal number
        testRow(" 123 ", 0, strategy);      // Numeric string with spaces
        testRow("", 0, strategy);           // Empty string
        testRow("    ", 0, strategy);       // Whitespace-only string
        testRow("0", 0, strategy);          // Single digit zero
    }

    private void testRow(String value, int expectedScore, StringTypeStrategy strategy) {
        Row row = RowFactory.create(value);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.getScore());
    }
}
