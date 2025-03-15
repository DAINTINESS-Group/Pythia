package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DecimalTypeStrategyTest {

    private DecimalTypeStrategy strategy;

    @Before
    public void setUp() {
        strategy = new DecimalTypeStrategy();
    }

    @Test
    public void testValidDecimals() {
        // Test various valid decimal numbers
        testRow("123.45", 1); // Standard decimal number
        testRow("-123.45", 2); // Negative decimal number
        testRow("+123.45", 3); // Decimal number with positive sign
        testRow("123.0", 4); // Decimal with zero fractional part
        testRow("0.123", 5); // Decimal with a small fractional part
        testRow(".123", 6); // Decimal without an integer part
        testRow("123.", 7); // Missing fractional part (unsupported by regex)
    }

    @Test
    public void testInvalidDecimals() {
        // Test various invalid decimal numbers
        testRow("abc", 0); // Non-numeric value
        testRow("1.23e+4", 0); // Scientific notation (unsupported)
        testRow("-1.23e-4", 0); // Negative scientific notation (unsupported)
        testRow("1.23.45", 0); // Multiple decimal points
        testRow("e1", 0); // Scientific notation without a number before "e"
        testRow("123e+4.5", 0); // Incorrect scientific notation format
        testRow("123", 0); // Integer value
    }

    @Test
    public void testNullOrEmptyValues() {
        // Test empty or null values
        testRow(null, 0); // Null value
        testRow("", 0); // Empty string
        testRow("   ", 0); // Whitespace only
    }

    private void testRow(String input, int expectedScore) {
        // Helper method to create a row and test the decimal identification strategy
        Row row = RowFactory.create(input);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.score);
    }
}
