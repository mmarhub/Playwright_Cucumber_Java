package pages;

import com.microsoft.playwright.Locator;
import manager.BrowserManager;
import org.testng.Assert;

public class LoginPage extends BasePage {

    private String alertText;

    public LoginPage(BrowserManager browserManager) {
        super(browserManager);
    }

    public void enterUsername(String username) {
        fillField("Username", username);
    }

    public void enterPassword(String password) {
        fillField("Password", password);
    }

    public void clickLoginButton() {
        getBrowserManager().getPage().onceDialog(dialog -> {
            alertText = dialog.message();
            dialog.accept();
        });

        Locator loginButton = getBrowserManager().getPage().locator("#login-button");
        waitAndClick(loginButton);
    }

    public void verifyAlertText(String expectedAlertText) {
        Assert.assertEquals(alertText, expectedAlertText, "The alert text does not match the expected text");
    }
}
