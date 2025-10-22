@regression @contact-us
Feature: WebdriverUniversity.com - Contact Us Page

  Background: Pre Conditions
    Given I navigate to the webdriveruniversity homepage
    When I click on the contact us button

  Scenario: Valid Contact Us Form Submission
    And I type a first name
    And I type a last name
    And I enter an email address
    And I type a comment
    And I click on the submit button
    Then I should be presented with a successful contact us submission message

  Scenario: Invalid Contact Us Form Submission
    And I type a first name
    And I type a last name
    #And I enter an email address
    And I type a comment
    And I click on the submit button
    Then I should be presented with a unsuccessful contact us submission message

  Scenario: Valid Contact Us Form Submission - Using Specific Data
    And I type a specific first name "Sarah"
    And I type a specific last name "Woods"
    And I enter a specific email address "sarah_woods@example.com"
    And I type specific text "Hello world" and a number 2 within the comment input field
    And I click on the submit button
    Then I should be presented with a successful contact us submission message

  Scenario: Valid Contact Us Form Submission - Using Random Data
    And I type a random first name
    And I type a random last name
    And I enter a random email address
    #And I type a comment
    And I type a random comment
    And I click on the submit button
    Then I should be presented with a successful contact us submission message

  @smoke
  Scenario Outline: Validate Contact Us Page
    And I type a first name <firstName> and a last name <lastName>
    And I type a email address '<emailAddress>' and a comment '<comment>'
    And I click on the submit button
    Then I should be presented with header text '<message>'

    Examples:
      | firstName | lastName | emailAddress              | comment                 | message                             |
      | John      | Jones    | john_jones@example.com    | hello how are you?      | Thank You for your Message!         |
      | Mia       | Carter   | mia_carter123@example.com | Test123 Test321         | Thank You for your Message!         |
      | Grace     | Hudson   | grace hudson              | Do you create websites? | Error: Invalid email address - fail |
