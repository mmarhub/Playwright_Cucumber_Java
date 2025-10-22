package step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import manager.BrowserManager;
import pages.HomePage;

public class HomePage_Steps {

    public BrowserManager browserManager;
    private final HomePage homePage;

    // Passing BrowserManager and HomePage via constructor injection
    // This is compatible with dependency injection frameworks like PicoContainer
    public HomePage_Steps(
            BrowserManager browserManager,
            HomePage homePage
    ) {
        this.browserManager = browserManager;
        this.homePage = homePage;
    }

    @Given("I navigate to the webdriveruniversity homepage")
    public void i_navigate_to_the_webdriveruniversity_homepage() {
        homePage.navigateToHomePage();
    }

    @When("I click on the contact us button")
    public void i_click_on_the_contact_us_button() {
        homePage.clickContactUsButton();
    }

    @When("I click on the login portal button")
    public void i_click_on_the_login_portal_button() {
        homePage.clickLoginButton();
    }
}
