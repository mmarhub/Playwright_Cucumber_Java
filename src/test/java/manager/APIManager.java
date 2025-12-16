package manager;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.Scenario;
import utilities.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class APIManager {

    // Dependency on PlaywrightManager to manage the Playwright instance.
    private final PlaywrightManager playwrightManager;

    // Represents a Playwright APIRequest for making API requests.
    private static final ThreadLocal<APIRequest> apiRequest = new ThreadLocal<>();

    // Represents the current Cucumber scenario for API tests.
    private static final ThreadLocal<Scenario> scenario = new ThreadLocal<>();

    // Represents a OAuthAPIHeaders from YAML file.
    //private Map<String, String> OAuthAPIHeaders = new HashMap<>();
    private static final ThreadLocal<Map<String, String>> OAuthAPIHeaders = ThreadLocal.withInitial(HashMap::new);

    // Represents a tranAPIHeaders from YAML file.
    private Map<String, String> tranAPIHeaders = new HashMap<>();

    // Represents a timeout from YAML file.
    private Double timeout;

    // Represents a Base from YAML file.
    private Map<String, Object> base = new HashMap<>();

    // Represents a OAuthSecrets from YAML file.
    private Map<String, Object> OAuthSecrets = new HashMap<>();

    // Represents a OAuthBodyForm from YAML file.
    private Map<String, Object> OAuthBodyForm = new HashMap<>();

    // Constructor with PlaywrightManager dependency injection.
    public APIManager(PlaywrightManager playwrightManager) {
        this.playwrightManager = playwrightManager;
    }

    // Setup method to initialize the API properties before each API test scenario.
    public void setUp(Scenario scn) {
        // Load API-related properties from a YAML file.
        JsonUtils.loadYamlFile("properties/restconfig_properties.yml");
        //OAuthAPIHeaders = JsonUtils.getHeaders("OAuthAPIHeaders");
        OAuthAPIHeaders.set(JsonUtils.getHeaders("OAuthAPIHeaders"));
        tranAPIHeaders = JsonUtils.getHeaders("tranAPIHeaders");
        base = JsonUtils.getConfigContent("Base");
        timeout = Double.valueOf(base.get("timeout").toString());
        OAuthSecrets = JsonUtils.getConfigContent("OAuthSecrets");
        OAuthBodyForm = JsonUtils.getConfigContent("OAuthBodyForm");

        // Set the shared Playwright instance from PlaywrightManager
        playwrightManager.initialize();
        Playwright pw = playwrightManager.getPlaywright();

        // Create a new APIRequest using Playwright and set it to the ThreadLocal variable
        apiRequest.set(pw.request());

        // Store the scenario variable for using anywhere in the test
        scenario.set(scn);
    }

    // Getter for APIRequest to be used in tests.
    public APIRequest getApiRequest() {
        return apiRequest.get();
    }

    // Setter for APIRequest to be used in tests.
    /*public void setApiRequest(APIRequest apiReq) {
        apiRequest.set(apiReq);
    }*/

    // Getter for Scenario. Used in tests to get the current scenario.
    public Scenario getScenario() {
        return scenario.get();
    }

    // Getter for OAuthAPIHeaders. Used in tests to get the OAuth API headers.
    /*public Map<String, String> getOAuthAPIHeaders() {
        return OAuthAPIHeaders;
    }*/
    public Map<String, String> getOAuthAPIHeaders() {
        return OAuthAPIHeaders.get();
    }

    // Getter for tranAPIHeaders. Used in tests to get the Transaction API headers.
    public Map<String, String> getTranAPIHeaders() {
        return tranAPIHeaders;
    }

    // Getter to get OAuthBaseURL from Base config.
    public String getOAuthBaseURL() {
        return base.get("OAuthBaseURL").toString();
    }

    // Getter to get tranBaseURL from Base config.
    public String getTranBaseURL() {
        return base.get("tranBaseURL").toString();
    }

    // Getter to get timeout value.
    public Double getTimeout() {
        return timeout;
    }

    // Getter to get OAuthSecrets map.
    public Map<String, Object> getOAuthSecrets() {
        return OAuthSecrets;
    }

    // Getter to get OAuthBodyForm map.
    public Map<String, Object> getOAuthBodyForm() {
        return OAuthBodyForm;
    }

    // Teardown method to clean up resources after each API test scenario.
    public void tearDown() {
        try {
            if (scenario.get() != null) {
                getScenario().attach(
                        "Cleaned up API resources.",
                        "text/plain",
                        "API Teardown"
                );
                scenario.remove();
            }

            // We don't dispose apiRequest – it's lightweight
            // Contexts created from it should be disposed by the caller (RESTUtils)
            if (apiRequest.get() != null) {
                apiRequest.remove();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during API teardown", e);
        }
    }
}
