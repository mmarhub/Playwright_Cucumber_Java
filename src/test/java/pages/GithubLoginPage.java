package pages;

import com.microsoft.playwright.Locator;
import manager.BrowserManager;

public class GithubLoginPage extends BasePage {

    private final String signInLink = "//div[contains(@class, 'HeaderMenu-link-wrap')]//a[@href='/login']";
    private final String usernameInput = "input[name='login']";
    private final String passwordInput = "input[name='password']";
    private final String signInButton = "input[name='commit']";
    private final String errorAlert = "div[id='js-flash-container'] div[role='alert']";

    public GithubLoginPage(BrowserManager browserManager) {
        super(browserManager);
    }

    public void navigateToHome() {
        navigate("https://github.com/");
    }

    public void clickSignIn() {
        waitAndClickBySelector(signInLink);
        //click(signInLink);
    }

    public void enterUsername(String username) {
        fill(usernameInput, username);
    }

    public void enterPassword(String password) {
        fill(passwordInput, password);
    }

    public void clickSignInButton() {
        click(signInButton);
    }

    public void attachToReport(String message) {
        scenarioLog(message);
    }

    public String getErrorMessage() {
        // Use Locator to access the alert div
        Locator alertDiv = getBrowserManager().getPage().locator(errorAlert);

        // Evaluate JS to get only the direct text content (exclude child nodes)
        return alertDiv.evaluate("el => { " +
                "  let text = '';" +
                "  for (let node of el.childNodes) {" +
                "    if (node.nodeType === Node.TEXT_NODE) {" +
                "      text += node.textContent.trim();" +
                "    }" +
                "  }" +
                "  return text;" +
                "}").toString();

        //return textTrim(errorAlert);
    }
}
