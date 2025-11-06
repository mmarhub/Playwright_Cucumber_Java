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

  @scroll
  Scenario: ScrollintoView example
    Given I open the webpage "https://playwright.dev/"
    When I scroll to the "Playwright Training" button
    Then I verify the "Playwright Training" button is visible on the page

  # This scenario can be seen clearly when executed with 'mvn clean test -DslowMo=1000'
  @keyActions
  Scenario: Keyboard actions example
    Given I navigate to GitHub homepage
    When I click on Sign in link
    And I enter username "positive@test.com"
    And I enter password "password123"
    And I perform keyboard actions to select all, cut, and paste the username into the password field

  @pdfvalidation
  Scenario: Download and validate PDF content
    Given I navigate to the webpage "https://the-internet.herokuapp.com/download"
    When I download the PDF file and verify the content contains the text "eleifend velit vitae"

  @hover
  Scenario: Testing the hover function
    Given I navigate to GitHub homepage
    When I hover over the "Enterprise" menu link
    Then I verify the "Enterprise platform" submenu is displayed
    When I hover over the "Resources" menu link
    Then I verify the "DevOps" submenu is displayed
    When I hover over the Enterprise menu link
    Then I verify the Enterprise Platform submenu is displayed
