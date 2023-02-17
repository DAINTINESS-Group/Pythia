package gr.uoi.cs.pythia.writer;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        NaiveWriterTests.class
})
public class AllWriterTests {

    @ClassRule
    public static WriterResource writerResource = new WriterResource();
}
