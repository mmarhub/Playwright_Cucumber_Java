package hooks;

import io.cucumber.java.*;
import manager.APIManager;
import manager.BrowserManager;
import manager.PlaywrightManager;
import utilities.RESTUtils;

public class Hooks {

    private final PlaywrightManager playwrightManager;
    private final BrowserManager browserManager;
    private final APIManager apiManager;
    private final RESTUtils restUtils;

    public Hooks(
            PlaywrightManager playwrightManager,
            BrowserManager browserManager,
            APIManager apiManager,
            RESTUtils restUtils
    ) {
        this.playwrightManager = playwrightManager;
        this.browserManager = browserManager;
        this.apiManager = apiManager;
        this.restUtils = restUtils;
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
        // Log in which thread the scenario is running
        scenario.attach(
                "This scenario runs in Thread ID: " + Thread.currentThread().threadId()
                        + " and runs in Thread Name: " + Thread.currentThread().getName(),
                "text/plain",
                "Thread Info"
        );

        boolean isAPIOnly = scenario.getSourceTagNames().contains("@api");

        // if it is an API test, skip browser setup
        if (isAPIOnly) {
            apiManager.setUp(scenario);
        } else {
            browserManager.setUp(scenario);
            // Optional: also set up API if it needs to have hybrid scenarios
            // apiManager.setUp(scenario);
        }
    }

    //Runs after each test
    @After
    public void tearDown(Scenario scenario) {
        boolean isAPIOnly = scenario.getSourceTagNames().contains("@api");

        try {
            if (isAPIOnly) {
                // Dispose the RESTUtils contexts first (if any)
                restUtils.disposeContext();
                // Then tear down the APIManager
                apiManager.tearDown();
                System.out.println("API test completed.");
            } else {
                // Optional: if hybrid scenarios are used, dispose RESTUtils contexts first
                // restUtils.disposeContext();
                if (scenario.isFailed()) {
                    byte[] screenshot = browserManager.takeScreenshot();
                    scenario.attach(screenshot, "image/png", "📸 " + "screenshot");
                }
                browserManager.tearDown();
                System.out.println("Web test completed.");
            }
        } finally {
            // Always clean up the shared Playwright instance at the end of each scenario
            playwrightManager.cleanup();
        }
    }
}
