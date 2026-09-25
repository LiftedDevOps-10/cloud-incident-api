package com.lifted.incident;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String TABLE_NAME = "cloud-incidents";

    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper;

    public LambdaHandler() {
        this.dynamoDbClient = DynamoDbClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Map<String, Object> handleRequest(
            Map<String, Object> input,
            Context context) {

       System.out.println("Lambda request received");

       try {

            String method = getHttpMethod(input);
            String path = getPath(input);

            if ("GET".equalsIgnoreCase(method) && "/health".equals(path)) {
                return healthResponse();
            }

            if ("POST".equalsIgnoreCase(method)
                    && "/incidents".equals(path)) {
                return createIncident(input);
            }

            if ("GET".equalsIgnoreCase(method)
                    && "/incidents".equals(path)) {
                return listIncidents();
            }

            if ("GET".equalsIgnoreCase(method)
                    && path.startsWith("/incidents/")) {

                String incidentId = path.substring("/incidents/".length());

                return getIncident(incidentId);
            }

            if ("PUT".equalsIgnoreCase(method)
                    && path.startsWith("/incidents/")) {

                String incidentId = path.substring("/incidents/".length());

                return updateIncident(incidentId, input);
            }

            if ("DELETE".equalsIgnoreCase(method)
                    && path.startsWith("/incidents/")) {

                String incidentId = path.substring("/incidents/".length());

                return deleteIncident(incidentId);
            }

           return response(
                    404,
                    "{\"message\":\"Route not found\"}"
            );

        } catch (Exception e) {

            if (context != null) {
                context.getLogger().log(
                        "ERROR: " + e.getMessage()
                );
            }

            return response(
                    500,
                    "{\"message\":\"Internal server error\"}"
            );
        }
    }

    private String getHttpMethod(Map<String, Object> input) {

        Object requestContextObject = input.get("requestContext");

        if (requestContextObject instanceof Map<?, ?> requestContext) {

            Object httpObject = requestContext.get("http");

            if (httpObject instanceof Map<?, ?> http) {

                Object method = http.get("method");

                if (method != null) {
                    return method.toString();
                }
            }
        }

        Object method = input.get("httpMethod");

        return method != null ? method.toString() : "";
    }

    private String getPath(Map<String, Object> input) {

        Object rawPath = input.get("rawPath");

        if (rawPath != null) {
            return rawPath.toString();
        }

        Object path = input.get("path");

        return path != null ? path.toString() : "";
    }

    private Map<String, Object> healthResponse() {

        String body = """
                {
                    "service": "cloud-incident-api",
                    "runtime": "Java 21",
                    "message": "Cloud Incident API is running!",
                    "status": "healthy"
                }
                """;

        return response(200, body);
    }

    private Map<String, Object> createIncident(
            Map<String, Object> input) throws Exception {

        String body = (String) input.get("body");

        if (body == null || body.isBlank()) {

            return response(
                    400,
                    "{\"message\":\"Request body is required\"}"
            );
        }

        JsonNode json = objectMapper.readTree(body);

        String title = getJsonValue(json, "title");
        String severity = getJsonValue(json, "severity");
        String status = getJsonValue(json, "status");
        String description = getJsonValue(json, "description");

        if (title == null || severity == null || status == null) {

            return response(
                    400,
                    "{\"message\":\"title, severity and status are required\"}"
            );
        }

        String incidentId =
                "INC-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        Map<String, AttributeValue> item = new HashMap<>();

        item.put(
                "incidentId",
                AttributeValue.builder()
                        .s(incidentId)
                        .build()
        );

        item.put(
                "title",
                AttributeValue.builder()
                        .s(title)
                        .build()
        );

        item.put(
                "severity",
                AttributeValue.builder()
                        .s(severity)
                        .build()
        );

        item.put(
                "status",
                AttributeValue.builder()
                        .s(status)
                        .build()
        );

        if (description != null) {

            item.put(
                    "description",
                    AttributeValue.builder()
                            .s(description)
                            .build()
            );
        }

        dynamoDbClient.putItem(
                PutItemRequest.builder()
                        .tableName(TABLE_NAME)
                        .item(item)
                        .build()
        );

        String responseBody = objectMapper.writeValueAsString(
                Map.of(
                        "message", "Incident created successfully",
                        "incidentId", incidentId
                )
        );

        return response(201, responseBody);
    }

    private Map<String, Object> listIncidents()
            throws Exception {

        var result = dynamoDbClient.scan(
                ScanRequest.builder()
                        .tableName(TABLE_NAME)
                        .build()
        );

        var incidents = result.items()
                .stream()
                .map(this::convertDynamoItem)
                .toList();

        String body = objectMapper.writeValueAsString(
                Map.of(
                        "count", incidents.size(),
                        "incidents", incidents
                )
        );

        return response(200, body);
    }

    private Map<String, Object> getIncident(
            String incidentId) throws Exception {

        var result = dynamoDbClient.getItem(
                GetItemRequest.builder()
                        .tableName(TABLE_NAME)
                        .key(
                                Map.of(
                                        "incidentId",
                                        AttributeValue.builder()
                                                .s(incidentId)
                                                .build()
                                )
                        )
                        .build()
        );

        if (!result.hasItem()) {

            return response(
                    404,
                    "{\"message\":\"Incident not found\"}"
            );
        }

        Map<String, String> incident =
                convertDynamoItem(result.item());

        String body =
                objectMapper.writeValueAsString(incident);

        return response(200, body);
    }

    private Map<String, Object> updateIncident(
            String incidentId,
            Map<String, Object> input) throws Exception {

        String body = (String) input.get("body");

        if (body == null || body.isBlank()) {

            return response(
                    400,
                    "{\"message\":\"Request body is required\"}"
            );
        }

        JsonNode json = objectMapper.readTree(body);

        String title = getJsonValue(json, "title");
        String severity = getJsonValue(json, "severity");
        String status = getJsonValue(json, "status");
        String description = getJsonValue(json, "description");

        if (title == null
                && severity == null
                && status == null
                && description == null) {

            return response(
                    400,
                    "{\"message\":\"At least one field is required\"}"
            );
        }

        Map<String, AttributeValue> values = new HashMap<>();
        Map<String, String> names = new HashMap<>();

        StringBuilder updateExpression =
                new StringBuilder("SET ");

        boolean first = true;

        if (title != null) {

            updateExpression.append("#title = :title");

            names.put("#title", "title");

            values.put(
                    ":title",
                    AttributeValue.builder()
                            .s(title)
                            .build()
            );

            first = false;
        }

        if (severity != null) {

            if (!first) {
                updateExpression.append(", ");
            }

            updateExpression.append("#severity = :severity");

            names.put("#severity", "severity");

            values.put(
                    ":severity",
                    AttributeValue.builder()
                            .s(severity)
                            .build()
            );

            first = false;
        }

        if (status != null) {

            if (!first) {
                updateExpression.append(", ");
            }

            updateExpression.append("#status = :status");

            names.put("#status", "status");

            values.put(
                    ":status",
                    AttributeValue.builder()
                            .s(status)
                            .build()
            );

            first = false;
       }

       if (description != null) {

           if (!first) {
               updateExpression.append(", ");
           }

           updateExpression.append("#description = :description");

           names.put("#description", "description");

           values.put(
                   ":description",
                   AttributeValue.builder()
                           .s(description)
                           .build()
           );
        }

        var result = dynamoDbClient.updateItem(
                UpdateItemRequest.builder()
                        .tableName(TABLE_NAME)
                        .key(
                                Map.of(
                                        "incidentId",
                                        AttributeValue.builder()
                                                .s(incidentId)
                                                .build()
                                )
                        )
                        .updateExpression(
                                updateExpression.toString()
                        )
                        .expressionAttributeNames(names)
                        .expressionAttributeValues(values)
                        .conditionExpression(
                             "attribute_exists(incidentId)"
                        )
                        .returnValues("ALL_NEW")
                        .build()
        );

        Map<String, String> updated =
                convertDynamoItem(result.attributes());

        String responseBody =
                objectMapper.writeValueAsString(updated);

        return response(200, responseBody);

    }

    private Map<String, Object> deleteIncident(
            String incidentId) throws Exception {

        var result = dynamoDbClient.deleteItem(
                DeleteItemRequest.builder()
                        .tableName(TABLE_NAME)
                        .key(
                                Map.of(
                                         "incidentId",
                                         AttributeValue.builder()
                                                 .s(incidentId)
                                                 .build()
                                )
                        )
                        .returnValues("ALL_OLD")
                        .build()
        );

        if (!result.attributes().isEmpty()) {

           Map<String, String> deleted =
                   convertDynamoItem(result.attributes());

           String body =
                   objectMapper.writeValueAsString(
                           Map.of(
                                   "message",
                                   "Incident deleted successfully",
                                   "incident",
                                   deleted
                           )
                   );

           return response(200, body);
        }

        return response(
                404,
                "{\"message\":\"Incident not found\"}"
        );
    }

    private Map<String, String> convertDynamoItem(
            Map<String, AttributeValue> item) {

        Map<String, String> result = new HashMap<>();

        item.forEach((key, value) -> {

            if (value.s() != null) {
                result.put(key, value.s());
            }
        });

        return result;
    }

    private String getJsonValue(
            JsonNode json,
            String field) {

        JsonNode value = json.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        return value.asText();
    }

    private Map<String, Object> response(
            int statusCode,
            String body) {

        Map<String, Object> response = new HashMap<>();

        response.put("statusCode", statusCode);

        Map<String, String> headers = new HashMap<>();

        headers.put(
                "Content-Type",
                "application/json"
        );

        response.put("headers", headers);
        response.put("body", body);

        return response;
    }
}
