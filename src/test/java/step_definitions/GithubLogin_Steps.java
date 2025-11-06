package step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.GithubLoginPage;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubLogin_Steps {

    private final GithubLoginPage githubLoginPage;

    private String insVarMenuLink;

    public GithubLogin_Steps(GithubLoginPage githubLoginPage) {
        this.githubLoginPage = githubLoginPage;
    }

    @Given("I navigate to GitHub homepage")
    public void iNavigateToGitHubHomepage() {
        // delegate to LoginPage which contains navigation logic
        githubLoginPage.navigateToHome("https://github.com/");
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

    @Given("I open the webpage {string}")
    public void iOpenTheWebpage(String url) {
        githubLoginPage.navigateToHome(url);
    }

    @When("I click the Discord icon and navigate to the new tab")
    public void iClickTheDiscordIconAndNavigateToTheNewTab() {
        githubLoginPage.clickDiscordIcon();
    }

    @And("I verify the title contains {string}")
    public void iVerifyTheTitleContains(String arg0) {
        assertThat(githubLoginPage.isDiscordHomePageVisible())
                .as("Discord home page is not visible. Something went wrong.")
                .isTrue();
    }

    @And("I close the new tab and switch back to the main tab")
    public void iCloseTheNewTabAndSwitchBackToTheMainTab() {
        githubLoginPage.closeChildTabAndSwitchToParentTab();
    }

    @When("I click the {string} menu link")
    public void iClickTheMenuLink(String menuLink) {
        githubLoginPage.clickCommunityMenu();
    }

    @Then("I verify the text {string} is visible on the page")
    public void iVerifyTheTextIsVisibleOnThePage(String expText) {
        String titleText = githubLoginPage.getPWWelcomeText();
        assertThat(titleText)
                .as("PW Hero title text validation")
                .isEqualTo(expText);
    }

    @When("I scroll to the {string} button")
    public void iScrollToTheButton(String arg0) {
        githubLoginPage.isTrainingVideosLinkVisible();
    }

    @Then("I verify the {string} button is visible on the page")
    public void iVerifyTheButtonIsVisibleOnThePage(String arg0) {
        assertThat(githubLoginPage.isTrainingVideosLinkVisible())
                .as("Training Videos link is not visible on the page.")
                .isTrue();
    }

    @And("I perform keyboard actions to select all, cut, and paste the username into the password field")
    public void iPerformKeyboardActionsToSelectAllCutAndPasteTheUsernameIntoThePasswordField() {
        githubLoginPage.performKeyboardActionsOnLoginFields();
    }

    @Given("I navigate to the webpage {string}")
    public void iNavigateToTheWebpage(String url) {
        githubLoginPage.navigateToHome(url);
    }

    @When("I download the PDF file and verify the content contains the text {string}")
    public void iDownloadThePDFFileAndVerifyTheContentContainsTheText(String expectedContent) {
        assertThat(githubLoginPage.downloadAndValidatePDF(expectedContent))
                .withFailMessage("PDF content validation failed for expected content: " + expectedContent)
                .isTrue();
    }

    @When("I hover over the {string} menu link")
    public void iHoverOverTheMenuLink(String menuName) {
        insVarMenuLink = menuName;
        githubLoginPage.hoverOverElement(menuName);
    }

    @Then("I verify the {string} submenu is displayed")
    public void iVerifyTheSubmenuIsDisplayed(String subMenu) {
        assertThat(githubLoginPage.isSubMenuVisible(insVarMenuLink, subMenu))
                .withFailMessage("Submenu '" + subMenu + "' is not visible.")
                .isTrue();
    }

    @When("I hover over the Enterprise menu link")
    public void iHoverOverTheEnterpriseMenuLink() {
        githubLoginPage.hoverOverEnterpriseMenu();
    }

    @Then("I verify the Enterprise Platform submenu is displayed")
    public void iVerifyTheEnterprisePlatformSubmenuIsDisplayed() {
        assertThat(githubLoginPage.isEnterprisePlatformSubMenuVisible())
                .withFailMessage("Enterprise Platform submenu is not visible.")
                .isTrue();
    }
}
