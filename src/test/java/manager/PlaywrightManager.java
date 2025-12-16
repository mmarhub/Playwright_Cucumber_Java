package manager;

import com.microsoft.playwright.Playwright;

public class PlaywrightManager {

    // Represents a single Playwright instance, which can shared for both Web and API tests.
    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();

    // Initializes a new Playwright instance.
    public void initialize() {
        if (playwright.get() == null) {
            playwright.set(Playwright.create());
        }
    }

    // Getter for Playwright instance. Used in tests to get the current Playwright instance.
    public Playwright getPlaywright() {
        if (playwright.get() == null) {
            throw new IllegalStateException("Playwright instance is not initialized for this Thread." +
                    " Call initialize() first.");
        }
        return playwright.get();
    }

    // Closes and removes the Playwright instance for the current thread.
    public void cleanup() {
        try {
            if (playwright.get() != null) {
                playwright.get().close();
            }
        } finally {
            playwright.remove();
        }
    }
}
