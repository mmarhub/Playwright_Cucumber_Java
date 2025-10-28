package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import manager.BrowserManager;

public class BasePage {

    private final BrowserManager browserManager;

    public BasePage(BrowserManager browserManager) {
        this.browserManager = browserManager;
    }

    protected BrowserManager getBrowserManager() {
        return browserManager;
    }

    public void navigate(String url) {
        browserManager.getPage().navigate(url);
    }

    public void waitAndClickByRole(String role, String name) {
        Locator element = browserManager.getPage().getByRole(AriaRole.valueOf(role.toUpperCase()), new Page.GetByRoleOptions().setName(name));
        element.click();
    }

    public void waitAndClickBySelector(String selector) {
        browserManager.getPage().waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        browserManager.getPage().click(selector);
    }

    public void waitAndClick(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        locator.click();
    }

    public void fillField(String placeholder, String value) {
        getBrowserManager().getPage().getByPlaceholder(placeholder).fill(value);
    }

    public void fill(String locator, String value) {
        browserManager.getPage().locator(locator).fill(value);
    }

    public void click(String locator) {
        browserManager.getPage().locator(locator).click();
    }

    public String getText(String locator) {
        return browserManager.getPage().locator(locator).textContent();
    }

    // Attach message to the cucumber scenario html report
    public void scenarioLog(String message) {
        browserManager.getScenario().attach(
                "🔹 " + message,
                "text/plain",
                "Thread : " + Thread.currentThread().getName()
        );
    }

    public String textTrim(String locator) {
        String t = browserManager.getPage().locator(locator).textContent();
        return t == null ? null : t.trim();
    }

    // 'page.waitForSelector' method is deprecated, so do not use it.
    public void waitForSelector(String selector, int timeoutMs) {
        getBrowserManager().getPage().waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(timeoutMs));
    }

    public void waitForElementVisible(String selector, int timeoutMs) {
        browserManager.getPage().locator(selector).waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs)); // e.g; 10000 ms timeout
    }

    public boolean isElementVisible(String selector) {
        return browserManager.getPage().locator(selector).isVisible();
    }

    public void clickAndSwitchToChildTab(String selector) {
        browserManager.setPage(browserManager.getContext().waitForPage(() -> {
            browserManager.getPage().locator(selector).click();
        }));

        browserManager.getPage().bringToFront();
    }

    public void closeChildTabAndSwitchToParentTab() {
        Page currentPage = browserManager.getPage();
        currentPage.close();

        // Assuming the parent page is the first page in the context
        Page parentPage = browserManager.getContext().pages().getFirst();
        browserManager.setPage(parentPage);
        browserManager.getPage().bringToFront();
    }

    public void highlightElement(String selector) {
        browserManager.getPage().locator(selector).evaluate("el => {" +
                "el.style.outline = 'thick solid #FF0000'; " +  // Red border
                "el.style.boxShadow = '0 0 10px #FF0000'; " +   // Optional glow
                "el.scrollIntoView({block: 'center'});" +       // Ensure center view
                "}");
    }

    public void scrollToElement(String selector) {
        highlightElement(selector);
        browserManager.getPage().locator(selector).scrollIntoViewIfNeeded();
    }
}
