package utilities;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to load YAML scenarios and convert them to JSON objects/strings.
 * Uses Jackson (best practice in Java ecosystem).
 * All methods are static → easy to call from Step Definitions or anywhere.
 */
public class JsonUtils {

    // Jackson ObjectMappers for YAML and JSON
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    // Holds the entire YAML content after loading (key → scenario data)
    private static Map<String, Object> loadedScenarios;

    /**
     * Load the YAML file once (call this in @Before hook or at startup)
     */
    public static void loadYamlFile(String filePath) {
        try {
            InputStream inputStream = JsonUtils.class.getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new RuntimeException("YAML file not found: " + filePath);
            }

            loadedScenarios = YAML_MAPPER.readValue(inputStream, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YAML file from classpath: " + filePath, e);
        }
    }

    /**
     * Get scenario data by top-level key (e.g., "Scenario1")
     * Returns as Map<String, Object> which preserves lists, strings, etc.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getScenario(String scenarioKey) {
        if (loadedScenarios == null) {
            throw new RuntimeException("YAML not loaded! Call loadYamlFile() first.");
        }

        Map<String, Object> scenario = (Map<String, Object>) loadedScenarios.get(scenarioKey);
        if (scenario == null) {
            throw new RuntimeException("Scenario not found: " + scenarioKey);
        }
        return scenario;
    }

    /**
     * Get the scenario data by top-level key (e.g., "Scenario1")
     * and return as Object (for flexibility)
     */
    public static Object getScenarioAsObject(String scenarioKey) {
        if (loadedScenarios == null) {
            throw new RuntimeException("YAML not loaded! Call loadYamlFile() first.");
        }
        Object scenario = loadedScenarios.get(scenarioKey);
        if (scenario == null) {
            throw new RuntimeException("Scenario not found: " + scenarioKey);
        }
        return scenario;
    }

    /**
     * Get config data by top-level key (e.g., "Base")
     * Returns as Map<String, Object> which preserves lists, strings, etc.
     */
    public static Map<String, Object> getConfigContent(String configKey) {
        return getScenario(configKey);
    }

    /**
     * Convert any Java object (like the Map from getScenario) to Jackson JsonNode (recommended)
     * JsonNode is Jackson's equivalent of JSONObject – very powerful and type-safe
     */
    public static JsonNode toJsonNode(Object javaObject) {
        return JSON_MAPPER.valueToTree(javaObject);
    }

    /**
     * Convert Java object (Map/List/etc.) directly to pretty JSON string
     */
    public static String toJsonString(Object javaObject) {
        try {
            return JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(javaObject);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert to JSON string", e);
        }
    }

    /**
     * Convert JSON string → JsonNode
     */
    public static JsonNode parseJsonString(String json) {
        try {
            return JSON_MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON string", e);
        }
    }

    /**
     * Convert JSON string → Map<String, Object> (if you prefer Map over JsonNode)
     */
    public static Map<String, Object> jsonStringToMap(String json) {
        try {
            return JSON_MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON string to Map", e);
        }
    }

    /**
     * Get headers from config as Map<String, String>
     */
    public static Map<String, String> getHeaders(String key) {
        Map<String, Object> raw = getConfigContent(key);
        return raw.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.valueOf(e.getValue())
                ));
    }

    /**
     * Update a string value at the specified JSON Pointer path within the given JsonNode.
     *
     * @param root       The root JsonNode to update. (e.g., the entire JSON object)
     * @param pointerStr The JSON Pointer string (e.g., "/data/user/name").
     * @param newValue   The new string value to set. (e.g., "newName")
     */
    public static void updateStringAtPointer(JsonNode root, String pointerStr, String newValue) {
        JsonPointer pointer = JsonPointer.compile(pointerStr);

        // Get the parent path (everything except the last segment)
        JsonPointer parentPointer = pointer.head();
        JsonNode parentNode = root.at(parentPointer);

        if (parentNode.isMissingNode()) {
            throw new IllegalArgumentException("Parent path not found: " + parentPointer);
        }

        // Get the last part of the path (field name or array index)
        String lastSegment = pointer.last().toString(); // e.g., "url" or "0" or "name"

        // Update the value based on whether parent is an object or array
        if (parentNode.isObject()) {
            ObjectNode objectParent = (ObjectNode) parentNode;
            // Remove leading '/' if present (for field names)
            String fieldName = lastSegment.startsWith("/") ? lastSegment.substring(1) : lastSegment;
            objectParent.put(fieldName, newValue);
        } else if (parentNode.isArray()) {
            ArrayNode arrayParent = (ArrayNode) parentNode;
            try {
                int index = Integer.parseInt(lastSegment);
                arrayParent.set(index, JSON_MAPPER.getNodeFactory().textNode(newValue));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Expected array index, got: " + lastSegment);
            }
        } else {
            throw new IllegalArgumentException("Parent is neither object nor array");
        }
    }
}

