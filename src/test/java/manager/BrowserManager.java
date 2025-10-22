package manager;

import com.microsoft.playwright.*;
import io.cucumber.java.Scenario;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserManager {

    // Represents a Playwright instance. used to create browser instances of chromium, firefox, webkit.
    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();

    // Represents a browser instance. used to create browser contexts.
    private static final ThreadLocal<Browser> browser = new ThreadLocal<>();

    // Represents a browser context. used to create and manage multiple independent browser sessions.
    private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();

    // Represents a single tab or page within a browser context.
    private static final ThreadLocal<Page> page = new ThreadLocal<>();

    private static final ThreadLocal<Scenario> scenario = new ThreadLocal<>();

    public Properties properties;
    private static final Logger logger = Logger.getLogger(BrowserManager.class.getName());

    // Constructor to load properties from a configuration file.
    public BrowserManager() {
        properties = new Properties();
        // creates a path to a configuration file. If "config.path" isn't set,
        // it defaults to a file located in "src/main/resources/config.properties
        Path configPath = Paths.get(System.getProperty("config.path",
                Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "properties",
                        "config.properties").toString()));
        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load properties file!", e);
        }
    }

    // Getter for BrowserContext. Used in tests to get the current browser context.
    public BrowserContext getContext() {
        return context.get();
    }

    // Getter for Page. Used in tests to get the current page.
    public Page getPage() {
        return page.get();
    }

    // Setter for Page. Used in tests to set the current page.
    public void setPage(Page pg) {
        page.set(pg);
    }

    // Getter for Scenario. Used in tests to get the current scenario.
    public Scenario getScenario() {
        return scenario.get();
    }

    // Method to take a screenshot of the current page.
    public byte[] takeScreenshot() {
        if (page.get() != null) {
            return page.get().screenshot();
        }
        return new byte[0];
    }

    // Method to set up Playwright, browser, context, and page before each test.
    public void setUp(Scenario scn) {
        logger.info("Setting up Playwright...");

        // Get viewport size of screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        int slowMo = Integer.parseInt(System.getProperty("slowMo", "0"));

        try {
            playwright.set(Playwright.create());

            String browserType = properties.getProperty("browser", "chromium");
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(headless)
                    .setSlowMo(slowMo)
                    .setArgs(java.util.List.of("--no-sandbox"));

            switch (browserType.toLowerCase()) {
                case "chromium":
                    browser.set(playwright.get().chromium().launch(launchOptions));
                    break;
                case "firefox":
                    browser.set(playwright.get().firefox().launch(launchOptions));
                    break;
                case "webkit":
                    browser.set(playwright.get().webkit().launch(launchOptions));
                    break;
                default:
                    logger.warning("Unsupported browser type: " + browserType + ". Defaulting to chromium.");
                    browser.set(playwright.get().chromium().launch(launchOptions));
                    break;
            }
            context.set(browser.get().newContext(new Browser.NewContextOptions().setViewportSize(width, height)));
            //context.set(browser.get().newContext());
            page.set(context.get().newPage());

            // Set timeouts from properties file
            int navigationTimeout = Integer.parseInt(properties.getProperty("navigation.timeout", "30000"));
            int actionTimeout = Integer.parseInt(properties.getProperty("action.timeout", "15000"));
            page.get().setDefaultNavigationTimeout(navigationTimeout);
            page.get().setDefaultTimeout(actionTimeout);

            // Store the scenario variable for using anywhere in the test
            scenario.set(scn);

            logger.info("Playwright setup complete!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to set up Playwright.", e);
        }
    }

    // Method to tear down Playwright, browser, context, and page after each test.
    public void tearDown() {
        try {
            logger.info("Tearing down Playwright...");

            if (scenario.get() != null) scenario.remove();
            if (page.get() != null) page.get().close();
            if (context.get() != null) context.get().close();
            if (browser.get() != null) browser.get().close();
            if (playwright.get() != null) playwright.get().close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to close Playwright resources.", e);
        }

        logger.info("Playwright teardown complete!");
    }
}
