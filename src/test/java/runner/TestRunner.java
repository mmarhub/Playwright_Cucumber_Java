package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"step_definitions", "hooks"},
        tags = "@github and not @ignore",
        plugin = {"pretty",
                "html:target/site/cucumber-pretty",
                "json:target/cucumber.json"
        }
)

public class TestRunner extends AbstractTestNGCucumberTests {

    // DataProvider Method
    // Used for parallel execution, allowing multiple tests to run simultaneous
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios(); // Provide data for the tests, enabling parallel execution
    }

    // Parallel Setup Method
    @BeforeClass
    public void parallelSetup(ITestContext context) {
        // Get the thread count from system properties or default to 1
        int threadCount = Integer.parseInt(System.getProperty("thread.count", "1"));
        Logger logger = Logger.getLogger(TestRunner.class.getName());
        logger.log(Level.INFO, "Configured thread count value: " + threadCount);

        // Set the data provider thread count for parallel execution
        context.getSuite().getXmlSuite().setDataProviderThreadCount(threadCount);
    }
}

// sample run command
// mvn clean test -Dheadless=true -DslowMo=500 -Dbrowser=chromium -Dthread.count=5 -Dcucumber.filter.tags="@regression"

// reference for parallel execution with TestNG and Cucumber
// https://www.youtube.com/watch?v=g3BGOmmkzpQ
