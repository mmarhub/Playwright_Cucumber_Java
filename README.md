# Playwright BDD Test Automation Framework

## Project Description
This is a modern test automation framework that combines Playwright with Cucumber BDD for end-to-end testing. The framework is built using Java and incorporates best practices for web automation testing, including page object model design pattern and BDD approach.

## Technologies Used
- Java 21
- Playwright (v1.55.0) - Modern browser automation library
- Cucumber (v7.30.0) - BDD test framework
- TestNG (v7.11.0) - Test execution framework
- AssertJ (v3.27.6) - Fluent assertions library
- Maven - Build and dependency management
- DataFaker - Test data generation
- PicoContainer - Dependency injection

## Prerequisites
- Java JDK 21 or higher
- Maven 3.8 or higher
- Any IDE that supports Java (IntelliJ IDEA recommended)

## Installation Steps
1. Clone the repository
```bash
git clone <repository-url>
cd PW_BDD_PrEx
```

2. Install dependencies and Playwright browsers
```bash
mvn clean install
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

## Running Tests

### Run Complete Test Suite
```bash
mvn clean test
```

### Run with headless mode
To run tests in headless mode:
```bash
mvn clean test -Dheadless=true
```

### Run Specific Feature Tags
To run tests with specific tags:
```bash
mvn clean test -Dcucumber.filter.tags="@your-tag"
```

### Run with Different Browsers in Parallel
You can specify the browser in the config.properties file or pass it as a system property:
```bash
mvn clean test -Dbrowser=chromium
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=webkit
```

### Run with Custom Thread Count
To run tests in parallel with a specific thread count:
```bash
mvn clean test -Dthread.count=3
```

### Run Specific Feature Files
To run a specific feature file:
```bash
mvn clean test -Dcucumber.features="src/test/resources/features/Login.feature"
```

## Project Structure
```
src/
├── test/
    ├── java/
    │   ├── hooks/          # Cucumber hooks for setup/teardown
    │   ├── manager/        # Browser and test data management
    │   ├── pages/          # Page Object Model classes
    │   ├── runner/         # TestNG runner configuration
    │   ├── step_definitions/  # Cucumber step definitions
    │   └── utilities/      # Helper utilities
    └── resources/
        ├── features/       # Cucumber feature files
        └── properties/     # Configuration files
```

## Configuration
The framework can be configured through `src/test/resources/properties/config.properties` file where you can set:
- Browser type
- Base URL
- Timeout values
- Environment specific configuration

## Reports
After test execution, you can find the reports in:
- Cucumber HTML reports: `target/cucumber-reports`
- TestNG reports: `target/surefire-reports`
