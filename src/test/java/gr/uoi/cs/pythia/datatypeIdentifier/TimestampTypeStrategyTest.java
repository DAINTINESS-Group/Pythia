package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class TimestampTypeStrategyTest {

    @Test
    public void testValidTimestampValues() {
        TimestampTypeStrategy strategy = new TimestampTypeStrategy();

        // Valid timestamp values
        testRow("2025-02-03 14:30:00", 1, strategy);         // YYYY-MM-DD HH:MM:SS
        testRow("2025-02-03 14:30:00.123", 2, strategy);     // YYYY-MM-DD HH:MM:SS.SSS
        testRow("2025-02-03 14:30:00+02:00", 3, strategy);   // YYYY-MM-DD HH:MM:SS±HH:MM
        testRow("2025-02-03 14:30:00-03:00", 4, strategy);   // YYYY-MM-DD HH:MM:SS±HH:MM
    }

    @Test
    public void testInvalidTimestampValues() {
        TimestampTypeStrategy strategy = new TimestampTypeStrategy();

        // Invalid timestamp values
        testRow("2025-02-03 14:60:00", 0, strategy);         // Invalid minutes (60 minutes)
        testRow("2025-02-03 25:30:00", 0, strategy);         // Invalid hour (25 hours)
        testRow("2025-02-03 14:30:60", 0, strategy);         // Invalid seconds (60 seconds)
        testRow("2025-02-03 14:30:00+25:00", 0, strategy);   // Invalid timezone offset (+25:00)
        testRow("2025-02-03 14:30:00-25:00", 0, strategy);   // Invalid timezone offset (-25:00)
        testRow("2025-02-03 14:30:00abc", 0, strategy);      // Invalid string appended to timestamp
    }

    private void testRow(String value, int expectedScore, TimestampTypeStrategy strategy) {
        Row row = RowFactory.create(value);
        strategy.identifyDataType(row);
        assertEquals(expectedScore, strategy.getScore());
    }
}
