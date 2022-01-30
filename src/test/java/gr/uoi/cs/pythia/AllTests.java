package gr.uoi.cs.pythia;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({UtilTests.class, LabelingSystemTests.class, ReportSystemTests.class})
public class AllTests {}
