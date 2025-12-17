package step_definitions;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.APIResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import manager.APIManager;
import manager.GlobalStorage;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import utilities.JsonHelper;
import utilities.RESTUtils;
import utilities.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class PaypalAPI_Steps {

    private final GlobalStorage globalStorage;
    private final APIManager apiManager;
    private final RESTUtils restUtils;

    private APIResponse apiResponse;
    private String OAuthAccessToken;

    public PaypalAPI_Steps(
            GlobalStorage globalStorage,
            APIManager apiManager,
            RESTUtils restUtils) {
        this.globalStorage = globalStorage;
        this.apiManager = apiManager;
        this.restUtils = restUtils;
    }

    @Given("I generate OAuth token with resource {string}")
    public void iGenerateOAuthTokenWithResource(String resourceURL) {
        // set up OAuth API request context and resource URL
        restUtils.setupOAuthResourceURL(resourceURL);
        restUtils.setupOAuthRequest();

        // Get the OAuth token from the API and validate the response status as 200 OK
        apiResponse = restUtils.getOAuthToken();
        assertThat(apiResponse.status()).isEqualTo(200);

        // Extract the OAuth token from the response and store it in GlobalStorage for later use
        String responseBody = apiResponse.text();
        OAuthAccessToken = JsonHelper.extractString(responseBody, "$.access_token");
        apiManager.getScenario().attach(
                "Generated OAuth Token: " + OAuthAccessToken,
                "text/plain",
                "🔑 OAuth Access Token");

        // Assuming the response is JSON and contains an "access_token" field
        /*try {
            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
            OAuthAccessToken = jsonNode.get("access_token").toString();
            apiManager.getScenario().attach(
                    OAuthAccessToken,
                    "text/plain",
                    "🔑 OAuth Access Token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OAuth token from response.", e);
        }*/
    }

    @Then("I form a client with this resource url {string}")
    public void iFormAClientWithThisResourceUrl(String resourceURL) {
        // set up transaction API request context and resource URL
        restUtils.setupTranResourceURL(resourceURL);
        restUtils.setupTranRequest();
        apiManager.getScenario().attach(
                "Formed API Client with Resource URL: " + resourceURL,
                "text/plain",
                "🛠️ API Client Setup");
    }

    @SuppressWarnings("unchecked")
    @Then("I get the {string} content from {string} file for the scenario {string}")
    public void iGetTheContentFromFileForTheScenario(String contentType, String fileName, String scenarioName) {
        String normalizedType = contentType.trim().toLowerCase();

        if (normalizedType.equalsIgnoreCase("request")) {
            fileName = "apiRequests/" + fileName + ".yml";
        } else if (normalizedType.equalsIgnoreCase("response")) {
            fileName = "apiResponses/" + fileName + ".yml";
        } else {
            throw new IllegalArgumentException("Invalid content type: " + contentType + ". Must be 'request' or 'response'.");
        }

        // Load the YAML file and parse the scenario data
        JsonUtils.loadYamlFile(fileName);

        // Retrieve the scenario object and convert it to Object
        Object scenarioObj = JsonUtils.getScenarioAsObject(scenarioName);
        JsonNode jsonContent;

        // Convert the scenario object to JsonNode based on its type
        if (scenarioObj instanceof Map) {
            jsonContent = JsonUtils.toJsonNode((Map<String, Object>) scenarioObj);
        } else if (scenarioObj instanceof List) {
            jsonContent = JsonUtils.toJsonNode((List<Object>) scenarioObj);
        } else {
            throw new IllegalArgumentException("Unsupported scenario data structure: " + scenarioObj.getClass().getName());
        }

        // Store the JsonNode content in GlobalStorage
        globalStorage.setInputReqResContent(jsonContent);
        apiManager.getScenario().attach(
                jsonContent.toPrettyString(),
                "application/json",
                "📄 Loaded " + contentType + " for Scenario " + scenarioName + " from file: " + fileName);
    }

    @Then("I make a {string} call with OAuth token and capture the response")
    public void iMakeACallWithOAuthTokenAndCaptureTheResponse(String httpMethod) {
        // If the httpMethod is POST or PUT or PATCH, then form the request body payload
        String method = httpMethod.trim().toUpperCase();
        if (method.equalsIgnoreCase("POST")
                || method.equalsIgnoreCase("PUT")
                || method.equalsIgnoreCase("PATCH")) {
            JsonNode inputReqResContent = globalStorage.getInputReqResContent();
            if (inputReqResContent == null) {
                throw new IllegalStateException("Request object not found in test data. " +
                        "Ensure that the request payload is loaded before making the API call.");
            }
            String requestBodyPayload = inputReqResContent.toPrettyString();
            restUtils.setAPIBodyPayload(requestBodyPayload);
        }

        // check if the OAuthAccessToken is available
        if (OAuthAccessToken == null || OAuthAccessToken.isEmpty()) {
            throw new IllegalStateException("OAuth Access Token is not available. " +
                    "Ensure that the token is generated before making the API call.");
        }

        // Set custom headers including the Bearer token for authorization
        HashMap<String, String> customHeaders = new HashMap<>();
        customHeaders.put("Authorization", "Bearer " + OAuthAccessToken);
        restUtils.setAPICustomHeaders(customHeaders);

        // Make the API call based on the HTTP method using if condition
        if (method.equalsIgnoreCase("GET")) {
            apiResponse = restUtils.getTransaction();
        } else if (method.equalsIgnoreCase("POST")) {
            apiResponse = restUtils.postTransaction();
        } else if (method.equalsIgnoreCase("PUT")) {
            apiResponse = restUtils.putTransaction();
        } else if (method.equalsIgnoreCase("DELETE")) {
            apiResponse = restUtils.deleteTransaction();
        } else if (method.equalsIgnoreCase("PATCH")) {
            apiResponse = restUtils.patchTransaction();
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }

        // Create one more custom logic for DELETE method to not expect any response body, if it does not have any
        if (method.equalsIgnoreCase("DELETE")) {
            apiManager.getScenario().attach(
                    "DELETE request made. No response body expected.",
                    "text/plain",
                    "📝 API Response");
        } else {
            String responseBody = apiResponse.text();
            // convert the response body to pretty JSON string for better readability in the report
            JsonNode prettyJsonResponse = JsonUtils.parseJsonString(responseBody);

            // Attach the response body to the Cucumber report for visibility
            apiManager.getScenario().attach(
                    prettyJsonResponse.toPrettyString(),
                    "application/json",
                    "📝 API Response");
        }
    }

    @Then("I should receive the HTTP status code in response as {int}")
    public void iShouldReceiveTheHTTPStatusCodeInResponseAs(int expectedStatusCode) {
        // Validate the response status code
        int actualStatusCode = restUtils.getResponseStatusCode();
        assertThat(actualStatusCode)
                .withFailMessage("Expected HTTP status code: %d but received: %d",
                        expectedStatusCode, actualStatusCode)
                .isEqualTo(expectedStatusCode);
        apiManager.getScenario().attach(
                "Validated HTTP Status Code: " + actualStatusCode,
                "text/plain",
                "✅ Status Code Validation"
        );
    }

    @Then("I extract value from response using json path {string} and store as {string}")
    public void iExtractValueFromResponseUsingJsonPathAndStoreAs(String jsonPath, String storeKey) {
        // Extract value from response using JsonPath
        String responseBody = restUtils.getResponseBodyAsString();
        String extractedValue = JsonHelper.extractString(responseBody, jsonPath);

        // Store the extracted value in GlobalStorage with the provided key
        globalStorage.setDataStorage(storeKey, extractedValue);

        apiManager.getScenario().attach(
                "Extracted value: " + extractedValue + " using JsonPath: " + jsonPath +
                        " and stored as key: " + storeKey,
                "text/plain",
                "🔍 Extracted Value");
    }

    @Then("I form a client by manipulating the resource url with {string}")
    public void iFormAClientByManipulatingTheResourceUrlWith(String resourceUrl) {
        // Manipulate the resource URL by replacing placeholders with stored values from GlobalStorage
        String manipulatedURL = resourceUrl;
        for (Map.Entry<String, String> entry : globalStorage.getDataStorage().entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            if (manipulatedURL.contains(placeholder)) {
                manipulatedURL = manipulatedURL.replace(placeholder, entry.getValue());
            }
        }

        // Optional: Check if the placeholder remains unreplaced
        if (manipulatedURL.contains("{") && manipulatedURL.contains("}")) {
            throw new RuntimeException("Unresolved placeholders in URL: " + manipulatedURL);
        }

        // Set up transaction API request context and manipulated resource URL
        restUtils.setupTranResourceURL(manipulatedURL);
        restUtils.setupTranRequest();
        apiManager.getScenario().attach(
                "Formed API Client with Manipulated Resource URL: " + manipulatedURL,
                "text/plain",
                "🛠️ Manipulated API Client Setup");
    }

    @And("I modify the request payload with below values for respective json paths:")
    public void iModifyTheRequestPayloadWithBelowValuesForRespectiveJsonPaths(DataTable dataTable) {
        // Get the current request payload from GlobalStorage
        JsonNode requestPayload = globalStorage.getInputReqResContent();
        if (requestPayload == null) {
            throw new IllegalStateException("Request payload not found in GlobalStorage. " +
                    "Ensure that the request payload is loaded before modification.");
        }

        // Iterate through each row in the DataTable to update the payload
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            String jsonPath = row.get("jsonPath");
            String newValue = row.get("value");

            // Replace <random> with 3-digit random number
            if (newValue.contains("<random>")) {
                int randomNum = (int) (Math.random() * 900) + 100; // Generates a random number between 100-999
                newValue = newValue.replace("<random>", String.valueOf(randomNum));
            }

            try {
                JsonUtils.updateStringAtPointer(requestPayload, jsonPath, newValue);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set value at JsonPath: " + jsonPath, e);
            }
        }

        // Convert the modified payload back to JsonNode and store it in GlobalStorage
        globalStorage.setInputReqResContent(requestPayload);

        apiManager.getScenario().attach(
                requestPayload.toPrettyString(),
                "application/json",
                "✏️ Modified Request Payload");
    }

    @Then("I form a JsonPath and verify the output object with ExpectedObject:")
    public void iFormAJsonPathAndVerifyTheOutputObjectWithExpectedObject(DataTable dataTable) {
        // Get the response body as string
        String responseBody = restUtils.getResponseBodyAsString();

        // Iterate through each row in the DataTable to verify the output object
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            String jsonPath = row.get("JsonPath");
            String expectedValue = row.get("ExpectedObject");

            // Extract the actual value from the response using JsonPath
            String actualValue = JsonHelper.extractString(responseBody, jsonPath);

            // Assert that the actual value matches the expected value
            assertThat(actualValue)
                    .withFailMessage("For JsonPath: %s, expected value: %s but found: %s",
                            jsonPath, expectedValue, actualValue)
                    .isEqualToIgnoringCase(expectedValue);

            apiManager.getScenario().attach(
                    "Verified JsonPath: " + jsonPath +
                            " | Expected: " + expectedValue +
                            " | Actual: " + actualValue,
                    "text/plain",
                    "✅ Verified Output Object");
        }
    }

    @Then("I validate the actual output response with expected api response")
    public void iValidateTheActualOutputResponseWithExpectedApiResponse() {
        // Get the actual response body as string
        String actualResponseBody = restUtils.getResponseBodyAsString();

        // Get the expected response JsonNode from GlobalStorage
        JsonNode expectedResponseNode = globalStorage.getInputReqResContent();
        if (expectedResponseNode == null) {
            throw new IllegalStateException("Expected response object not found in GlobalStorage. " +
                    "Ensure that the expected response is loaded before validation.");
        }
        String expectedResponseBody = expectedResponseNode.toPrettyString();

        // Use JSONAssert to compare actual and expected responses
        try {
            JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, false);
        } catch (Exception e) {
            throw new RuntimeException("Actual response does not match expected response.", e);
        }

        apiManager.getScenario().attach(
                "Validated actual response against expected response.",
                "text/plain",
                "✅ Response Validation");
    }

    @Then("I validate the actual output response with expected api response ignoring the below fields:")
    public void iValidateTheActualOutputResponseWithExpectedApiResponseIgnoringTheBelowFields(DataTable dataTable) {
        // Get the actual response body as string
        String actualResponseBody = restUtils.getResponseBodyAsString();

        // Get the expected response JsonNode from GlobalStorage
        JsonNode expectedResponseNode = globalStorage.getInputReqResContent();
        if (expectedResponseNode == null) {
            throw new IllegalStateException("Expected response object not found in GlobalStorage. " +
                    "Ensure that the expected response is loaded before validation.");
        }
        String expectedResponseBody = expectedResponseNode.toString();

        // Call the JsonHelper customFieldsToIgnore method by dataTable and getback the CustomComparator object to use in JSONAssert
        List<String> fieldsToIgnore = dataTable.asList(String.class);
        CustomComparator comparator = JsonHelper.customFieldsToIgnore(fieldsToIgnore);

        // Use JSONAssert to compare actual and expected responses with the custom comparator
        try {
            JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, comparator);
        } catch (Exception e) {
            throw new RuntimeException("Actual response does not match expected response when ignoring specified fields.", e);
        }

        apiManager.getScenario().attach(
                "Validated actual response against expected response while ignoring fields: " + fieldsToIgnore,
                "text/plain",
                "✅ Response Validation with Ignored Fields");
    }
}
