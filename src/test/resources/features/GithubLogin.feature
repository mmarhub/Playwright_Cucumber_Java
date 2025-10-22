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
