package gr.uoi.cs.pythia.util;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

import java.util.HashSet;
import java.util.Set;

public class DatatypeFilterer {

    // Supported Data Types collected from https://spark.apache.org/docs/latest/sql-ref-datatypes.html
    private static final Set<String> numericalDatatypes = new HashSet<String>(){/**
		 * 
		 */
		private static final long serialVersionUID = -4729602094657994877L;

	{
                add(DataTypes.ByteType.toString());
                add(DataTypes.ShortType.toString());
                add(DataTypes.IntegerType.toString());
                add(DataTypes.LongType.toString());
                add(DataTypes.FloatType.toString());
                add(DataTypes.DoubleType.toString());
                add(DataTypes.createDecimalType().toString());
    }};

    private static final Set<String> stringDatatypes = new HashSet<String>(){/**
		 * 
		 */
		private static final long serialVersionUID = -6399462691991676263L;

	{
        add(DataTypes.StringType.toString());
    }};

    public static boolean isNumerical(DataType dataType) {
        return numericalDatatypes.contains(dataType.toString());
    }

    public static boolean isNumerical(String dataType) {
        return numericalDatatypes.contains(dataType);
    }

    public static boolean isStringType(DataType dataType) {
        return stringDatatypes.contains(dataType.toString());
    }
}
