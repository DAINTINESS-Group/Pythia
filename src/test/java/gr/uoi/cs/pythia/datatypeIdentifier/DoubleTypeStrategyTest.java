package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DoubleTypeStrategyTest {
    private DoubleTypeStrategy strategy;

    @Before
    public void setUp() {
        strategy = new DoubleTypeStrategy();
    }

    @Test
    public void testValidDoubles() {
        // Test valid double values
        testRow("123.45", 1);          // Standard decimal number
        testRow("-123.45", 2);         // Negative decimal number
        testRow("+123.45", 3);         // Positive decimal number
        testRow("1.23e+4", 4);         // Scientific notation (positive exponent)
        testRow("-1.23e-4", 5);        // Scientific notation (negative exponent)
        testRow("0.123", 6);           // Small decimal number
        testRow("123456789012345.1234567890123456", 7); // Maximum precision number
        testRow("1.23e123", 8);        // Scientific notation with large exponent
        testRow("123456789012345.1234567890123456e123", 9); // Combination of precision and exponent
        testRow("1e10", 10);           // Exponential notation without decimal part
    }

    @Test
    public void testInvalidDoubles() {
        // Test invalid double values
        testRow("123", 0);            // Integer value (no decimal part)
        testRow("abc", 0);             // Non-numeric string
        testRow("12.34.56", 0);        // Multiple decimal points
        testRow("12e34.56", 0);        // Incorrect scientific notation format
        testRow("1234567890123456.12345678901234567", 0); // Exceeds precision limit
        testRow("1.23e1234", 0);       // Exponent too large
        testRow(".123", 0);            // Decimal without integer part
        testRow("123.", 0);            // Integer with missing fractional part
    }

    @Test
    public void testNullValue() {
        // Test null or empty values
        testRow(null, 0); // Null value
        testRow("", 0);                // Empty string
        testRow(" ", 0);               // Whitespace only
    }

    private void testRow(String input, int expectedScore) {
        // Helper method to create a row and test the double identification strategy
        Row row = RowFactory.create(input);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.score);
    }
}
