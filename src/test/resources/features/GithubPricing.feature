@github @regression
Feature: GitHub Pricing Functionality
  As a user
  I want to test GitHub pricing functionality
  So that I can verify pricing page access and content

  Scenario: Verify the pricing page - positive test
    Given I navigate to GitHub homepage
    When I click on Pricing link
    Then I should see the pricing page with title "Try the Copilot-powered platform"

  Scenario: Verify the pricing page - negative test
    Given I navigate to GitHub homepage
    When I click on Pricing link
    Then I should see the pricing page with title "Try the Copilot-powered platform - fail"

