@github @regression
Feature: GitHub Login Functionality
  As a user
  I want to test GitHub login functionality
  So that I can verify invalid login attempts

  Scenario: Verify error message for invalid login credentials - positive test
    Given I navigate to GitHub homepage
    When I click on Sign in link
    And I enter username "positive@test.com"
    And I enter password "password123"
    And I click on Sign in button
    Then I should see error message "Incorrect username or password."

  Scenario: Verify error message for invalid login credentials - negative test
    Given I navigate to GitHub homepage
    When I click on Sign in link
    And I enter username "negative@test.com"
    And I enter password "password123"
    And I click on Sign in button
    Then I should see error message "Incorrect username or password. - fail"

  @switchtab
  Scenario: Open Discord tab and verify then return to main tab and verify Community
    Given I open the webpage "https://playwright.dev/"
    When I click the Discord icon and navigate to the new tab
    And I verify the title contains "Discord"
    And I close the new tab and switch back to the main tab
    When I click the "Community" menu link
    Then I verify the text "Welcome" is visible on the page
