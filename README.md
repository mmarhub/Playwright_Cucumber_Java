# 🎭 Playwright BDD Test Automation Framework

## 📝 Project Description
This is a modern test automation framework that combines Playwright with Cucumber BDD for end-to-end testing. The framework is built using Java and incorporates best practices for web automation testing, including page object model design pattern and BDD approach.

## 🛠 Technologies Used
- Java 21
- Playwright (v1.55.0) - Modern browser automation library
- Cucumber (v7.30.0) - BDD test framework
- TestNG (v7.11.0) - Test execution framework
- AssertJ (v3.27.6) - Fluent assertions library
- Maven - Build and dependency management
- DataFaker - Test data generation
- PicoContainer - Dependency injection

## 🚨 Prerequisites
- Java JDK 21 or higher
- Maven 3.8 or higher
- Any IDE that supports Java (IntelliJ IDEA recommended)

## 🧰 Installation Steps
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

## 🧪 Running Tests

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

## 📂 Project Structure
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

## ⚙️ Configuration
The framework can be configured through `src/test/resources/properties/config.properties` file where you can set:
- Browser type
- Base URL
- Timeout values
- Environment specific configuration

## 📊 Reports
After test execution, you can find the reports in:
- Cucumber HTML reports: `target/cucumber-reports`
- TestNG reports: `target/surefire-reports`

---

<!-- API badge and header image -->

[![API Tests](https://img.shields.io/badge/API%20Tests-Playwright%20API-blue?style=for-the-badge)](#api-automation)

## API Automation

### 👀 Overview
This framework also includes a standalone API automation suite implemented with Playwright's APIRequest and Cucumber BDD. The API tests live alongside the web tests but can run independently. They follow the same project conventions (Cucumber features, step definitions, TestNG runner and Maven execution) so getting started is consistent with the web automation flow.

### 🚀 Quick Start — Run API Tests
Prerequisites: make sure Playwright (and its browsers) is installed (same install step used for web tests).

Install dependencies and Playwright (if not already done):

```bash
mvn clean install
```

Run API scenarios by Cucumber tag (if your API scenarios use tags like @api):

```bash
mvn clean test -Dcucumber.filter.tags="@api"
```

Run a single API scenario or scenario line (same approach as web features):

```bash
mvn clean test -Dcucumber.features="src/test/resources/features/<filename>.feature:10"
```

Tip: you can combine with other system properties used across the project (for example `-Dheadless=true`, `-Dthread.count=2`), though API tests are not browser-dependent.

### 🌐 Configuration & Environment
API-specific configuration is held in `src/test/resources/properties/restconfig_properties.yml` and the generic `src/test/resources/properties/config.properties` used across the framework.

Key config items you may need to update:
- Base API URL / host
- OAuth/client credentials (if used by scenarios)
- Default timeouts and retry settings

You can override some values at runtime using system properties when invoking Maven. For secrets or CI usage, prefer environment variables or your CI secret manager.

### 📬 Requests & Responses (Samples)
Sample request payloads and expected responses are stored as YAML for easy reuse and readability:

- Requests: `src/test/resources/apiRequests/ScenarioRequests.yml`
- Expected responses: `src/test/resources/apiResponses/ScenarioResponses.yml`

Feature file(s) driving the API scenarios:
- `src/test/resources/features/<filename>.feature`

### 📑 Reports & Artifacts
API test execution produces the same style of Cucumber reports used by the web suite. After a run you can find results here (same report location):

- JSON: `reports/cucumber-report/cucumber-pretty/cucumber.json`
- HTML: `reports/cucumber-report/cucumber-html-reports/` (open `overview-features.html` or relevant feature-level report)

If you run Maven locally, `target` may also contain generated test artifacts depending on your Maven/Cucumber plugin configuration.

---

## 📚 Additional Resources

- [Playwright Docs](https://playwright.dev)
- [Playwright API Testing](https://playwright.dev/docs/api-testing)
- [Cucumber Docs](https://cucumber.io/docs/cucumber/)

---

**Happy Testing! 🎉**
