package pages;

import com.microsoft.playwright.Locator;
import manager.BrowserManager;

public class GithubLoginPage extends BasePage {

    private final String signInLink = "//div[contains(@class, 'HeaderMenu-link-wrap')]//a[@href='/login']";
    private final String usernameInput = "input[name='login']";
    private final String passwordInput = "input[name='password']";
    private final String signInButton = "input[name='commit']";
    private final String errorAlert = "div[id='js-flash-container'] div[role='alert']";
    private final String pwDiscordIcon = "a[aria-label='Discord server']";
    private final String discordSiteHomeIcon = "//header[contains(@class, 'wrapperDesktop')]//a[contains(@class, 'logoLink')]";
    private final String communityMenuLink = "//a[contains(text(), 'Community')]";
    private final String pwWelcomeTitle = "header h1";
    private final String pwTrainingVideosLink = "//li[@class='footer__item']/a[contains(@href, 'training')]";

    public GithubLoginPage(BrowserManager browserManager) {
        super(browserManager);
    }

    public void navigateToHome(String url) {
        navigate(url);
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

    public void clickDiscordIcon() {
        clickAndSwitchToChildTab(pwDiscordIcon);
    }

    public boolean isDiscordHomePageVisible() {
        waitForElementVisible(discordSiteHomeIcon, 10000);
        return isElementVisible(discordSiteHomeIcon);
    }

    public void clickCommunityMenu() {
        click(communityMenuLink);
    }

    public String getPWWelcomeText() {
        return textTrim(pwWelcomeTitle);
    }

    public boolean isTrainingVideosLinkVisible() {
        scrollToElement(pwTrainingVideosLink);
        return isElementVisible(pwTrainingVideosLink);
    }

    public void performKeyboardActionsOnLoginFields() {
        // Focus on username input field
        click(usernameInput);

        // check the os is Mac or Windows/Linux for paste shortcut
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            // Use Command (Meta) key for Mac
            getBrowserManager().getPage().keyboard().press("Meta+A");
            getBrowserManager().getPage().keyboard().press("Delete");
        } else {
            // Use Control key for Windows/Linux
            getBrowserManager().getPage().keyboard().press("Control+A");
            getBrowserManager().getPage().keyboard().press("Backspace");
        }

        // Type random username for checking the cut operation
        fill(usernameInput, "manual playwright");
    }
}
