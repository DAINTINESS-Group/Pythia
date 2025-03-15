package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateTypeStrategyTest {
    private DateTypeStrategy strategy;

    @Before
    public void setUp() {
        strategy = new DateTypeStrategy();
    }

    @Test
    public void testValidDates() {
        // Test various valid date formats
        testRow("2025-02-03", 1);  // YYYY-MM-DD (valid)
        testRow("2023-11-25", 2);  // YYYY-MM-DD (valid)
        testRow("03-02-2025", 3);  // DD-MM-YYYY (valid)
        testRow("01-12-2023", 4);  // DD-MM-YYYY (valid)
        testRow("31-04-2025", 4);  // DD-MM-YYYY (invalid, April has 30 days)
        testRow("03/02/2025", 5);  // DD/MM/YYYY (valid)
        testRow("12/08/2024", 6);  // DD/MM/YYYY (valid)
        testRow("31/02/2025", 6);  // DD/MM/YYYY (invalid, February has max 29 days)
        testRow("03/02/25", 7);    // DD/MM/YY (valid)
        testRow("28/11/24", 8);    // DD/MM/YY (valid)
        testRow("30/02/25", 8);    // DD/MM/YY (invalid, February has max 29 days)
        testRow("02-03-25", 9);    // MM-DD-YY (valid)
        testRow("11-15-24", 10);   // MM-DD-YY (valid)
        testRow("13-25-22", 10);   // MM-DD-YY (invalid, month 13 out of bounds)
        testRow("2025/02/03", 11); // YYYY/MM/DD (valid)
        testRow("2023/12/25", 12); // YYYY/MM/DD (valid)
        testRow("2025/15/03", 12); // YYYY/MM/DD (invalid, month 15 out of bounds)
        testRow("03 February 2025", 13); // DD Month YYYY (valid)
        testRow("28 November 2024", 14); // DD Month YYYY (valid)
        testRow("31 April 2025", 14);    // DD Month YYYY (invalid, April has 30 days)
        testRow("February 03, 2025", 15); // Month DD, YYYY (valid)
        testRow("October 12, 2023", 16);  // Month DD, YYYY (valid)
        testRow("December 31, 2025", 17); // Month DD, YYYY (valid)
    }

    @Test
    public void testInvalidDates() {
        // Test various invalid dates
        testRow("2025-02-31", 0);  // Invalid (February 31st does not exist)
        testRow("31 February 2025", 0);  // Invalid (February 31st does not exist)
        testRow("15/13/2025", 0);  // Invalid (Month 13 out of bounds)
        testRow("2025/02/30", 0);  // Invalid (February 30th does not exist)
        testRow("29 February 2023", 0);  // Invalid (2023 is not a leap year)
        testRow("abc", 0);         // Invalid (Non-numeric string)
        testRow("128", 0);         // Invalid (Incorrect date format)
    }

    @Test
    public void testNullValue() {
        // Test empty or null values
        testRow(null, 0);  // Null value
        testRow("", 0);    // Empty string
        testRow(" ", 0);   // Whitespace only
    }

    private void testRow(String input, int expectedScore) {
        // Helper method to create a row and test the date identification strategy
        Row row = RowFactory.create(input);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.score);
    }
}
