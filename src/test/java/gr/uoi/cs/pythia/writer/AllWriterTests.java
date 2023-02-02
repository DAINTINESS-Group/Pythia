package gr.uoi.cs.pythia.writer;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NaiveWriterTests.class
})
public class AllWriterTests {

    @ClassRule
    public static WriterResource writerResource = new WriterResource();
}
