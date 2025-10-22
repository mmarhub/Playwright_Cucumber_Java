package step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import pages.LoginPage;

public class Login_Steps {

    private final LoginPage loginPage;

    public Login_Steps(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    @And("I type a username {word}")
    public void i_type_a_username(String username) {
        loginPage.enterUsername(username);
    }

    @And("I type a password {word}")
    public void i_type_a_password(String password) {
        loginPage.enterPassword(password);
    }

    @And("I click on the login button")
    public void i_click_on_the_login_button() {
        loginPage.clickLoginButton();
    }

    @Then("I should be presented with an alert box which contains text {string}")
    public void i_should_be_presented_with_an_alert_box_which_contains_text(String expectedAlertText) {
        loginPage.verifyAlertText(expectedAlertText);
    }
}
