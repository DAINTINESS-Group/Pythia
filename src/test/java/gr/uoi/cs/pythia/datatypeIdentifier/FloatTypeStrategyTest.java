package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FloatTypeStrategyTest {
    private FloatTypeStrategy strategy;

    @Before
    public void setUp() {
        strategy = new FloatTypeStrategy();
    }

    @Test
    public void testValidFloats() {
        // Test valid float values
        testRow("123.456 ", 1);          // Standard float number
        testRow(" -123.456", 2);         // Negative float number
        testRow(" +123.456 ", 3);        // Positive float number
        testRow("0.123", 4);            // Small float value
        testRow("-0.123", 5);           // Negative small float value
        testRow("1.23e+4", 6);          // Scientific notation with positive exponent
        testRow("123456.1234567", 7);   // Maximum precision float
        testRow("1.23e12", 8);          // Scientific notation with 2-digit exponent
    }

    @Test
    public void testInvalidFloats() {
        // Test invalid float values
        testRow("123.", 0);             // Missing decimal part
        testRow(".123", 0);             // Missing integer part
        testRow("123e", 0);             // Missing exponent value
        testRow("123..123", 0);         // Multiple decimal points
        testRow("abc", 0);              // Non-numeric string
        testRow("123.45.67", 0);        // Multiple decimal points
        testRow("1234567.12345678", 0); // Exceeds precision limit
        testRow("1.23e123", 0);         // Exponent too large
        testRow("", 0);                 // Empty string
        testRow(" ", 0);                // Whitespace only
        testRow("123", 0);              // Integer value
    }

    @Test
    public void testNullValue() {
        // Test null or empty values
        testRow(null, 0); // Null value
        testRow("", 0);                 // Empty string
        testRow(" ", 0);                // Whitespace only
    }

    private void testRow(String input, int expectedScore) {
        // Helper method to create a row and test the float identification strategy
        Row row = RowFactory.create(input);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.score);
    }
}
