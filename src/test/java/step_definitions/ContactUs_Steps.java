package step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import manager.GlobalStorage;
import net.datafaker.Faker;
import pages.ContactUsPage;

public class ContactUs_Steps {

    private final Faker faker = new Faker();
    private final GlobalStorage globalStorage;
    private final ContactUsPage contactUsPage;

    // Passing BrowserManager, GlobalStorage, and ContactUsPage via constructor injection
    // This is handled by dependency injection frameworks like PicoContainer
    public ContactUs_Steps(GlobalStorage globalStorage, ContactUsPage contactUsPage) {
        this.globalStorage = globalStorage;
        this.contactUsPage = contactUsPage;
    }

    @And("I type a first name")
    public void i_type_a_first_name() {
        contactUsPage.typeFirstName("Joe");
    }

    @And("I type a last name")
    public void i_type_a_last_name() {
        contactUsPage.typeLastName("Blogs");
    }

    @And("I enter an email address")
    public void i_enter_an_email_address() {
        contactUsPage.enterEmailAddress("joe_blogs@example.com");
    }

    @And("I type a comment")
    public void i_type_a_comment() {
        contactUsPage.typeComment("Hello World!!");
    }

    @And("I click on the submit button")
    public void i_click_on_the_submit_button() {
        contactUsPage.clickSubmitButton();
    }

    @Then("I should be presented with a successful contact us submission message")
    public void i_should_be_presented_with_a_successful_contact_us_submission_message() {
        contactUsPage.verifySuccessfulSubmissionMessage();
    }

    @Then("I should be presented with a unsuccessful contact us submission message")
    public void i_should_be_presented_with_a_unsuccessful_contact_us_submission_message() {
        contactUsPage.verifyUnsuccessfulSubmissionMessage();
    }

    //Cucumber Expressions:
    @And("I type a specific first name {string}")
    public void i_type_a_specific_first_name(String firstName) {
        contactUsPage.typeFirstName(firstName);
    }

    @And("I type a specific last name {string}")
    public void i_type_a_specific_last_name(String lastName) {
        contactUsPage.typeLastName(lastName);
    }

    @And("I enter a specific email address {string}")
    public void i_enter_a_specific_email_address(String emailAddress) {
        contactUsPage.enterEmailAddress(emailAddress);
    }

    @And("I type specific text {string} and a number {int} within the comment input field")
    public void i_type_specific_text_and_a_number_within_the_comment_input_field(String word, Integer number) {
        contactUsPage.typeComment(word + " " + number);
    }

    //Random Data - Data Faker
    @And("I type a random first name")
    public void i_type_a_random_first_name() {
        String randomFirstName = faker.name().firstName();
        globalStorage.setRandomFirstName(randomFirstName);
        contactUsPage.typeFirstName(randomFirstName);
    }

    @And("I type a random last name")
    public void i_type_a_random_last_name() {
        String randomLastName = faker.name().lastName();
        globalStorage.setRandomLastName(randomLastName);
        contactUsPage.typeLastName(randomLastName);
    }

    @And("I enter a random email address")
    public void i_enter_a_random_email_address() {
        String randomEmail = faker.internet().emailAddress();
        globalStorage.setEmailAddress(randomEmail);
        contactUsPage.enterEmailAddress(randomEmail);
    }

    @And("I type a random comment")
    public void i_type_a_random_comment() {
        contactUsPage.typeComment("Hi, Please contact me. Regards, "
                + globalStorage.getRandomFirstName()
                + " " + globalStorage.getRandomLastName()
                + " " + globalStorage.getEmailAddress());
    }

    //Scenario outlines:
    @And("I type a first name {word} and a last name {word}")
    public void i_type_a_first_name_john_and_a_last_name_jones(String firstName, String lastName) {
        contactUsPage.typeFirstName(firstName);
        contactUsPage.typeLastName(lastName);
        contactUsPage.attachToReport("Typed first name: " + firstName + " and last name: " + lastName);
    }

    @And("I type a email address {string} and a comment {string}")
    public void i_type_a_email_address_and_a_comment(String email, String comment) {
        contactUsPage.enterEmailAddress(email);
        contactUsPage.typeComment(comment);
        contactUsPage.attachToReport("Typed email: " + email + " and comment: " + comment);
    }

    @Then("I should be presented with header text {string}")
    public void i_should_be_presented_with_header_text(String message) {
        contactUsPage.verifyHeaderText(message);
        contactUsPage.attachToReport("Message to be verified: " + message);
    }
}
