package pages;

import manager.BrowserManager;

public class HomePage extends BasePage {

    public HomePage(BrowserManager browserManager) {
        super(browserManager);
    }

    public void navigateToHomePage() {
        navigate("https://www.webdriveruniversity.com/");
    }

    public void clickContactUsButton() {
        getBrowserManager().setPage(getBrowserManager().getContext().waitForPage(() -> {
            waitAndClickByRole("LINK", "CONTACT US Contact Us Form");
        }));

        getBrowserManager().getPage().bringToFront();
    }

    public void clickLoginButton() {
        getBrowserManager().setPage(getBrowserManager().getContext().waitForPage(() -> {
            waitAndClickByRole("LINK", "LOGIN PORTAL Login Portal");
        }));

        getBrowserManager().getPage().bringToFront();
    }
}
