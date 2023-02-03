package gr.uoi.cs.pythia.report;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TxtReportTests.class,
        MdReportTests.class
})
public class AllReportTests {

    @ClassRule
    public static ReportResource reportResource = new ReportResource();
}
