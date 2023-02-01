package gr.uoi.cs.pythia.report;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TxtReportTests.class,
        MdReportTests.class
})
public class AllReportTests {

    @ClassRule
    public static ReportResource reportResource = new ReportResource();
}
