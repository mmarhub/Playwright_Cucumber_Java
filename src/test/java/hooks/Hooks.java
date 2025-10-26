package hooks;

import io.cucumber.java.*;
import manager.BrowserManager;

public class Hooks {

    private final BrowserManager browserManager;

    public Hooks(BrowserManager browserManager) {
        this.browserManager = browserManager;
    }

    //Runs once before all tests start
    @BeforeAll
    public static void beforeAll() {
        System.out.println("\nExecuting test suite....");
    }

    //Runs once after all tests are done
    @AfterAll
    public static void afterAll() {
        System.out.println("\nFinished executing the test suite!\n");
    }

    //Runs before each test
    @Before
    public void setup(Scenario scenario) {
        browserManager.setUp(scenario);
    }

    //Runs after each test
    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = browserManager.takeScreenshot();
            scenario.attach(screenshot, "image/png", "📸 " + "screenshot");
        }
        browserManager.tearDown();
    }
}
