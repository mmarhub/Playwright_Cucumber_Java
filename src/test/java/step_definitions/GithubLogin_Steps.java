package step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.GithubLoginPage;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubLogin_Steps {

    private final GithubLoginPage githubLoginPage;

    public GithubLogin_Steps(GithubLoginPage githubLoginPage) {
        this.githubLoginPage = githubLoginPage;
    }

    @Given("I navigate to GitHub homepage")
    public void iNavigateToGitHubHomepage() {
        // delegate to LoginPage which contains navigation logic
        githubLoginPage.navigateToHome();
    }

    @When("I click on Sign in link")
    public void iClickOnSignInLink() {
        githubLoginPage.clickSignIn();
    }

    @And("I enter username {string}")
    public void iEnterUsername(String username) {
        githubLoginPage.enterUsername(username);
        githubLoginPage.attachToReport("Entered username: " + username);
    }

    @And("I enter password {string}")
    public void iEnterPassword(String password) {
        githubLoginPage.enterPassword(password);
    }

    @And("I click on Sign in button")
    public void iClickOnSignInButton() {
        githubLoginPage.clickSignInButton();
    }

    @Then("I should see error message {string}")
    public void iShouldSeeErrorMessage(String errorMessage) {
        String alertText = githubLoginPage.getErrorMessage();
        assertThat(alertText.trim())
                .as("Error message validation")
                .isEqualTo(errorMessage.trim());
    }
}
