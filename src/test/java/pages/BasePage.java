package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import manager.BrowserManager;
import com.microsoft.playwright.Download;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    // Get an identifier as an object (it could be either string or locator) and return as Locator
    public Locator getAsLocator(Object identifier) {
        Page page = browserManager.getPage();
        Locator locator;

        if (identifier instanceof String) {
            locator = page.locator((String) identifier);
        } else if (identifier instanceof Locator) {
            locator = (Locator) identifier;
        } else {
            throw new IllegalArgumentException("Identifier must be a String or Locator instance." +
                    " But got: " + identifier.getClass().getName());
        }

        waitForElementVisibleByLocator(locator);
        return locator;
    }

    public void fillField(String placeholder, String value) {
        getBrowserManager().getPage().getByPlaceholder(placeholder).fill(value);
    }

    public void fill(String locator, String value) {
        //browserManager.getPage().locator(locator).fill(value);
        getAsLocator(locator).fill(value);
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

    // Method with custom timeout
    public void waitForElementVisibleBySelector(String selector, double timeoutMs) {
        browserManager.getPage().locator(selector).waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
    }

    // Overloaded method with default timeout
    public void waitForElementVisibleBySelector(String selector) {
        waitForElementVisibleBySelector(selector, 30000);
    }

    // Method with custom timeout
    public void waitForElementVisibleByLocator(Locator locator, double timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
    }

    // Overloaded method with default timeout
    public void waitForElementVisibleByLocator(Locator locator) {
        waitForElementVisibleByLocator(locator, 30000);
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

    /**
     * Validate that the expected text exists inside a downloaded PDF file.
     * fileName should be the filename as saved in the downloadedFiles folder (e.g. "sample.pdf").
     * Returns true if expectedContent is found (case-insensitive); false otherwise.
     */
    public boolean validateContentInsidePDFFile(String fileName, String expectedContent) {
        Path downloadDir = Paths.get(System.getProperty("user.dir"),
                "src", "test", "resources", "downloadedFiles");
        Path filePath = downloadDir.resolve(fileName);

        if (!Files.exists(filePath)) {
            scenarioLog("File not found for validation: " + filePath);
            return false;
        }

        if (expectedContent == null || expectedContent.trim().isEmpty()) {
            scenarioLog("No expected content provided for validation");
            return false;
        }

        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text == null) text = "";

            boolean found = text.toLowerCase().contains(expectedContent.trim().toLowerCase());
            if (!found) {
                scenarioLog("Expected content not found in file: " + fileName);
            }
            return found;
        } catch (Exception e) {
            scenarioLog("Failed to read/validate file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Download the file by clicking the locator, then validate that expectedContent exists in the downloaded file.
     * Returns true only if the file is successfully downloaded and the content validation passes.
     */
    public boolean downloadAndValidate(String locator, String expectedContent) {
        try {

            Path downloadDir = Paths.get(System.getProperty("user.dir"),
                    "src", "test", "resources", "downloadedFiles");
            if (!Files.exists(downloadDir)) {
                Files.createDirectories(downloadDir);
            }

            final Page page = browserManager.getPage();
            Download download = page.waitForDownload(() -> page.locator(locator).click());

            String suggested = download.suggestedFilename();
            if (suggested == null || suggested.isEmpty()) {
                suggested = "downloaded-file-" + System.currentTimeMillis();
            }

            Path target = downloadDir.resolve(suggested);
            if (Files.exists(target)) {
                Files.delete(target);
            }

            download.saveAs(target);

            boolean exists = Files.exists(target) && Files.size(target) > 0;
            if (!exists) {
                scenarioLog("❌ - Download completed but file not found or empty: " + target);
                return false;
            }

            // Delegate to existing validator which reads the file and checks contents
            boolean validated = validateContentInsidePDFFile(suggested, expectedContent);
            if (!validated) {
                scenarioLog("❌ - Downloaded file found but content validation failed for: " + suggested);
            }

            scenarioLog("✅ - Download and validation succeeded for file: " + suggested);
            return exists && validated;
        } catch (Exception e) {
            scenarioLog("❌ - Download and validation failed due to exception: " + e.getMessage());
            return false;
        }
    }

    // Method to hover over element, by getting either String or Locator.
    public void hoverElement(Object element) {
        if (element instanceof String) {
            waitForElementVisibleBySelector((String) element);
            browserManager.getPage().locator((String) element).hover();
        } else if (element instanceof Locator) {
            waitForElementVisibleByLocator((Locator) element);
            ((Locator) element).hover();
        } else {
            throw new IllegalArgumentException("Element must be a String selector or Locator instance." +
                    ", but got: " + element.getClass().getName());
        }
    }
}
