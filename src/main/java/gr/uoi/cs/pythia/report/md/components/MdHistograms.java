package gr.uoi.cs.pythia.report.md.components;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.histogram.Bin;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;
import gr.uoi.cs.pythia.report.md.structures.MdTable;
import gr.uoi.cs.pythia.util.DatatypeFilterer;

public class MdHistograms {

    private final List<Column> columns;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.###",
            new DecimalFormatSymbols(Locale.ENGLISH));;

    public MdHistograms(List<Column> columns) {
        this.columns = columns.stream()
                .filter(column -> DatatypeFilterer.isNumerical(column.getDatatype()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        if (columns.isEmpty())
            return "";
        return getTitle() + "\n" +
                MdBasicStructures.horizontalLine() + "\n" +
                getAllHistograms() + "\n"+
                MdBasicStructures.center(MdBasicStructures.heading2("Quartile Histograms:"))+"\n"+
                MdBasicStructures.horizontalLine() + "\n" +
                getAllQuartileHistograms()+"\n";
    }

    private String getTitle() {
        return MdBasicStructures.center(MdBasicStructures.heading2("Histograms"));
    }

    private String getAllHistograms(){
        return generateHistogramSection(Column::getQuartilesHistogram);
    }
    private String getAllQuartileHistograms(){
        return generateHistogramSection(Column::getQuartilesHistogram);
    }
    private String generateHistogramSection( Function<Column, Histogram> histogramExtractor){
        StringBuilder stringBuilder = new StringBuilder();
        for (Column column : columns) {
           Histogram histogram = histogramExtractor.apply(column);
           if(histogram == null){continue;}
            stringBuilder.append(MdBasicStructures.bold(String.format(
                    "~ Column: %s", column.getName())));
            stringBuilder.append("\n\n");
            stringBuilder.append(MdBasicStructures.center(
                    new MdTable(getTableHeaders(), getTableData(histogram), MdTable.ALIGNMENT_TYPE.CENTER)
                            .getTable()
            ));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private List<String> getTableHeaders() {
        return Arrays.asList("Range", "Values");
    }

    private List<List<String>> getTableData(Histogram histogram) {
        List<Bin> bins = histogram.getBins();
        List<List<String>> allRowsData = new ArrayList<>();
        for (Bin bin : bins) {
            List<String> rowData = new ArrayList<>();
            rowData.add(bin.getBoundsLabel());
            rowData.add(decimalFormat.format(bin.getCount()));
            allRowsData.add(rowData);
        }
        return allRowsData;
    }

}
