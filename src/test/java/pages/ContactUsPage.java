package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import manager.BrowserManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;

public class ContactUsPage extends BasePage {

    public ContactUsPage(BrowserManager browserManager) {
        super(browserManager);
    }

    public void attachToReport(String message) {
        scenarioLog(message);
    }

    public void typeFirstName(String firstName) {
        fillField("First Name", firstName);
    }

    public void typeLastName(String lastName) {
        fillField("Last Name", lastName);
    }

    public void enterEmailAddress(String emailAddress) {
        fillField("Email Address", emailAddress);
    }

    public void typeComment(String comment) {
        fillField("Comments", comment);
    }

    public void clickSubmitButton() {
        waitAndClickBySelector("input[value='SUBMIT']");
    }

    public void verifySuccessfulSubmissionMessage() {
        getBrowserManager().getPage().waitForSelector("#contact_reply h1", new Page.WaitForSelectorOptions().setTimeout(10000));

        Locator locator = getBrowserManager().getPage().locator("#contact_reply h1");
        assertThat(locator).isVisible();
        assertThat(locator).hasText("Thank You for your Message!");
    }

    public void verifyUnsuccessfulSubmissionMessage() {
        //wait for the <body> element
        getBrowserManager().getPage().waitForSelector("body");

        //Locator of the body element
        Locator bodyElement = getBrowserManager().getPage().locator("body");

        // Extract text from the element
        String bodyText = bodyElement.textContent();

        // Assert that the body text matches the expected pattern
        Pattern pattern = Pattern.compile("Error: (all fields are required|Invalid email address)");
        Matcher matcher = pattern.matcher(bodyText);
        assertTrue(matcher.find(), "The body text does not match the expected error message. Found Text: " + bodyText);
    }

    public void verifyHeaderText(String message) {
        //Wait for the target element
        getBrowserManager().getPage().waitForSelector("//h1 | //body");

        // Get all elements' inner text
        List<String> texts = getBrowserManager().getPage().locator("//h1 | //body").allInnerTexts();

        //Variable to store the found text
        String foundText = "";

        // Check if any of the texts include the expected message
        boolean found = false;
        for (String text : texts) {
            if (text.contains(message)) {
                foundText = text;
                found = true;
                break;
            } else {
                foundText = text;
            }
        }

        //Perform an assertion
        assertTrue(found, "The element does not contain the expected message. Expected message: " +
                foundText + ", to be equal to: " + message);
    }
}
