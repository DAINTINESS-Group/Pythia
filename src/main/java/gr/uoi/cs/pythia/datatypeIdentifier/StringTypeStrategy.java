package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

public class StringTypeStrategy extends DataDetectorStrategy {


    public StringTypeStrategy() {
        super();
    }

    @Override
    public void findDataType(Row valueInColum) {

            String valueInRowString = valueInColum.get(0).toString().trim();
            if (valueInRowString.isEmpty()) {
                return;
            }
            if (valueInRowString.matches(".*[a-zA-Z].*")) {
                score += 1;
            }
            else if (valueInRowString.matches(".*[\\w\\s\\p{Punct}].*")
                    && !valueInRowString.matches("^[0-9]+$")
                    && !valueInRowString.matches("^[+-]?\\d*\\.\\d+$")) {
                score += 1;
            }
        }
}
