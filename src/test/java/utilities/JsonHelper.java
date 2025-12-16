package utilities;

import com.jayway.jsonpath.*;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonHelper {

    // Default configuration - suppresses exceptions for missing paths (returns null instead of throwing exception)
    private static final Configuration CONF = Configuration.builder()
            .options(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST)
            .build();

    private static DocumentContext yamlContext;

    // Method to load a yaml file content from yaml file and return it as HashMap<String, Object>
    public static HashMap<String, Object> loadYamlFile(String filePath) {
        Yaml yaml = new Yaml();
        try (InputStream in = JsonHelper.class.getClassLoader().getResourceAsStream(filePath)) {
            if (in == null) {
                throw new RuntimeException("Resource not found in classpath: " + filePath);
            }
            return yaml.load(in);  // Parses YAML into Map<String, Object> or List
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YAML file from classpath: " + filePath, e);
        }
    }

    /**
     * Get scenario data by top-level key (e.g., "Scenario1")
     * Returns as Map<String, Object> which preserves lists, strings, etc.
     */
    @SuppressWarnings("unchecked")
    public static Object getScenario(Object yamlContent, String scenarioKey) {
        if (yamlContent == null) {
            throw new RuntimeException("YAML content is null! Load YAML file first.");
        }

        if (yamlContent instanceof HashMap) {
            HashMap<String, Object> yamlMap = (HashMap<String, Object>) yamlContent;
            Object scenario = yamlMap.get(scenarioKey);
            if (scenario == null) {
                throw new RuntimeException("Scenario not found: " + scenarioKey);
            }
            return scenario;
        } else if (yamlContent instanceof ArrayList<?>) {
            ArrayList<Object> yamlList = (ArrayList<Object>) yamlContent;
            for (Object item : yamlList) {
                if (item instanceof HashMap) {
                    HashMap<String, Object> itemMap = (HashMap<String, Object>) item;
                    if (itemMap.containsKey(scenarioKey)) {
                        return itemMap.get(scenarioKey);
                    }
                }
            }
            throw new RuntimeException("Scenario not found in list: " + scenarioKey);
        } else {
            throw new RuntimeException("Invalid YAML content structure. Expected a Map at the top level.");
        }
    }

    /**
     * Extracts a value from JSON string using JsonPath query.
     * Supports deep nested paths, arrays, filters, etc.
     *
     * @param jsonString    The raw JSON response (e.g., from response.text())
     * @param jsonPathQuery JsonPath expression, e.g. "$.data.user.name" or "$['access_token']"
     * @param expectedType  Class of expected return type (String.class, Integer.class, List.class, etc.)
     * @param <T>           Generic return type
     * @return Extracted value or null if path not found (due to SUPPRESS_EXCEPTIONS)
     */
    public static <T> T extractValue(String jsonString, String jsonPathQuery, Class<T> expectedType) {
        // Basic validation
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        if (jsonPathQuery == null || jsonPathQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("JsonPath query cannot be null or empty");
        }

        // Perform extraction
        try {
            //return JsonPath.using(CONF).parse(jsonString).read(jsonPathQuery, expectedType);
            return JsonPath.parse(jsonString).read(jsonPathQuery, expectedType);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Failed to extract value using JsonPath '%s': %s",
                            jsonPathQuery,
                            e.getMessage()
                    ), e
            );
        }
    }

    /**
     * Convenience method for extracting String values (most common for tokens, IDs, etc.)
     */
    public static String extractString(String jsonString, String jsonPathQuery) {
        return extractValue(jsonString, jsonPathQuery, String.class);
    }

    /**
     * Extract as Integer (useful for expires_in, counts, etc.)
     */
    public static Integer extractInteger(String jsonString, String jsonPathQuery) {
        return extractValue(jsonString, jsonPathQuery, Integer.class);
    }

    /**
     * Extract as List (useful for arrays: $.items[*].id)
     */
    public static List<Object> extractList(String jsonString, String jsonPathQuery) {
        return extractValue(jsonString, jsonPathQuery, List.class);
    }

    // Write a method to set a new or update value in a JSON string given a JsonPath and return the updated JSON string
    public static String setValue(String jsonString, String jsonPathQuery, Object newValue) {
        // Basic validation
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        if (jsonPathQuery == null || jsonPathQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("JsonPath query cannot be null or empty");
        }

        try {
            DocumentContext ctx = JsonPath.using(CONF).parse(jsonString);
            ctx.set(jsonPathQuery, newValue);
            return ctx.jsonString();
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Failed to set value using JsonPath '%s': %s",
                            jsonPathQuery,
                            e.getMessage()),
                    e);
        }
    }

    public static CustomComparator customFieldsToIgnore(List<String> pathsToIgnore) {
        List<Customization> customizations = new ArrayList<>();

        for (String path : pathsToIgnore) {
            if (path != null && !path.trim().isEmpty()) {
                String cleanPath = path.trim();
                // JSONAssert supports exactly this format: root.field[0].subfield or root.array[*].field
                customizations.add(new Customization(cleanPath, (o1, o2) -> true));
            }
        }

        CustomComparator comparator = new CustomComparator(
                JSONCompareMode.LENIENT,  // or STRICT if you want exact match on other fields
                customizations.toArray(new Customization[0])
        );

        return comparator;
    }

    // Create a method
}
