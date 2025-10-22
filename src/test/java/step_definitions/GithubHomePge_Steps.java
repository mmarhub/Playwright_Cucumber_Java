package step_definitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.GithubHomePage;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubHomePge_Steps {

    private final GithubHomePage githubHomePage;

    public GithubHomePge_Steps(GithubHomePage githubHomePage) {
        this.githubHomePage = githubHomePage;
    }

    @When("I click on Pricing link")
    public void iClickOnPricingLink() {
        githubHomePage.clickPricingLink();
    }

    @Then("I should see the pricing page with title {string}")
    public void iShouldSeeThePricingPageWithTitle(String expTitle) {
        String title = githubHomePage.getPricingTitle();
        assertThat(title).isEqualTo(expTitle);
    }
}
