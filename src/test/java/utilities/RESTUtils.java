package utilities;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import manager.APIManager;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RESTUtils {

    private final APIManager apiManager;

    private APIRequestContext apiRequestContext;
    private APIResponse response;
    private String OAuthResourceURL;
    private String tranResourceURL;
    private String apiBodyPayload;
    private HashMap<String, String> apiCustomHeaders;

    public RESTUtils(APIManager apiManager) {
        this.apiManager = apiManager;
    }

    protected APIManager getApiManager() {
        return apiManager;
    }

    // Set up the OAuth Resource URL
    public void setupOAuthResourceURL(String resourcePath) {
        this.OAuthResourceURL = resourcePath;
    }

    // Set up the Transaction Resource URL
    public void setupTranResourceURL(String resourcePath) {
        this.tranResourceURL = resourcePath;
    }

    // Set the API request body payload
    public void setAPIBodyPayload(String payload) {
        this.apiBodyPayload = payload;
    }

    // Set custom headers for the API request
    public void setAPICustomHeaders(HashMap<String, String> headers) {
        this.apiCustomHeaders = headers;
    }

    // Initialize the OAuth API Request Context
    public void setupOAuthRequest() {
        this.apiRequestContext = apiManager.getApiRequest().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(apiManager.getOAuthBaseURL() + OAuthResourceURL)
                        .setTimeout(apiManager.getTimeout())
                        .setExtraHTTPHeaders(apiManager.getOAuthAPIHeaders())
        );
    }

    // Initialize the Transaction API Request Context
    public void setupTranRequest() {
        this.apiRequestContext = apiManager.getApiRequest().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(apiManager.getTranBaseURL() + tranResourceURL)
                        .setTimeout(apiManager.getTimeout())
                        .setExtraHTTPHeaders(apiManager.getTranAPIHeaders())
        );
    }

    // Get the current API Request Context
    public APIRequestContext getApiRequestContext() {
        if (apiRequestContext == null) {
            throw new IllegalStateException("API Request Context is not initialized. Call setupOAuthRequest() or setupTranRequest() first.");
        }
        return apiRequestContext;
    }

    // Get OAuth token from the OAuth API with client id and client secret
    public APIResponse getOAuthToken() {
        // Get the Client ID and Client Secret from YAML if not provided
        String clientId = apiManager.getOAuthSecrets().get("client_id").toString();
        String clientSecret = apiManager.getOAuthSecrets().get("client_secret").toString();

        // Convert the clientId and clientSecret to form data
        String basicAuth = "Basic " +
                Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        // Get the form data map from YAML
        //Map<String, Object> formMap = JsonUtils.getConfigContent("OAuthBodyForm");
        Map<String, Object> formMap = apiManager.getOAuthBodyForm();

        // Build FormData dynamically
        FormData formData = FormData.create();
        formMap.forEach((key, value) -> {
            // Convert value to String (handles String, int, boolean, etc.)
            formData.set(key, String.valueOf(value));
        });

        // Make the POST request to get the OAuth token
        response = apiRequestContext.post("",
                RequestOptions.create()
                        .setHeader("Authorization", basicAuth)
                        .setForm(formData)
        );

        // Check for successful response
        if (response.status() != 200) {
            throw new RuntimeException("Failed to get OAuth token. Status: "
                    + response.status() + " - " + response.statusText());
        }
        return response;
    }

    // Perform the POST request to the Transaction API
    public APIResponse postTransaction() {
        RequestOptions requestOptions = RequestOptions.create().setData(apiBodyPayload);
        //apiCustomHeaders.forEach((key, value) -> requestOptions.setHeader(key, value));
        // Above line can be replaced with the below line for simplicity
        apiCustomHeaders.forEach(requestOptions::setHeader);
        response = apiRequestContext.post("", requestOptions);
        return response;
    }

    // Perform the GET request to the Transaction API
    public APIResponse getTransaction() {
        RequestOptions requestOptions = RequestOptions.create();
        apiCustomHeaders.forEach(requestOptions::setHeader);
        response = apiRequestContext.get("", requestOptions);
        return response;
    }

    // Perform the PUT request to the Transaction API
    public APIResponse putTransaction() {
        RequestOptions requestOptions = RequestOptions.create().setData(apiBodyPayload);
        //apiCustomHeaders.forEach((key, value) -> requestOptions.setHeader(key, value));
        // Above line can be replaced with the below line for simplicity
        apiCustomHeaders.forEach(requestOptions::setHeader);
        response = apiRequestContext.put("", requestOptions);
        return response;
    }

    // Perform the PATCH request to the Transaction API
    public APIResponse patchTransaction() {
        RequestOptions requestOptions = RequestOptions.create().setData(apiBodyPayload);
        //apiCustomHeaders.forEach((key, value) -> requestOptions.setHeader(key, value));
        // Above line can be replaced with the below line for simplicity
        apiCustomHeaders.forEach(requestOptions::setHeader);
        response = apiRequestContext.patch("", requestOptions);
        return response;
    }

    // Perform the DELETE request to the Transaction API
    public APIResponse deleteTransaction() {
        RequestOptions requestOptions = RequestOptions.create();
        //apiCustomHeaders.forEach((key, value) -> requestOptions.setHeader(key, value));
        // Above line can be replaced with the below line for simplicity
        apiCustomHeaders.forEach(requestOptions::setHeader);
        response = apiRequestContext.delete("", requestOptions);
        return response;
    }

    // Get the API response status code
    public int getResponseStatusCode() {
        if (response == null) {
            throw new IllegalStateException("API response is not available. Make sure to perform an API call before getting the status code.");
        }
        return response.status();
    }

    // Get the API response status line
    public String getResponseStatusLine() {
        if (response == null) {
            throw new IllegalStateException("API response is not available. Make sure to perform an API call before getting the status line.");
        }
        return response.statusText();
    }

    // Get the API response body as a string
    public String getResponseBodyAsString() {
        if (response == null) {
            throw new IllegalStateException("API response is not available. Make sure to perform an API call before getting the response body.");
        }
        return response.text();
    }

    // Dispose the APIRequestContext to prevent memory leaks
    public void disposeContext() {
        if (apiRequestContext != null) {
            try {
                apiRequestContext.dispose();
            } catch (Exception e) {
                // Logging the exception instead of throwing and failing the test
                System.err.println("Error disposing APIRequestContext: " + e.getMessage());
            } finally {
                apiRequestContext = null;  // Ensure it's cleared
            }
        }
    }
}
